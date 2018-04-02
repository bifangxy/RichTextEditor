package xy.richtexteditor.theme;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public class BaseTheme extends AbstractTheme {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new BaseTheme(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new BaseTheme[i];
        }
    };

    public BaseTheme() {
        super(null);
    }

    protected BaseTheme(Parcel parcel) {
        super(parcel);
    }


    @Override
    public int[] getBackGroundColors() {
        return new int[]{};
    }

    @Override
    public int getAccentColor() {
        return Color.BLACK;
    }

    @Override
    public int getNormalColor() {
        return Color.GRAY;
    }

}
