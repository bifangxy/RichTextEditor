package xy.richtexteditor.view.richeditor;

import android.content.Context;
import android.support.annotation.DrawableRes;

import xy.richtexteditor.R;
import xy.richtexteditor.view.bottommenu.IBottomMenuItem;
import xy.richtexteditor.view.bottommenu.ImageViewButtonItem;
import xy.richtexteditor.view.bottommenu.MenuItemFactory;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
public class DefaultItemFactory extends BaseItemFactory<ImageViewButtonItem> {

    private ImageViewButtonItem generateItem(Context context, long itemIndex, @DrawableRes int id) {
        return MenuItemFactory.generateImageItem(context, itemIndex, id, false);
    }

    private ImageViewButtonItem generateAutoSetItem(Context context, long itemIndex, @DrawableRes int id) {
        return MenuItemFactory.generateImageItem(context, itemIndex, id, true);
    }

    protected ImageViewButtonItem generateInsertImageItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.INSERT_IMAGE, R.drawable.insert_image);
        item.setOnItemCLickListener(listener);
        return item;
    }


    protected ImageViewButtonItem generateAItem(Context context) {
        return generateAutoSetItem(context, ItemIndex.A, R.drawable.a);
    }

    protected ImageViewButtonItem generateMoreItem(Context context) {
        return generateAutoSetItem(context, ItemIndex.MORE, R.drawable.more);
    }

    protected ImageViewButtonItem generateUndoItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.UNDO, R.drawable.undo);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateRedoItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.REDO, R.drawable.redo);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateBoldItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.BOLD, R.drawable.bold);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateItalicItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.ITALIC, R.drawable.italic);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateStrikeThroughItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.STRIKE_THROUGH, R.drawable.strikethrough);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateBlockQuoteItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.BLOCK_QUOTE, R.drawable.blockquote);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateH1Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H1, R.drawable.h1);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateH2Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H2, R.drawable.h2);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateH3Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H3, R.drawable.h3);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateH4Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H4, R.drawable.h4);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateHalvingItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.HALVING_LINE, R.drawable.halving_line);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateLinkItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.LINK, R.drawable.link);
        item.setOnItemCLickListener(listener);
        return item;
    }

    protected ImageViewButtonItem generateThemeItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.THEME, R.drawable.theme);
        item.setOnItemCLickListener(listener);
        return item;
    }


    @Override
    public ImageViewButtonItem generateItem(Context context, Long id, IBottomMenuItem.OnBottomItemClickListener listener) {
        switch (id.intValue()) {
            case (int) ItemIndex.INSERT_IMAGE:
                return generateInsertImageItem(context, listener);
            case (int) ItemIndex.UNDO:
                return generateUndoItem(context, listener);
            case (int) ItemIndex.REDO:
                return generateRedoItem(context, listener);
            case (int) ItemIndex.BOLD:
                return generateBoldItem(context, listener);
            case (int) ItemIndex.ITALIC:
                return generateItalicItem(context, listener);
            case (int) ItemIndex.STRIKE_THROUGH:
                return generateStrikeThroughItem(context, listener);
            case (int) ItemIndex.BLOCK_QUOTE:
                return generateBlockQuoteItem(context, listener);
            case (int) ItemIndex.H1:
                return generateH1Item(context, listener);
            case (int) ItemIndex.H2:
                return generateH2Item(context, listener);
            case (int) ItemIndex.H3:
                return generateH3Item(context, listener);
            case (int) ItemIndex.H4:
                return generateH4Item(context, listener);
            case (int) ItemIndex.HALVING_LINE:
                return generateHalvingItem(context, listener);
            case (int) ItemIndex.LINK:
                return generateLinkItem(context, listener);
            case (int) ItemIndex.THEME:
                return generateThemeItem(context, listener);
            default:
                return null;
        }
    }

    @Override
    public ImageViewButtonItem generateItem(Context context, Long id) {
        switch (id.intValue()) {
            case (int) ItemIndex.A:
                return generateAItem(context);
            case (int) ItemIndex.MORE:
                return generateMoreItem(context);
            default:
                return null;
        }
    }
}
