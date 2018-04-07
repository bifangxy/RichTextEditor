package xy.richtexteditor.activity;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import xy.richtexteditor.R;
import xy.richtexteditor.dialog.LinkDialog;
import xy.richtexteditor.theme.BaseTheme;
import xy.richtexteditor.theme.DarkTheme;
import xy.richtexteditor.theme.LightTheme;
import xy.richtexteditor.view.bottommenu.BottomMenu;
import xy.richtexteditor.view.richeditor.MyRichEditor;
import xy.richtexteditor.view.richeditor.RichEditor;

/**
 * Created by Xieying on 2018/3/30
 * Function:MainActivity
 */
public class MainActivity extends AppCompatActivity implements MyRichEditor.OnEditorClickListener, View.OnClickListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_IMAGE = 101;

    private Context mContext;

    private LinearLayout ll_title_bar;
    private ImageView iv_return;
    private TextView tv_count;
    private TextView tv_submit;
    private MyRichEditor mRichEditor;
    private BottomMenu mBottomMenu;

    private HashMap<Long, String> mInsertedImages;
    private HashMap<Long, String> mFailedImages;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_count.setText(msg.arg1 + "字");
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

    private void initData() {
        mInsertedImages = new HashMap<>();
        mFailedImages = new HashMap<>();
        mRichEditor.setBottomMenu(mBottomMenu);
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

    @Override
    public void onLinkButtonClick() {
        showLinkDialog(new LinkDialog(mContext), false);
    }

    @Override
    public void onInsertImageButtonClick() {
        showImagePicker();
    }

    @Override
    public void onLinkClick(String name, String url) {
        showLinkDialog(new LinkDialog(mContext, name, url), true);
    }

    @Override
    public void onImageClick(Long id) {

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
                break;
            case R.id.tv_submit:
                break;
        }
    }
}
