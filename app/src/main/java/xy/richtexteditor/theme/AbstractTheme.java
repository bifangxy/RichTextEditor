package xy.richtexteditor.theme;

import android.os.Parcel;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public abstract class AbstractTheme implements ITheme {

    public static final int LIGHT_THEME = 0x01;

    public static final int DARK_THEME = 0x02;

    @Override
    public abstract int[] getBackGroundColors();

    @Override
    public abstract int getAccentColor();

    @Override
    public abstract int getNormalColor();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected AbstractTheme(Parcel parcel) {
    }
}
