package xy.richtexteditor.view.bottommenu;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public interface IBottomMenuItem {
    Long getItemId();

    View getMainView();

    interface OnItemCLickListenerParcelable extends Parcelable {
        void onItemClick(MenuItem item);

        @Override
        int describeContents();

        @Override
        void writeToParcel(Parcel parcel, int i);
    }

    interface OnBottomItemClickListener {
        boolean onItemClick(MenuItem item, boolean isSelected);
    }
}
