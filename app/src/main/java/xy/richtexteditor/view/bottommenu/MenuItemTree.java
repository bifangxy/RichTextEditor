package xy.richtexteditor.view.bottommenu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Xieying on 2018/4/3
 * Function:
 */
public class MenuItemTree implements Parcelable {

    private MenuItem rootItem;

    public MenuItemTree() {
        init();
    }

    private void init() {
        this.rootItem = new MenuItem(null, null, 0x1183424701L, null);
        this.rootItem.setDeep(0);
    }

    public void setRoots(MenuItem... roots) {
        rootItem.setNextLevel(Arrays.asList(roots));
    }

    public void addRootItem(MenuItem item) {
        if (rootItem.getNextLevel() == null) {
            rootItem.setNextLevel(new ArrayList<MenuItem>());
        }
        item.setParent(rootItem);
        rootItem.getNextLevel().add(item);
    }

    public boolean addByParent(MenuItem parent, MenuItem addMenuItem) {
        return addByParentId(parent.getId(), addMenuItem);
    }

    public boolean addByParentId(Long id, MenuItem addMenuItem) {
        if (id == null)
            return false;
        MenuItem findItem = rootItem.getMenuItemById(id);
        if (findItem != null) {
            if (findItem.getNextLevel() == null)
                findItem.setNextLevel(new ArrayList<MenuItem>());
            addMenuItem.setParent(findItem);
            findItem.getNextLevel().add(addMenuItem);
            return true;
        }
        return false;
    }

    public boolean addByParent(MenuItem parent, MenuItem... menuItems) {
        return addByParentId(parent.getId(), menuItems);
    }

    public boolean addByParentId(Long id, MenuItem... menuItems) {
        if (id == null)
            return false;
        MenuItem findItem = rootItem.getMenuItemById(id);
        if (findItem != null) {
            if (findItem.getNextLevel() == null)
                findItem.setNextLevel(new ArrayList<MenuItem>());
            Collections.addAll(findItem.getNextLevel(), menuItems);
            return true;
        }
        return false;
    }

    public MenuItem getRootItem() {
        return rootItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.rootItem);
    }

    protected MenuItemTree(Parcel parcel) {
        this.rootItem = (MenuItem) parcel.readSerializable();
    }

    public static final Creator<MenuItemTree> CREATOR = new Creator<MenuItemTree>() {
        @Override
        public MenuItemTree createFromParcel(Parcel parcel) {
            return new MenuItemTree(parcel);
        }

        @Override
        public MenuItemTree[] newArray(int i) {
            return new MenuItemTree[i];
        }
    };
}
