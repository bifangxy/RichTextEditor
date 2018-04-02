package xy.richtexteditor.theme;

import android.graphics.Color;

/**
 * Created by Xieying on 2018/4/2
 * Function:
 */
public class DarkTheme extends BaseTheme {

    @Override
    public int[] getBackGroundColors() {
        return new int[]{Color.DKGRAY, Color.rgb(50, 50, 50)};
    }

    @Override
    public int getAccentColor() {
        return Color.rgb(255, 161, 118);
    }

    @Override
    public int getNormalColor() {
        return Color.LTGRAY;
    }
}
