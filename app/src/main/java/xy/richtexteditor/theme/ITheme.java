package xy.richtexteditor.theme;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public interface ITheme extends Serializable, Parcelable {
    int[] getBackGroundColors();

    int getAccentColor();

    int getNormalColor();
}
