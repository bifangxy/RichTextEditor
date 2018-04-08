package xy.richtexteditor.utils;

import android.graphics.BitmapFactory;

/**
 * Created by Xieying on 2018/4/8
 * Function:
 */
public class SizeUtils {
    public static final double ERROR_CONVER = -1d;

    public static final int KB = 0x001;
    public static final int MB = 0x002;
    public static final int GB = 0x003;
    public static final int TB = 0x004;

    double conver(long byteSize, int flag) {
        if (flag <= TB && flag >= KB)
            return byteSize >> (flag * 10);
        else
            return ERROR_CONVER;
    }

    public static long[] getBitmapSize(String url) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, op);
        return new long[]{op.outWidth, op.outHeight};
    }
}
