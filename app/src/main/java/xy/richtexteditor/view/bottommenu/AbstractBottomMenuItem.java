package xy.richtexteditor.view.bottommenu;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.Serializable;

import xy.richtexteditor.theme.ITheme;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public abstract class AbstractBottomMenuItem<T extends View> implements IBottomMenuItem, Parcelable, Serializable {

    private MenuItem mMenuItem;

    private boolean isSelected = false;

    private transient Context mContext;

    private ITheme mTheme;

    private OnItemClickListener onItemClickListener;

    public AbstractBottomMenuItem(Context mContext, MenuItem mMenuItem) {
        this.mContext = mContext;
        this.mMenuItem = mMenuItem;
        isSelected = false;
    }

    public void onDisplayPrepare() {
        View view = mMenuItem.getContentView();
        if (view == null) {
            mMenuItem.setContentView(createView());
        }
        settingAfterCreate(isSelected, (T) mMenuItem.getContentView());

        mMenuItem.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick();
            }
        });
    }

    public void onViewDestroy() {
        if (getMainView() != null) {
            getMainView().setOnClickListener(null);
            mMenuItem.setContentView(null);
        }
    }

    @NonNull
    public abstract T createView();

    public abstract void settingAfterCreate(boolean isSelected, T view);

    public void onSelectChange(boolean isSelected) {

    }

    public boolean onItemClickIntercept() {
        return false;
    }

    @Override
    public Long getItemId() {
        return mMenuItem.getId();
    }

    @Override
    public T getMainView() {
        return (T) mMenuItem.getContentView();
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }

    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != this.onItemClickListener)
            this.onItemClickListener = onItemClickListener;
    }

    public final void setSelected(boolean selected) {
        if (selected != isSelected) {
            this.isSelected = selected;
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private void onItemClick() {
        if (!onItemClickIntercept() && onItemClickListener != null)
            onItemClickListener.onItemClick(mMenuItem);
    }

    public ITheme getTheme() {
        return mTheme;
    }

    public void setTheme(ITheme mTheme) {
        if (mTheme != this.mTheme)
            this.mTheme = mTheme;
    }

    public void setThemeForDisplay(ITheme mTheme) {
        setTheme(mTheme);
        if (getMainView() != null) {
            settingAfterCreate(isSelected, getMainView());
            getMainView().invalidate();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.mMenuItem);
        parcel.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        parcel.writeParcelable(this.mTheme, i);
        parcel.writeParcelable(this.onItemClickListener, i);
    }

    protected AbstractBottomMenuItem(Parcel parcel) {
        this.mMenuItem = (MenuItem) parcel.readSerializable();
        this.isSelected = parcel.readByte() != 0;
        this.mTheme = parcel.readParcelable(ITheme.class.getClassLoader());
        this.onItemClickListener = parcel.readParcelable(OnItemClickListener.class.getClassLoader());
    }

    public abstract static class OnItemClickListener implements OnItemCLickListenerParcelable {

        @Override
        public abstract void onItemClick(MenuItem item);

        @Override
        public abstract int describeContents();

        @Override
        public abstract void writeToParcel(Parcel parcel, int i);
    }
}
