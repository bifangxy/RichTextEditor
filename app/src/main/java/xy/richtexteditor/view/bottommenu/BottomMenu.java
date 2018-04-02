package xy.richtexteditor.view.bottommenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Xieying on 2018/4/2
 * Function:自定义底部按钮
 */
@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public class BottomMenu extends ViewGroup {

    private static final int DEFAULT_HEIGHT = 54;

    private static int MAX_NUM_ONE_ROW = 6;

    private static int MAX_LEVELS = 3;

    private static int INNER_LAYOUT_PADDING_L = 0;

    private static int INNER_LAYOUT_PADDING_R = 0;

    private static int INNER_LAYOUT_PADDING_T = 0;

    private static int INNER_LAYOUT_PADDING_B = 0;

    private static float INNER_LAYOUT_PADDING_RATE = 0.44f;

    private int[] colorSet;

    public BottomMenu(Context context) {
        super(context);
    }

    public BottomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }
}
