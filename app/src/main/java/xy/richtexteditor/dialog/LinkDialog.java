package xy.richtexteditor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import xy.richtexteditor.R;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
public class LinkDialog extends Dialog {
    private static final String LOG_TAG = LinkDialog.class.getSimpleName();

    private Context mContext;

    private OnDialogClickListener mListener;

    private String linkName;

    private String linkUrl;

    private EditText et_link_name;

    private EditText et_link_url;

    private Button bt_cancel;

    private Button bt_confirm;

    private View contentView;

    public LinkDialog(Context context) {
        super(context);
    }

    public LinkDialog(Context context, String linkName, String linkUrl) {
        super(context);
        this.linkUrl = linkUrl;
        this.linkName = linkName;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_link);
        initView();
        initData();
        initListener();

    }

    private void initListener() {
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null)
                    mListener.cancel();
            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                linkName = et_link_name.getText().toString();
                linkUrl = et_link_url.getText().toString();
                if (mListener != null)
                    mListener.confirm(linkName, linkUrl);
            }
        });
    }

    private void initView() {
        et_link_name = findViewById(R.id.et_link_name);
        et_link_url = findViewById(R.id.et_link_url);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_confirm = findViewById(R.id.bt_confirm);
    }

    private void initData() {
        if (linkName != null)
            et_link_name.setText(linkName);
        if (linkUrl != null)
            et_link_url.setText(linkUrl);
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.mListener = listener;
    }

    public interface OnDialogClickListener {
        void cancel();

        void confirm(String linkName, String linkUrl);
    }
}
