package xy.richtexteditor.view.richeditor;

import android.support.v4.util.ArraySet;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
@SuppressWarnings("WeakAccess")
public class ItemIndex {
    public static final String HAS_REGISTER_EXCEPTION = "this id has been register";
    public static final String NO_RSGISTER_EXCEPTION = "this id has not been register";


    public static final long INSERT_IMAGE = 0x01;
    public static final long A = 0x02;
    public static final long MORE = 0x03;
    public static final long UNDO = 0x04;
    public static final long REDO = 0x05;
    public static final long BOLD = 0x06;
    public static final long ITALIC = 0x07;
    public static final long STRIKE_THROUGH = 0x08;
    public static final long BLOCK_QUOTE = 0x09;
    public static final long H1 = 0x0a;
    public static final long H2 = 0x0b;
    public static final long H3 = 0x0c;
    public static final long H4 = 0x0d;
    public static final long HALVING_LINE = 0x0e;
    public static final long LINK = 0x0f;
    public static final long THEME = 0x10;
    public static final long LEFT = 0x11;
    public static final long CENTER = 0x12;

    private long[] defaultItems = {INSERT_IMAGE, A, MORE, UNDO, REDO, BOLD, ITALIC,
            STRIKE_THROUGH, BLOCK_QUOTE, H1, H2, H3, H4, HALVING_LINE, LINK, THEME, LEFT, CENTER};

    private final static ItemIndex instance = new ItemIndex();

    private ArraySet<Long> registerSet = new ArraySet<>();
    private Register register;

    private ItemIndex() {
        init();
    }

    public static ItemIndex getInstance() {
        return instance;
    }

    public Register getRegister() {
        return register;
    }

    private void init() {
        for (long id : defaultItems) {
            registerSet.add(id);
        }
    }


    public class Register {
        Register() {

        }

        public boolean register(long id) {
            return registerSet.add(id);
        }

        public boolean hasRegister(long id) {
            return registerSet.contains(id);
        }

        public boolean isDefaultId(long id) {
            for (long i : defaultItems) {
                if (i == id)
                    return true;
            }
            return false;
        }
    }

}
