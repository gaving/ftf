package net.brokentrain.ftf.ui.gui.util;

import java.util.Hashtable;

import org.eclipse.swt.SWT;

public class HotkeyUtil {

    public static Hashtable<String, String[]> hotKeys = new Hashtable<String, String[]>();

    public static final String NULL_ACCELERATOR_NAME = "";

    public static final int NULL_ACCELERATOR_VALUE = 0;

    public static String getHotkeyName(String itemName) {
        if (!isHotkeySet(itemName)) {
            return NULL_ACCELERATOR_NAME;
        }

        return (hotKeys.get(itemName))[0];
    }

    public static int getHotkeyValue(String itemName) {
        if (!isHotkeySet(itemName)) {
            return NULL_ACCELERATOR_VALUE;
        }

        return Integer.parseInt((hotKeys.get(itemName))[1]);
    }

    public static void initDefaultAccelerators() {
        String ctrl = "Ctrl";
        String shift = "Shift";

        setHotkey("MENU_NEW_TEXT", ctrl + "+T", SWT.CTRL | 't');
        setHotkey("MENU_NEW_LOCAL", ctrl + "+" + shift + "+L", SWT.CTRL
                | SWT.SHIFT | 'l');
        setHotkey("MENU_NEW_PROCESSING", ctrl + "+" + shift + "+P", SWT.CTRL
                | SWT.SHIFT | 'p');
        setHotkey("MENU_OPEN", ctrl + "+O", SWT.CTRL | 'o');
        setHotkey("MENU_OPEN_MODEL", ctrl + "+" + shift + "+O", SWT.CTRL
                | SWT.SHIFT | 'o');
        setHotkey("MENU_CLOSE", ctrl + "+W", SWT.CTRL | 'w');
        setHotkey("MENU_CLOSE_ALL", ctrl + "+" + shift + "+W", SWT.CTRL
                | SWT.SHIFT | 'w');
        setHotkey("MENU_SAVE", ctrl + "+S", SWT.CTRL | 's');
        setHotkey("MENU_SAVE_AS", ctrl + "+" + shift + "+S", SWT.CTRL
                | SWT.SHIFT | 's');
        setHotkey("MENU_SAVE_MODEL", ctrl + "+E", SWT.CTRL | 'e');
        setHotkey("MENU_SAVE_MODEL_AS", ctrl + "+" + shift + "+E", SWT.CTRL
                | SWT.SHIFT | 'e');
        setHotkey("MENU_EXIT", ctrl + "+Q", SWT.CTRL | 'q');

        setHotkey("MENU_EDIT_CUT", ctrl + "+X", SWT.CTRL | 'x');
        setHotkey("MENU_EDIT_COPY", ctrl + "+C", SWT.CTRL | 'c');
        setHotkey("MENU_EDIT_PASTE", ctrl + "+V", SWT.CTRL | 'v');
        setHotkey("MENU_EDIT_SELECT_ALL", ctrl + "+A", SWT.CTRL | 'a');

        setHotkey("MENU_PREVIOUS_TAB", "F2", SWT.F2);
        setHotkey("MENU_NEXT_TAB", "F3", SWT.F3);

        setHotkey("BUTTON_SEARCH", ctrl + "+F", SWT.CTRL | 'f');
        setHotkey("MENU_BROWSER", ctrl + "+B", SWT.CTRL | 'b');

        setHotkey("MENU_MINIMIZE_FTF", ctrl + "+" + shift + "+X", SWT.CTRL
                | SWT.SHIFT | 'x');
    }

    public static boolean isHotkeySet(String itemName) {
        if (!hotKeys.containsKey(itemName)) {
            return false;
        }

        return Integer.parseInt((hotKeys.get(itemName))[1]) != NULL_ACCELERATOR_VALUE;
    }

    public static void setHotkey(String itemName, String keyName, int keyValue) {
        setHotkey(itemName, keyName, String.valueOf(keyValue));
    }

    public static void setHotkey(String itemName, String keyName,
            String keyValue) {
        hotKeys.put(itemName, new String[] { keyName, keyValue });
    }

    private HotkeyUtil() {
    }
}
