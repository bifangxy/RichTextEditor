package xy.richtexteditor.view.richeditor;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import xy.richtexteditor.theme.AbstractTheme;
import xy.richtexteditor.theme.DarkTheme;
import xy.richtexteditor.theme.ITheme;
import xy.richtexteditor.theme.IThemeMenu;
import xy.richtexteditor.theme.LightTheme;
import xy.richtexteditor.utils.Utils;
import xy.richtexteditor.view.bottommenu.AbstractBottomMenuItem;
import xy.richtexteditor.view.bottommenu.IBottomMenuItem;
import xy.richtexteditor.view.bottommenu.IMultiMenu;
import xy.richtexteditor.view.bottommenu.MenuItem;

/**
 * Created by Xieying on 2018/4/1
 * Function:
 */
public class MyRichEditor extends RichEditor {

    private IMultiMenu mMultiBottomMenu;

    private SelectController mSelectController;

    private OnEditorClickListener mOnEditorClickListener;

    private ArrayList<Long> mFreeItem;

    private ItemIndex.Register mRegister;

    private OnStateChangeListener mOnStateChangeListener;

    private BaseItemFactory mBaseItemFactory;

    public MyRichEditor(Context context) {
        super(context);
    }

    public MyRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBottomMenu(@NonNull IMultiMenu multiMenu) {
        this.mMultiBottomMenu = multiMenu;
        init();
        initRichTextViewListener();
    }


    private void init() {
        mSelectController = SelectController.createrController();
        mRegister = ItemIndex.getInstance().getRegister();
        mFreeItem = new ArrayList<>();

        addImageInsert();
        addTypefaceBranch(true, true, true, true, true);
        addMoreBranch(true, true);
        addUndo();
        addRedo();
        addTheme();

        mSelectController.setHandler(new SelectController.StateTransHandler() {
            @Override
            public void handleA2B(long id) {
                if (id > 0) {
                    mMultiBottomMenu.setItemSelected(id, true);
                }
            }

            @Override
            public void handleB2A(long id) {
                if (id > 0) {
                    mMultiBottomMenu.setItemSelected(id, false);
                }

            }
        });


    }


    private void initRichTextViewListener() {

        setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChangeListener(String text, List<Type> types) {
                onStateChange(text, types);
                for (long id : mFreeItem) {
                    mMultiBottomMenu.setItemSelected(id, false);
                }
                mSelectController.reset();
                for (Type t : types) {
                    if (!mSelectController.contain(t.getTypeCode()))
                        mMultiBottomMenu.setItemSelected(t.getTypeCode(), true);
                    else {
                        mSelectController.changeState(t.getTypeCode());
                    }
                }
            }
        });
        setOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {

            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(boolean isFocus) {
                if (!isFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        mMultiBottomMenu.show(200);
                } else {
                    mMultiBottomMenu.hide(200);
                }
            }
        });
        setOnLinkClickListener(new OnLinkClickListener() {
            @Override
            public void onLinkClick(String linkName, String url) {
                showChangeLinkDialog(linkName, url);
            }
        });
        setOnImageClickListener(new OnImageClickListener() {
            @Override
            public void onImageClick(Long id) {
                showImageClick(id);
            }
        });
        setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad(boolean isReady) {
                if (isReady)
                    focusEditor();
            }
        });
    }

    public MyRichEditor addTypefaceBranch(boolean needBold, boolean needItalic, boolean needStrikeThrough, boolean needBlockQuote, boolean needH) {
        checkNull(mMultiBottomMenu);
        if (!needBold || !needItalic || !needStrikeThrough || !needBlockQuote || !needH)
            return this;
        if (needBlockQuote)
            mSelectController.add(ItemIndex.BLOCK_QUOTE);
        if (needH)
            mSelectController.addAll(ItemIndex.H1, ItemIndex.H2, ItemIndex.H3, ItemIndex.H4);
        if (needBold)
            mFreeItem.add(ItemIndex.BOLD);
        if (needItalic)
            mFreeItem.add(ItemIndex.ITALIC);
        if (needStrikeThrough)
            mFreeItem.add(ItemIndex.STRIKE_THROUGH);
        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(), ItemIndex.A))
                .addItem(ItemIndex.A, needBold ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.BOLD,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setBold();
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needItalic ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.ITALIC,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setItalic();
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needStrikeThrough ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.STRIKE_THROUGH,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setStrikeThrough();
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needBlockQuote ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.BLOCK_QUOTE,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setBlockquote(!isSelected);
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H1,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(1, !isSelected);
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H2,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(2, !isSelected);
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H3,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(3, !isSelected);
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H4,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(4, !isSelected);
                                return isInSelectedController(item.getId());
                            }
                        }
                ) : null);
        return this;


    }


    public MyRichEditor addImageInsert() {
        checkNull(mMultiBottomMenu);

        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(
                getContext(),
                ItemIndex.INSERT_IMAGE,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        showImagePicker();
                        return true;
                    }
                }));
        return this;
    }

    public MyRichEditor addMoreBranch(boolean needHalvingLine, boolean needLink) {
        checkNull(mMultiBottomMenu);
        if (!needHalvingLine && !needLink) {
            return this;
        }
        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(), ItemIndex.MORE))
                .addItem(ItemIndex.MORE, needHalvingLine ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.HALVING_LINE,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                insertLine();
                                return false;
                            }
                        }) : null)
                .addItem(ItemIndex.MORE, needLink ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.LINK,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                showLinkDialog();
                                return false;
                            }
                        }
                ) : null);
        return this;
    }

    public MyRichEditor addUndo() {
        checkNull(mMultiBottomMenu);
        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(
                getContext(),
                ItemIndex.UNDO,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        undo();
                        return false;
                    }
                }
        ));
        return this;
    }


    public MyRichEditor addRedo() {
        checkNull(mMultiBottomMenu);
        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(
                getContext(),
                ItemIndex.REDO,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        redo();
                        return false;
                    }
                }
        ));
        return this;
    }


    public MyRichEditor addTheme() {
        checkNull(mMultiBottomMenu);
        mMultiBottomMenu.addRootItem(getBaseItemFactory().generateItem(
                getContext(),
                ItemIndex.THEME,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        onThemeClick(isSelected);
                        return false;
                    }
                }
        ));
        return this;
    }

    @SuppressWarnings("unused")
    public MyRichEditor addRootCustomImage(long id, AbstractBottomMenuItem item) {
        checkNull(mMultiBottomMenu);
        if (mRegister.isDefaultId(id))
            throw new RuntimeException(id + ":" + ItemIndex.HAS_REGISTER_EXCEPTION);
        if (!mRegister.hasRegister(id))
            mRegister.register(id);
        item.getMenuItem().setId(id);
        mMultiBottomMenu.addRootItem(item);
        return this;

    }

    @SuppressWarnings("unused")
    public MyRichEditor addCustomImage(long parentId, long id, AbstractBottomMenuItem item) {
        checkNull(mMultiBottomMenu);
        if (!mRegister.hasRegister(parentId)) {
            throw new RuntimeException(parentId + ":" + ItemIndex.NO_RSGISTER_EXCEPTION);
        }
        if (mRegister.isDefaultId(id)) {
            throw new RuntimeException(id + ":" + ItemIndex.HAS_REGISTER_EXCEPTION);
        }
        if (!mRegister.hasRegister(id))
            mRegister.register(id);
        item.getMenuItem().setId(id);
        mMultiBottomMenu.addItem(parentId, item);
        return this;
    }

    @SuppressWarnings("unused")
    public void setTheme(int theme) {
        if (theme == AbstractTheme.DARK_THEME) {
            if (mMultiBottomMenu instanceof ITheme)
                ((IThemeMenu) mMultiBottomMenu).setTheme(new DarkTheme());
        } else if (theme == AbstractTheme.LIGHT_THEME) {
            if (mMultiBottomMenu instanceof ITheme)
                ((IThemeMenu) mMultiBottomMenu).setTheme(new LightTheme());
        }
    }

    public void setTheme(final ITheme theme) {
        if (mMultiBottomMenu instanceof IThemeMenu)
            ((IThemeMenu) mMultiBottomMenu).setTheme(theme);
        post(new Runnable() {
            @Override
            public void run() {
                String backgroundColor = Utils.converInt2HexColor(theme.getBackGroundColors()[0]);
                double backgroundLum = ColorUtils.calculateLuminance(theme.getBackGroundColors()[0]);
                double normalLum = ColorUtils.calculateLuminance(theme.getNormalColor());
                double accentLum = ColorUtils.calculateLuminance(theme.getAccentColor());

                int fontColorInt;
                if (Math.abs(normalLum - backgroundLum) > Math.abs(accentLum - backgroundLum))
                    fontColorInt = theme.getNormalColor();
                else
                    fontColorInt = theme.getAccentColor();
                String fontColor = Utils.converInt2HexColor(fontColorInt);
                int color = ColorUtils.blendARGB(fontColorInt, theme.getBackGroundColors()[0], 0.5f);
                exec("javascript:RE.setBackgroundColor('" + backgroundColor + "');");
                exec("javascript:RE.setFontColor('" + fontColor + "');");
            }
        });
    }


    private void onStateChange(String text, List<Type> types) {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onStateChangeListener(text, types);
        }
    }

    private void showImagePicker() {
        if (mOnEditorClickListener != null) {
            mOnEditorClickListener.onInsertImageButtonClick();
        }
    }

    private void showImageClick(long id) {
        if (mOnEditorClickListener != null) {
            mOnEditorClickListener.onImageClick(id);
        }
    }

    private void showLinkDialog() {
        if (mOnEditorClickListener != null) {
            mOnEditorClickListener.onLinkButtonClick();
        }
    }

    private void showChangeLinkDialog(String name, String url) {
        if (mOnEditorClickListener != null) {
            mOnEditorClickListener.onLinkClick(name, url);
        }
    }

    private void onThemeClick(boolean isSelected) {
        if (mOnEditorClickListener != null) {
            mOnEditorClickListener.onThemeClick(isSelected);
        }
    }

    private boolean isInSelectedController(long id) {
        if (mSelectController.contain(id)) {
            mSelectController.changeState(id);
            return true;
        }
        return false;
    }


    private void checkNull(Object obj) {
        if (obj == null) {
            throw new RuntimeException("object can't be null");
        }
    }


    public void setOnEditorClickListener(OnEditorClickListener mOnEditorClickListener) {
        this.mOnEditorClickListener = mOnEditorClickListener;
    }

    public void setOnStateChangeListener(OnStateChangeListener mOnStateChangeListener) {
        this.mOnStateChangeListener = mOnStateChangeListener;
    }

    public void setBaseItemFactory(BaseItemFactory mBaseItemFactory) {
        this.mBaseItemFactory = mBaseItemFactory;
    }


    public BaseItemFactory getBaseItemFactory() {
        if (mBaseItemFactory == null)
            mBaseItemFactory = createDefaultFactory();
        return mBaseItemFactory;
    }

    private DefaultItemFactory createDefaultFactory() {
        return new DefaultItemFactory();
    }

    public interface OnEditorClickListener {

        void onLinkButtonClick();

        void onInsertImageButtonClick();

        void onLinkClick(String name, String url);

        void onImageClick(Long id);

        void onThemeClick(boolean isSelected);

    }

    @SuppressWarnings("unused")
    public abstract static class OnEditorClickListenerImp implements OnEditorClickListener {
        @Override
        public void onLinkButtonClick() {

        }

        @Override
        public void onInsertImageButtonClick() {

        }

        @Override
        public void onLinkClick(String name, String url) {

        }

        @Override
        public void onImageClick(Long id) {

        }

        @Override
        public void onThemeClick(boolean isSelected) {

        }
    }
}
