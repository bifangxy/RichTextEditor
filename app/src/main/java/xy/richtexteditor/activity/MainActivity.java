package xy.richtexteditor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import xy.richtexteditor.R;
import xy.richtexteditor.data.ResponseData;
import xy.richtexteditor.data.Url;
import xy.richtexteditor.dialog.LinkDialog;
import xy.richtexteditor.dialog.PictureDialog;
import xy.richtexteditor.theme.BaseTheme;
import xy.richtexteditor.theme.DarkTheme;
import xy.richtexteditor.theme.LightTheme;
import xy.richtexteditor.utils.RealPathFromUriUtils;
import xy.richtexteditor.utils.SizeUtils;
import xy.richtexteditor.utils.Utils;
import xy.richtexteditor.view.bottommenu.BottomMenu;
import xy.richtexteditor.view.bottommenu.IBottomMenuItem;
import xy.richtexteditor.view.bottommenu.ImageViewButtonItem;
import xy.richtexteditor.view.bottommenu.MenuItem;
import xy.richtexteditor.view.bottommenu.MenuItemFactory;
import xy.richtexteditor.view.richeditor.ItemIndex;
import xy.richtexteditor.view.richeditor.MyRichEditor;
import xy.richtexteditor.view.richeditor.RichEditor;

/**
 * Created by Xieying on 2018/3/30
 * Function:MainActivity
 */
public class MainActivity extends AppCompatActivity implements MyRichEditor.OnEditorClickListener, View.OnClickListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_IMAGE = 0x101;

    private static final int REQUEST_CAMERA_PERMISSION = 0x102;

    private static final int REQUEST_WRITE_PERMISSION = 0x103;

    private static final int REQUEST_CAMERA_AND_WRITE_PERMISSION = 0x104;

    private static final String UPLOAD_IMAGE_URL = "http://47.52.227.43/zhuwai365/api/v2/public/api.php/Ajax/uploadpic";

    private static final String PARME_NAME = "file";

    private Context mContext;

    private LinearLayout ll_title_bar;
    private ImageView iv_return;
    private TextView tv_count;
    private TextView tv_submit;
    private MyRichEditor mRichEditor;
    private BottomMenu mBottomMenu;

    private HashMap<Long, String> mInsertedImages;

    private HashMap<Long, String> mFailedImages;

    private HashMap<String, String> mPathAndUri;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_count.setText(msg.arg1 + "字");
                    break;
                case 3:
                    String htmlContent = mRichEditor.getHtml();
                    Log.d(LOG_TAG, htmlContent);
                    Iterator iter = mPathAndUri.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String) entry.getKey();
                        String val = (String) entry.getValue();
                        htmlContent = htmlContent.replace(key, val);
                    }
                    String strBase64 = Utils.Base64encode(htmlContent);
                    submitArticle(strBase64, mRichEditor.getHtmlTitle());
                    break;
            }
            return false;
        }
    });


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
        initListener();

    }

    private void initView() {
        ll_title_bar = findViewById(R.id.ll_title_bar);
        iv_return = findViewById(R.id.iv_return);
        tv_count = findViewById(R.id.tv_count);
        tv_submit = findViewById(R.id.tv_submit);
        mRichEditor = findViewById(R.id.rich_editor);
        mBottomMenu = findViewById(R.id.bottom_menu);

    }

    @SuppressLint("UseSparseArrays")
    private void initData() {
        mInsertedImages = new HashMap<>();
        mFailedImages = new HashMap<>();
        mPathAndUri = new HashMap<>();
        mRichEditor.setBottomMenu(mBottomMenu);
        mHandler.sendEmptyMessageDelayed(2, 1000);
    }

    private void initListener() {
        mRichEditor.setOnEditorClickListener(this);
        mRichEditor.setOnTextLengthChangeListener(new RichEditor.OnTextLengthChangeListener() {
            @Override
            public void onTextLengthChange(final long length) {

                Message message = new Message();
                message.what = 1;
                message.arg1 = (int) length;
                mHandler.sendMessage(message);
            }
        });
        iv_return.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

    }

    private void showImagePicker() {

        Matisse.from(MainActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "xy.richtexteditor.fileprovider"))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_IMAGE);
    }

    private void showLinkDialog(LinkDialog linkDialog, final boolean isChange) {
        linkDialog.setOnDialogClickListener(new LinkDialog.OnDialogClickListener() {
            @Override
            public void cancel() {
            }

            @Override
            public void confirm(String linkName, String linkUrl) {
                if (TextUtils.isEmpty(linkName) || TextUtils.isEmpty(linkUrl))
                    Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_SHORT).show();
                else {
                    if (!isChange)
                        mRichEditor.insertLink(linkUrl, linkName);
                    else
                        mRichEditor.changeLink(linkUrl, linkName);
                }

            }
        });
        linkDialog.show();

    }

    private void showPictureClickDialog(final PictureDialog dialog, CharSequence[] items) {

        dialog.setOnDialogClickListener(new PictureDialog.OnDialogClickListener() {
            @Override
            public void delete(Long id) {
                mRichEditor.deleteImageById(id);
                removeFromLocalCache(id);

            }

            @Override
            public void reload(Long id) {
                mRichEditor.setImageReload(id);
                uploadFile(mFailedImages.get(id), id);
                mInsertedImages.put(id, mFailedImages.get(id));
                mFailedImages.remove(id);
            }
        });

        dialog.setItems(items);
        dialog.show(getSupportFragmentManager(), PictureDialog.LOG_TAG);
    }

    private void removeFromLocalCache(long id) {
        if (mInsertedImages.containsKey(id))
            mInsertedImages.remove(id);
        else if (mFailedImages.containsKey(id))
            mFailedImages.remove(id);
    }


    @Override
    public void onLinkButtonClick() {
        showLinkDialog(new LinkDialog(mContext), false);
    }

    @Override
    public void onInsertImageButtonClick() {
        if (checkPermission()) {
            showImagePicker();
        }
    }

    @Override
    public void onLinkClick(String name, String url) {
        showLinkDialog(new LinkDialog(mContext, name, url), true);
    }

    @Override
    public void onImageClick(Long id) {
        if (mInsertedImages.containsKey(id))
            showPictureClickDialog(PictureDialog.createPictureDialog(id), new CharSequence[]{getString(R.string.delete)});
        else if (mFailedImages.containsKey(id)) {
            showPictureClickDialog(PictureDialog.createPictureDialog(id),
                    new CharSequence[]{getString(R.string.delete), getString(R.string.retry)});
        }
    }

    @Override
    public void onThemeClick(boolean isSelected) {
        if (!isSelected) {
            BaseTheme theme = new DarkTheme();
            mRichEditor.setTheme(theme);
            ll_title_bar.setBackgroundColor(theme.getBackGroundColors()[0]);
            tv_submit.setTextColor(theme.getAccentColor());
            tv_count.setTextColor(theme.getNormalColor());
            iv_return.setImageResource(R.mipmap.arrow_left_white);
        } else {
            BaseTheme theme = new LightTheme();
            mRichEditor.setTheme(theme);
            ll_title_bar.setBackgroundColor(theme.getBackGroundColors()[0]);
            tv_submit.setTextColor(theme.getAccentColor());
            tv_count.setTextColor(theme.getNormalColor());
            iv_return.setImageResource(R.mipmap.left_arrow_black);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.tv_submit:
                String title = mRichEditor.getHtmlTitle();
                if (!TextUtils.isEmpty(title)) {
                    if (mFailedImages.size() == 0) {
                        Iterator iterator = mInsertedImages.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            Long key = (Long) entry.getKey();
                            mRichEditor.removeInput(key);
                        }
                        //延时1s获取htmlContent,防止input内容没有更新完;
                        mHandler.sendEmptyMessageDelayed(3, 1000);

                    } else {
                        Toast.makeText(mContext, "您还有未上传的图片", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "请填写标题", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE) {
            List<Uri> result = Matisse.obtainResult(data);
            String path = getRealPath(result.get(0));
            long id = SystemClock.currentThreadTimeMillis();
            long size[] = SizeUtils.getBitmapSize(path);
            mRichEditor.insertImage(path, id, size[0], size[1]);
            mInsertedImages.put(id, path);
            uploadFile(path, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker();
                } else {
                    Toast.makeText(mContext, "无法获取到拍照权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker();
                } else {
                    Toast.makeText(mContext, "无法获取到拍照权限", Toast.LENGTH_SHORT).show();
                }

                break;
            case REQUEST_CAMERA_AND_WRITE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showImagePicker();
                } else {
                    Toast.makeText(mContext, "无法获取到权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean checkPermission() {
        boolean isHasWritePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean isHasCameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (!isHasWritePermission || !isHasCameraPermission) {
            if (isHasWritePermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
                return false;
            } else if (isHasCameraPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_PERMISSION);
                return false;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_CAMERA_AND_WRITE_PERMISSION);
                return false;
            }
        } else {
            return true;
        }
    }

    private void uploadFile(final String filePath, final long id) {
        OkGo.<String>post(UPLOAD_IMAGE_URL)
                .tag(this)
                .params(PARME_NAME, new File(filePath))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ResponseData<Url> data = com.alibaba.fastjson.JSONObject.parseObject(response.body(), new TypeReference<ResponseData<Url>>() {
                        }.getType());
                        if (data != null) {
                            if (data.getCode() == 200) {
                                mRichEditor.setImageUploadProcess(id, 100);
                                mPathAndUri.put(filePath, data.getRows().getUrl());
                            } else {
                                mRichEditor.setImageFailed(id);
                                mInsertedImages.remove(id);
                                mFailedImages.put(id, filePath);
                            }
                        }
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                        if (progress.fraction != 1) {
                            mRichEditor.setImageUploadProcess(id, (int) (progress.fraction * 100));
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mRichEditor.setImageFailed(id);
                        mInsertedImages.remove(id);
                        mFailedImages.put(id, filePath);
                    }
                });

    }

    private void submitArticle(String content, String title) {
        Map<String, String> params = new HashMap<>();
        params.put("id", "0");
        params.put("title", title);
        params.put("content", content);
        params.put("userid", "25");
        params.put("forumid", "3");
        params.put("source", "原创");
        params.put("state", "1");
        JSONObject jsonObject = new JSONObject(params);
        OkGo.<String>post("http://47.52.227.43/zhuwai365/api/v2/public/api.php/Article/release")
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d(LOG_TAG, "Success" + response.body());
                        ResponseData responseData = com.alibaba.fastjson.JSONObject.parseObject(response.body(), ResponseData.class);
                        if (responseData.getCode() == 200) {
                            Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Fail" + response.body());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });

    }

    //针对知乎图片选择框架拍照返回获取不到path
    private String getRealPath(Uri uri) {
        String path = RealPathFromUriUtils.getRealPathFromUri(mContext, uri);
        if (TextUtils.isEmpty(path)) {
            if (uri != null) {
                String uriString = uri.toString();
                int index = uriString.lastIndexOf("/");
                String imageName = uriString.substring(index);
                File storageDir;
                storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File file = new File(storageDir, imageName);
                if (file.exists()) {
                    path = file.getAbsolutePath();
                } else {
                    storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file1 = new File(storageDir, imageName);
                    path = file1.getAbsolutePath();
                }
            }
        }
        return path;
    }


}
