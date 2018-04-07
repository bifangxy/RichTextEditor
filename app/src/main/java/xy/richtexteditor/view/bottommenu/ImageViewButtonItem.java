package xy.richtexteditor.view.bottommenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.widget.ImageButton;
import android.widget.ImageView;

import xy.richtexteditor.R;

/**
 * Created by Xieying on 2018/4/3
 * Function:
 */
public class ImageViewButtonItem extends AbstractBottomMenuItem<ImageButton> implements Parcelable {

    private int idRes;

    private boolean enableAutoSet = true;

    private OnBottomItemClickListener mOnItemCLickListener;


    public ImageViewButtonItem(Context mContext, MenuItem mMenuItem, int idRes) {
        this(mContext, mMenuItem, idRes, true);
    }

    public ImageViewButtonItem(Context mContext, MenuItem mMenuItem, int idRes, boolean enableAutoSet) {

        super(mContext, mMenuItem);
        this.idRes = idRes;
        this.enableAutoSet = enableAutoSet;
    }

    protected ImageViewButtonItem(Parcel parcel) {
        super(parcel);
        this.idRes = parcel.readInt();
        this.enableAutoSet = parcel.readInt() == 1;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public ImageButton createView() {
        ImageButton imageButton = new ImageButton(getContext());
        if (!enableAutoSet) {
            TypedArray typedArray = getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
            Drawable drawable = typedArray.getDrawable(0);
            imageButton.setBackgroundDrawable(drawable);
            typedArray.recycle();
        } else {
            imageButton.setBackgroundDrawable(null);
        }
        imageButton.setImageResource(idRes);
        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        return imageButton;
    }

    @Override
    public void settingAfterCreate(boolean isSelected, ImageButton view) {
        if (enableAutoSet) {
            if (isSelected) {
                view.setColorFilter(getTheme().getAccentColor(), PorterDuff.Mode.SRC_IN);
            } else {
                view.setColorFilter(getTheme().getNormalColor(), PorterDuff.Mode.SRC_IN);
            }
        } else {
            view.setColorFilter(getTheme().getNormalColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public boolean onItemClickIntercept() {
        return mOnItemCLickListener == null ? super.onItemClickIntercept() :
                mOnItemCLickListener.onItemClick(getMenuItem(), isSelected());
    }

    @Override
    public void onSelectChange(boolean isSelected) {
        ImageButton imageButton = getMainView();
        if (imageButton == null)
            return;
        settingAfterCreate(isSelected, imageButton);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.idRes);
        dest.writeInt(this.enableAutoSet ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageViewButtonItem> CREATOR = new Creator<ImageViewButtonItem>() {
        @Override
        public ImageViewButtonItem createFromParcel(Parcel in) {
            return new ImageViewButtonItem(in);
        }

        @Override
        public ImageViewButtonItem[] newArray(int size) {
            return new ImageViewButtonItem[size];
        }
    };

    public boolean isEnableAutoSet() {
        return enableAutoSet;
    }

    public void setEnableAutoSet(boolean enableAutoSet) {
        this.enableAutoSet = enableAutoSet;
    }

    public void setOnItemCLickListener(OnBottomItemClickListener mOnItemCLickListener) {
        this.mOnItemCLickListener = mOnItemCLickListener;
    }
}
