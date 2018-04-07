package xy.richtexteditor.view.bottommenu;

import android.content.Context;
import android.view.View;

/**
 * Created by Xieying on 2018/4/3
 * Function:
 */
public class MenuItemFactory {

    public static MenuItem generateMenuItem(long id, View contentView) {
        return new MenuItem(id, contentView);
    }

    public static ImageViewButtonItem generateImageItem(Context context, long id, int uri, boolean b) {
        return new ImageViewButtonItem(context, generateMenuItem(id, null), uri, b);
    }

    public static ImageViewButtonItem generateImageItem(Context context, long id, int uri) {
        return new ImageViewButtonItem(context, generateMenuItem(id, null), uri);
    }

    public static ImageViewButtonItem generateImageItem(Context context, int uri, boolean b) {
        return new ImageViewButtonItem(context, generateMenuItem(0x00, null), uri, b);
    }

    public static TextViewItem generateTextItem(Context context, long id, String text) {
        return new TextViewItem(context, generateMenuItem(id, null), text);
    }
}
