package xy.richtexteditor.view.richeditor;

import android.content.Context;

import xy.richtexteditor.view.bottommenu.AbstractBottomMenuItem;
import xy.richtexteditor.view.bottommenu.IBottomMenuItem;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
public interface IItemFactory<T extends AbstractBottomMenuItem> {

    T generateItem(Context context, Long id, IBottomMenuItem.OnBottomItemClickListener listener);

}
