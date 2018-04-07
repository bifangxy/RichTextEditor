package xy.richtexteditor.view.bottommenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import xy.richtexteditor.theme.ITheme;
import xy.richtexteditor.theme.IThemeMenu;
import xy.richtexteditor.theme.LightTheme;
import xy.richtexteditor.utils.Utils;

/**
 * Created by Xieying on 2018/4/2
 * Function:自定义底部按钮
 */
@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public class BottomMenu extends ViewGroup implements IMultiMenu, IThemeMenu {

    private static final int DEFAULT_HEIGHT = 54;

    private static int MAX_NUM_ONE_ROW = 6;

    private static int MAX_LEVELS = 3;

    private static int INNER_LAYOUT_PADDING_L = 0;

    private static int INNER_LAYOUT_PADDING_R = 0;

    private static int INNER_LAYOUT_PADDING_T = 0;

    private static int INNER_LAYOUT_PADDING_B = 0;

    private static float INNER_LAYOUT_PADDING_RATE = 0.44f;

    private int[] colorSet;

    private MenuItemTree mMenuTree;

    private HashMap<Long, AbstractBottomMenuItem> mBottomMenuItems;

    private ArrayDeque<MenuItem> mPathRecord;

    private ArrayList<LinearLayout> mDisplayMenus;

    private MenuItem mCurMenuItem;

    private int mDisplayRowNum = 0;

    private int mSingleRowHeight = 0;

    private Paint mPaint;

    private ITheme mTheme;

    private boolean isFirstMeasure = true;

    private boolean isFirstLayout = true;

    private boolean needLine = true;

    private AbstractBottomMenuItem.OnItemClickListener mOnItemClickListener;

    private AbstractBottomMenuItem.OnItemClickListener mInnerListener;


    public BottomMenu(Context context) {
        this(context, null);
    }

    public BottomMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (needLine) {
            mPaint = new Paint();
            mPaint.setAlpha(127);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.max(widthSize, 0);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                MAX_LEVELS = 5;
                break;
            case MeasureSpec.AT_MOST:
                heightSize = Utils.dip2px(getContext(), DEFAULT_HEIGHT);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.EXACTLY:
                mSingleRowHeight = Math.max(heightSize - getPaddingBottom() - getPaddingTop(), 0);
                break;
        }

        int height;
        if (mDisplayRowNum < 1) {
            height = mSingleRowHeight + getPaddingTop() + getPaddingBottom();
        } else {
            height = mSingleRowHeight * mDisplayRowNum + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);

        if (isFirstMeasure) {
            removeUselessViews();
            isFirstMeasure = false;
            return;
        }
        int childCount = getChildCount();

        View chlid;
        for (int i = 0; i < childCount; i++) {
            chlid = getChildAt(i);
            measureChildWithMargins(chlid, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

    }


    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {

        if (isFirstLayout) {
            isFirstLayout = false;
            if (mPathRecord.isEmpty()) {
                addOneLevel();
            }
            return;
        }

        int childCount = getChildCount();
        int topBase = getPaddingTop();
        int leftBase = getPaddingLeft();
        int width = r - l;
        int height = b - t;

        int offset;

        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                offset = mDisplayRowNum - 1 - i;
                child.layout(leftBase + child.getPaddingLeft(), topBase + offset * mSingleRowHeight + child.getPaddingTop(),
                        width - getPaddingRight() - child.getPaddingRight(), topBase + (offset + 1) * mSingleRowHeight - child.getPaddingBottom());
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean success = super.drawChild(canvas, child, drawingTime);
        if (needLine)
            drawLine(canvas);
        return success;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null) {
            SaveState ss = (SaveState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            mTheme = ss.theme;
            colorSet = mTheme.getBackGroundColors();
            restoreAllInfo(ss.pathRecord);
        }
        super.onRestoreInstanceState(state);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSate = super.onSaveInstanceState();
        SaveState ss = new SaveState(superSate);
        ss.pathRecord = mPathRecord;
        ss.theme = mTheme;
        return ss;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Set<Map.Entry<Long, AbstractBottomMenuItem>> entrySet = mBottomMenuItems.entrySet();
        for (Map.Entry<Long, AbstractBottomMenuItem> e : entrySet) {
            e.getValue().onViewDestroy();
        }

        removeAllLevels(mDisplayRowNum);
    }

    @SuppressLint("UseSparseArrays")
    private void init() {
        mMenuTree = new MenuItemTree();
        mDisplayMenus = new ArrayList<>();
        mPathRecord = new ArrayDeque<>();
        mBottomMenuItems = new HashMap<>();

        mInnerListener = new AbstractBottomMenuItem.OnItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                onItemClickHandle(item);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(item);
                }
            }

        };

        if (mCurMenuItem == null || mCurMenuItem.isLeafNode())
            mDisplayRowNum = 0;

        mCurMenuItem = mMenuTree.getRootItem();
    }

    private void onItemClickHandle(MenuItem item) {
        if (!item.equals(mCurMenuItem)) {
            if (!item.isLeafNode()) {
                boolean isOldItemChild = item.getDeep() > mCurMenuItem.getDeep();
                boolean isOldItemElder = (!isOldItemChild && item.isEldOf(mCurMenuItem));
                mCurMenuItem = item;
                if (isOldItemChild)
                    addOneLevel();
                else {
                    removeAllLevels(Math.max(0, mDisplayRowNum - mCurMenuItem.getDeep()));
                    if (!isOldItemElder)
                        addOneLevel();
                    else {
                        mCurMenuItem = mCurMenuItem.getParent();
                    }
                }
            } else {
                AbstractBottomMenuItem newItem = getBottomMenuItem(item);
                newItem.setSelected(!newItem.isSelected());
            }
        } else {
            if (!item.isLeafNode()) {
                removeAllLevels(mDisplayRowNum - mCurMenuItem.getDeep());
                mCurMenuItem = mCurMenuItem.getParent();
            } else {
                AbstractBottomMenuItem newItem = getBottomMenuItem(item);
                newItem.setSelected(!newItem.isSelected());
            }
        }
    }

    private void removeAllLevels(int num) {
        if (num >= mDisplayRowNum || num < 1)
            return;
        int b = mDisplayRowNum - num;
        for (int i = mDisplayRowNum - 1; i >= b; i--) {
            if (mDisplayMenus.get(i).getChildAt(0) instanceof HorizontalScrollView) {
                View v = ((HorizontalScrollView) mDisplayMenus.get(i).getChildAt(0)).getChildAt(0);
                if (v != null && v instanceof LinearLayout)
                    ((LinearLayout) v).removeAllViews();
            }
            mDisplayMenus.get(i).removeAllViews();
            mDisplayMenus.remove(i);
            removeView(getChildAt(i));
            getBottomMenuItem(mPathRecord.peek()).setSelected(false);
            mPathRecord.pop();
            mDisplayRowNum--;
        }
    }


    private void addOneLevel() {
        if (mCurMenuItem == null || mCurMenuItem.isLeafNode() || mCurMenuItem.getDeep() > MAX_LEVELS - 1)
            return;

        LinearLayout linearLayout = new LinearLayout(getContext());
        if (mTheme == null)
            createDefaultTheme();
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(getColorByDeep(mCurMenuItem.getDeep()));
        linearLayout.setPadding(INNER_LAYOUT_PADDING_L, INNER_LAYOUT_PADDING_T, INNER_LAYOUT_PADDING_R, INNER_LAYOUT_PADDING_B);
        linearLayout.setLayoutParams(new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mDisplayMenus.add(linearLayout);
        fillMenu(linearLayout, mCurMenuItem);
        addView(linearLayout);
        mPathRecord.push(mCurMenuItem);
        if (mCurMenuItem != null && !mCurMenuItem.equals(mMenuTree.getRootItem()))
            getBottomMenuItem(mCurMenuItem).setSelected(true);
        mDisplayRowNum++;

    }

    private void fillTopMenu(MenuItem parent) {
        if (mDisplayRowNum >= 1)
            fillMenu(mDisplayMenus.get(mDisplayRowNum - 1), parent);
    }

    private void fillMenu(LinearLayout menu, MenuItem parent) {
        if (parent.getNextLevel() == null) return;
        final int count = parent.getNextLevel().size();
        View v;

        if (count > MAX_NUM_ONE_ROW) {
            HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
            scrollView.setSmoothScrollingEnabled(true);
            scrollView.setHorizontalScrollBarEnabled(false);
            menu.addView(scrollView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            LinearLayout linearLayout = new LinearLayout(getContext());
            for (MenuItem item :
                    parent.getNextLevel()) {
                AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
                bottomMenuItem.setOnItemClickListener(mInnerListener);
                bottomMenuItem.setTheme(mTheme);
                //当恢复时Context对象无法序列化，这里临时传入
                if (bottomMenuItem.getContext() == null)
                    bottomMenuItem.setContext(getContext());
                bottomMenuItem.onDisplayPrepare();

                v = bottomMenuItem.getMainView();

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                v.setLayoutParams(lp);
                if (mSingleRowHeight == 0) {
                    mSingleRowHeight = getLayoutParams().height;
                }
                int padding = (int) (mSingleRowHeight * (1 - INNER_LAYOUT_PADDING_RATE) / 2);
                v.setPadding(padding / 3, padding, padding / 3, padding);
                linearLayout.addView(v);
            }
            scrollView.addView(linearLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        } else {
            for (MenuItem item :
                    parent.getNextLevel()) {
                AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
                bottomMenuItem.setOnItemClickListener(mInnerListener);
                //当恢复时Context对象无法序列化，这里临时传入
                bottomMenuItem.setTheme(mTheme);
                if (bottomMenuItem.getContext() == null)
                    bottomMenuItem.setContext(getContext());
                bottomMenuItem.onDisplayPrepare();

                v = bottomMenuItem.getMainView();
                if (mSingleRowHeight == 0) {
                    mSingleRowHeight = getLayoutParams().height;
                }
                int padding = (int) (mSingleRowHeight * (1 - INNER_LAYOUT_PADDING_RATE) / 2);
                v.setPadding(padding / 3, padding, padding / 3, padding);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                lp.weight = 1;
                v.setLayoutParams(lp);
                menu.addView(v);
            }
        }

    }

    private int getAllChildrenWidthSum(boolean withPadding, boolean withMargin) {
        final int childCount = getChildCount();
        int width = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            int margin = ((MarginLayoutParams) child.getLayoutParams()).leftMargin +
                    ((MarginLayoutParams) child.getLayoutParams()).rightMargin;

            width += child.getMeasuredWidth() +
                    (withPadding ? child.getPaddingLeft() + child.getPaddingRight() : 0) +
                    (withMargin ? margin : 0);
        }

        return Math.max(width, 0);
    }

    private int getAllChildrenHeightSum(boolean withPadding, boolean withMargin) {
        final int childCount = getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;

            int margin = ((MarginLayoutParams) child.getLayoutParams()).topMargin +
                    ((MarginLayoutParams) child.getLayoutParams()).bottomMargin;

            height += child.getMeasuredHeight() +
                    (withPadding ? child.getPaddingTop() + child.getPaddingBottom() : 0) +
                    (withMargin ? margin : 0);
        }
        return Math.max(height, 0);
    }

    private int getColorByDeep(int deep) {
        return colorSet[deep % colorSet.length];
    }

    private void drawLine(Canvas canvas) {
        if (getLayoutTransition() != null && getLayoutTransition().isRunning()) return;
        mPaint.setColor(Utils.getDarkerColor(getColorByDeep(mDisplayRowNum), 0.2f));
        mPaint.setAlpha(80);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            canvas.drawLine(getPaddingStart(), getPaddingTop(), getWidth() - getPaddingEnd(), getPaddingTop(), mPaint);
        } else {
            canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop(), mPaint);
        }
    }

    private ITheme createDefaultTheme() {
        mTheme = new LightTheme();
        colorSet = mTheme.getBackGroundColors();
        return mTheme;
    }

    private void removeUselessViews() {
        int childCount = getChildCount();
        if (childCount > mDisplayMenus.size()) {
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (!mDisplayMenus.contains(v))
                    removeView(v);
            }
        }

    }


    private AbstractBottomMenuItem getBottomMenuItem(MenuItem item) {
        return mBottomMenuItems.get(item.getId());
    }

    private void restoreAllInfo(ArrayDeque<MenuItem> pathRecord) {
        mPathRecord.clear();
        while (!pathRecord.isEmpty()) {
            mCurMenuItem = pathRecord.getLast();
            addOneLevel();
            pathRecord.removeLast();
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public static class SaveState extends BaseSavedState {
        ArrayDeque<MenuItem> pathRecord;

        ITheme theme;

        public SaveState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeSerializable(this.pathRecord);
            out.writeParcelable(this.theme, flags);
        }


        protected SaveState(Parcel parcel) {
            super(parcel);
            this.pathRecord = (ArrayDeque<MenuItem>) parcel.readSerializable();
            this.theme = parcel.readParcelable(ITheme.class.getClassLoader());
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel parcel) {
                return new SaveState(parcel);
            }

            @Override
            public SaveState[] newArray(int i) {
                return new SaveState[i];
            }
        };
    }

    //----------------------------------------------------------------------------------------------------------------------------
    public void setInnerLayoutPadding(int l, int t, int r, int b) {
        INNER_LAYOUT_PADDING_L = l;
        INNER_LAYOUT_PADDING_R = r;
        INNER_LAYOUT_PADDING_T = t;
        INNER_LAYOUT_PADDING_B = b;
        invalidate();
    }

    public void setOnItemClickListener(AbstractBottomMenuItem.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setItemSelected(long id, boolean isSelected) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null)
            bottomMenuItem.setSelected(isSelected);
    }

    //can't work now
    public void setEnabled(boolean enabled) {
        Set<Map.Entry<Long, AbstractBottomMenuItem>> entrySet = mBottomMenuItems.entrySet();
        for (Map.Entry<Long, AbstractBottomMenuItem> e :
                entrySet) {
            if (e.getValue().getMainView() != null)
                e.getValue().getMainView().setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void show(long d) {
        AnimatorUtil.show(this, d);
    }

    public void hide(long d) {
        AnimatorUtil.hide(this, d);
    }

    public int isItemSelected2(MenuItem item) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
        if (bottomMenuItem != null)
            return bottomMenuItem.isSelected() ? 1 : 0;
        else
            return -1;
    }

    /**
     * @param id 搜索的id,the search id
     * @return 1:searched 0:not searched ,-1 the id not find
     */
    public int isItemSelected2(long id) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null)
            return bottomMenuItem.isSelected() ? 1 : 0;
        else
            return -1;
    }

    /**
     * @param item search item
     * @return can be searched ? if not exit it will throw an exception;
     */
    public boolean isItemSelected(MenuItem item) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
        if (bottomMenuItem != null)
            return bottomMenuItem.isSelected();
        else
            throw new RuntimeException("no item match");
    }

    public boolean isItemSelected(long id) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null)
            return bottomMenuItem.isSelected();
        else
            throw new RuntimeException("no item match");
    }


    /**
     * @param num 数量
     *            设置当单行数量达到的最大阈值来开启滚动模式
     *            set the max num of a row to enable the scroll-mode for a line
     */
    public void setScrollModeNum(int num) {
        MAX_NUM_ONE_ROW = num;
    }

    public void setMenuBackGroundColor(int... colors) {
        colorSet = colors;
        if (!isFirstMeasure) {
            final int childrenCount = getChildCount();
            for (int i = 0; i < childrenCount; i++) {
                getChildAt(i).setBackgroundColor(getColorByDeep(i));
            }
            invalidate();
        }
    }


    /**
     * @param theme 主题色：custom theme
     */
    @Override
    public void setTheme(ITheme theme) {
        mTheme = theme;
        colorSet = theme.getBackGroundColors();

        if (mDisplayMenus != null && !mDisplayMenus.isEmpty()) {
            Set<Map.Entry<Long, AbstractBottomMenuItem>> entries = mBottomMenuItems.entrySet();
            for (Map.Entry<Long, AbstractBottomMenuItem> e :
                    entries) {
                e.getValue().setThemeForDisplay(mTheme);
            }
        }

        int i = 0;
        if (mDisplayMenus != null)
            setMenuBackGroundColor(colorSet);
    }

    public IMultiMenu addRootItem(AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) return this;
        MenuItem menuItem = bottomMenuItem.getMenuItem();
        mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);

        mMenuTree.addRootItem(menuItem);
        return this;
    }

    public IMultiMenu addItem(MenuItem parentItem, AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) return this;
        MenuItem menuItem = bottomMenuItem.getMenuItem();
        if (parentItem == mMenuTree.getRootItem())
            addRootItem(bottomMenuItem);
        else {
            mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            mMenuTree.addByParent(parentItem, menuItem);
        }
        return this;
    }

    public IMultiMenu addItem(long parentId, AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) return this;
        MenuItem menuItem = bottomMenuItem.getMenuItem();

        mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
        mMenuTree.addByParentId(parentId, menuItem);
        return this;
    }

    public IMultiMenu addItems(long parentId, AbstractBottomMenuItem... menuItems) {
        for (AbstractBottomMenuItem bottomMenuItem :
                menuItems) {
            addItem(parentId, bottomMenuItem);
        }
        return this;
    }

    public IMultiMenu addItems(MenuItem parentItem, AbstractBottomMenuItem... menuItems) {
        MenuItem menuItem;
        for (AbstractBottomMenuItem bottomMenuItem :
                menuItems) {
            menuItem = bottomMenuItem.getMenuItem();

            mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            mMenuTree.addByParent(parentItem, menuItem);
        }
        return this;
    }

    public static class Builder {
        private BottomMenu bottomMenu;

        public Builder(Context context) {
            bottomMenu = new BottomMenu(context);
            if (bottomMenu.mMenuTree == null)
                bottomMenu.mMenuTree = new MenuItemTree();
            bottomMenu.mCurMenuItem = bottomMenu.mMenuTree.getRootItem();
            if (bottomMenu.mCurMenuItem == null || bottomMenu.mCurMenuItem.getNextLevel().isEmpty())
                bottomMenu.mDisplayRowNum = 0;
        }

        public Builder addRootItem(MenuItem menuItem) {
            bottomMenu.mMenuTree.addRootItem(menuItem);
            if (bottomMenu.mDisplayRowNum < 1)
                bottomMenu.mDisplayRowNum = 1;
            return this;
        }

        public Builder addItem(MenuItem parentItem, AbstractBottomMenuItem bottomMenuItem) {
            MenuItem menuItem = bottomMenuItem.getMenuItem();
            bottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            if (parentItem == bottomMenu.mMenuTree.getRootItem())
                addRootItem(menuItem);
            else
                bottomMenu.mMenuTree.addByParent(parentItem, menuItem);
            return this;
        }

        public Builder addItem(long parentId, AbstractBottomMenuItem bottomMenuItem) {
            MenuItem menuItem = bottomMenuItem.getMenuItem();
            bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                @Override
                public void onItemClick(MenuItem menuItem) {
                    bottomMenu.mCurMenuItem = menuItem;
                    if (bottomMenu.mOnItemClickListener != null)
                        bottomMenu.mOnItemClickListener.onItemClick(menuItem);
                }
            });

            bottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            bottomMenu.mMenuTree.addByParentId(parentId, menuItem);
            return this;
        }

        public Builder addItems(long parentId, AbstractBottomMenuItem... menuItems) {
            MenuItem menuItem;
            for (AbstractBottomMenuItem bottomMenuItem :
                    menuItems) {
                menuItem = bottomMenuItem.getMenuItem();
                bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem) {
                        bottomMenu.mCurMenuItem = menuItem;
                        if (bottomMenu.mOnItemClickListener != null)
                            bottomMenu.mOnItemClickListener.onItemClick(menuItem);
                    }
                });

                bottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
                bottomMenu.mMenuTree.addByParentId(parentId, menuItem);
            }
            return this;
        }

        public Builder addItems(MenuItem parentItem, AbstractBottomMenuItem... menuItems) {
            MenuItem menuItem;
            for (AbstractBottomMenuItem bottomMenuItem :
                    menuItems) {
                menuItem = bottomMenuItem.getMenuItem();
                bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem) {
                        bottomMenu.mCurMenuItem = menuItem;
                        if (bottomMenu.mOnItemClickListener != null)
                            bottomMenu.mOnItemClickListener.onItemClick(menuItem);
                    }
                });

                bottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
                bottomMenu.mMenuTree.addByParent(parentItem, menuItem);
            }
            return this;
        }

        public BottomMenu build() {
            return bottomMenu;
        }
    }

}
