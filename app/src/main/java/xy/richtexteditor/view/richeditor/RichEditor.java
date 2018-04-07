package xy.richtexteditor.view.richeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xy.richtexteditor.utils.Utils;

/**
 * Created by Xieying on 2018/4/1
 * Function:自定义RichEditor基类
 */
public class RichEditor extends WebView {


    private static final String SETUP_HTML = "file:///android_asset/editor.html";
    private static final String CALLBACK_SCHEME = "callback://";
    private static final String STATE_SCHEME = "state://";
    private static final String LINK_CHANGE_SCHEME = "change://";
    private static final String FOCUS_CHANGE_SCHEME = "focus://";
    private static final String IMAGE_CLICK_SCHEME = "image://";


    private OnTextChangeListener mTextChangeListener;

    private OnStateChangeListener mStateChangeListener;

    private OnLinkClickListener mLinkClickListener;

    private OnFocusChangeListener mFocusChangeListener;

    private OnLoadListener mLoadListener;

    private OnImageClickListener mImageClickListener;

    private OnTextLengthChangeListener mTextLengthChangeListener;

    private OnScrollChangedCallback mScrollChangeCallback;


    private boolean isReady = false;

    private String mContent;

    private long mContentLength;

    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();
    }


    public void setOnTextChangeListener(OnTextChangeListener mTextChangeListener) {
        this.mTextChangeListener = mTextChangeListener;
    }

    public void setOnTextLengthChangeListener(OnTextLengthChangeListener mTextLengthChangeListener) {
        this.mTextLengthChangeListener = mTextLengthChangeListener;
    }

    public void setOnStateChangeListener(OnStateChangeListener mStateChangeListener) {
        this.mStateChangeListener = mStateChangeListener;
    }

    public void setOnLinkClickListener(OnLinkClickListener mLinkClickListener) {
        this.mLinkClickListener = mLinkClickListener;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener mFocusChangeListener) {
        this.mFocusChangeListener = mFocusChangeListener;
    }

    public void setOnLoadListener(OnLoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    public void setOnImageClickListener(OnImageClickListener mImageClickListener) {
        this.mImageClickListener = mImageClickListener;
    }


    public OnScrollChangedCallback getOnScrollChangeCallback() {
        return mScrollChangeCallback;
    }

    public void setOnScrollChangeCallback(OnScrollChangedCallback mScrollChangeCallback) {
        this.mScrollChangeCallback = mScrollChangeCallback;
    }

    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode())
            return;
        initWebView();

    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() {
        addJavascriptInterface(new Android4JsInterface(), "AndroidInterface");
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setWebViewClient(new EditorWebViewClient());
        setWebChromeClient(new WebChromeClient());
        getSettings().setJavaScriptEnabled(true);
        loadUrl(SETUP_HTML);
        mContentLength = 0;
    }

    private void callback(String text) {
        mContent = text.replaceFirst(CALLBACK_SCHEME, "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mContent);
        }
    }

    private void linkChangeCallBack(String text) {
        text = text.replaceFirst(LINK_CHANGE_SCHEME, "");
        String[] results = text.split("@_@");
        if (mLinkClickListener != null && results.length >= 2) {
            mLinkClickListener.onLinkClick(results[0], results[1]);
        }
    }

    private void imageClickCallBack(String url) {
        if (mImageClickListener != null) {
            mImageClickListener.onImageClick(Long.valueOf(url.replaceFirst(IMAGE_CLICK_SCHEME, "")));
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollChangeCallback != null) {
            mScrollChangeCallback.onScroll(l - oldl, t - oldt);
        }
    }

    public void stateCheck(String text) {
        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }
        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChangeListener(state, types);
        }

    }

    public void getHtmlAsyn() {
        exec("javascript:RE.getHtml4Android();");
    }

    public String getHtml() {
        return mContent;
    }

    public long getContentLength() {
        return mContentLength;
    }


    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                + "px');");
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        setPadding(start, top, end, bottom);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public void setEditorBackgroundColoer(int color) {
        setBackgroundColor(color);
    }

    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    public void undo() {
        exec("javascript:RE.exec('undo');");
    }

    public void redo() {
        exec("javascript:RE.exec('redo');");
    }

    public void setBold() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.exec('bold');");
    }

    public void setItalic() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.exec('italic')");
    }

    public void setStrikeThrough() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.exec('strikethrough');");
    }

    public void setBlockquote(boolean b) {
        exec("javascript:RE.saveRange();");
        if (b) {
            exec("javascript:RE.exec('blockquote');");
        } else {
            exec("javascript:RE.exec('p');");
        }


    }

    public void setHeading(int heading, boolean b) {
        exec("javascript:RE.saveRange();");
        if (b) {
            exec("javascript:RE.exec('h" + heading + "');");
        } else {
            exec("javascript:RE.exec('p');");
        }
    }

    public void insertImage(String url, long id, long width, long height) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.insertImage('" + url + "'," + id + ", " + width + "," + height + ");");
    }

    public void deleteImageById(long id) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.removeImage(" + id + ");");
    }

    public void insertLine() {
        exec("javascript:RE.Range();");
        exec("javascript:RE.insertLine();");
    }

    public void insertLink(String href, String title) {
        exec("javascript:RE.Range();");
        exec("javascript:RE.insertLink('" + title + "', " + href + "');");
    }

    public void changeLink(String href, String title) {
        exec("javascript:RE.Range();");
        exec("javascript:RE.changeLink('" + title + "', " + href + "');");
    }

    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    public void setImageUploadProcess(long id, int process) {
        exec("javascript:RE.changeProcess(" + id + "', " + process + ");");
    }

    public void setImageFailed(long id) {
        exec("javascript:RE.uploadFailure(" + id + ");");
    }

    public void setImageReload(long id) {
        exec("javascript:RE.uploadReload(" + id + ");");
    }

    public void focusEditor() {
        requestFocus();
    }

    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }


    protected void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    private class EditorWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            mLoadListener.onLoad(isReady);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String encode;

            try {
                encode = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                callback(encode);
                return true;
            } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                stateCheck(encode);
                return true;
            }
            if (TextUtils.indexOf(url, LINK_CHANGE_SCHEME) == 0) {
                linkChangeCallBack(encode);
                return true;
            }
            if (TextUtils.indexOf(url, IMAGE_CLICK_SCHEME) == 0) {
                imageClickCallBack(encode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    private class Android4JsInterface {

        @JavascriptInterface
        public void setViewEnable(boolean enable) {
            if (mFocusChangeListener != null) {
                mFocusChangeListener.onFocusChange(enable);
            }
        }

        @JavascriptInterface
        public void setHtmlContent(String htmlContent) {
            mContent = htmlContent;
            if (mTextChangeListener != null) {
                mTextChangeListener.onTextChange(htmlContent);
            }
        }

        @JavascriptInterface
        public void staticWords(long num) {
            mContentLength = num;
            if (mTextLengthChangeListener != null) {
                mTextLengthChangeListener.onTextLengthChange(num);
            }
        }
    }


    public enum Type {
        BOLD(0x06),
        ITALIC(0x07),
        STRIKETHROUGH(0x08),
        BLOCKQUOTE(0x09),
        H1(0x0a),
        H2(0x0b),
        H3(0x0c),
        H4(0x0d);

        //SUPERSCRIPT(1),//SUBSCRIPT(2),//UNDERLINE(3),
        private long typeCode;

        Type(long i) {
            typeCode = i;
        }

        public long getTypeCode() {
            return typeCode;
        }

        public boolean isMapTo(long id) {
            return typeCode == id;
        }
    }


    public interface OnTextChangeListener {
        void onTextChange(String text);
    }

    public interface OnStateChangeListener {
        void onStateChangeListener(String text, List<Type> types);
    }

    public interface OnLinkClickListener {
        void onLinkClick(String linkName, String url);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(boolean isFocus);
    }

    public interface OnLoadListener {
        void onLoad(boolean isReady);
    }

    public interface OnImageClickListener {
        void onImageClick(Long id);
    }

    public interface OnTextLengthChangeListener {
        void onTextLengthChange(long length);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnScrollChangedCallback {
        void onScroll(int dx, int dy);
    }

}
