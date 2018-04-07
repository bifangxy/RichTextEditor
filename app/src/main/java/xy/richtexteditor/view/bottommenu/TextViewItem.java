package xy.richtexteditor.view.bottommenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by Xieying on 2018/4/3
 * Function:
 */
@SuppressLint("WeakerAccess")
public class TextViewItem extends AbstractBottomMenuItem<TextView> {

    private String text;

    public TextViewItem(Context mContext, MenuItem mMenuItem, String text) {
        super(mContext, mMenuItem);
        this.text = text;
    }

    protected TextViewItem(Parcel parcel) {
        super(parcel);
        this.text = parcel.readString();
    }

    @NonNull
    @Override
    public TextView createView() {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void settingAfterCreate(boolean isSelected, TextView view) {
        onSelectChange(isSelected);
    }

    @Override
    public void onSelectChange(boolean isSelected) {

        TextView textView = (TextView) getMainView();
        if (textView != null) {
            if (isSelected)
                textView.setBackgroundColor(Color.YELLOW);
            else {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.text);
    }

    public static final Creator<TextViewItem> CREATOR = new Creator<TextViewItem>() {
        @Override
        public TextViewItem createFromParcel(Parcel parcel) {
            return new TextViewItem(parcel);
        }

        @Override
        public TextViewItem[] newArray(int i) {
            return new TextViewItem[i];
        }
    };
}
