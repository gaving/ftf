package net.brokentrain.ftf.ui.gui.components;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

public class MenuManager {

    public static final int ZERO_TAB_OPENED = 0;

    public static final int MORE_THAN_ONE_TAB_OPENED = 1;

    public static final int SEARCH_TAB_FOCUSED = 2;

    public static final int SPECIAL_TAB_FOCUSED = 3;

    public static final int TREE_SELECTION_RESULT = 4;

    public static final int TREE_SELECTION_SERVICE = 5;

    public static final int TREE_SELECTION_EMPTY = 5;

    public static final int NO_INTERNAL_BROWSER = 6;

    public static final int ONE_TAB_OPENED = 7;

    private static MenuItem closeCurrentTab;

    private static MenuItem closeAllTabs;

    private static MenuItem closeCurrentTabWindowItem;

    private static MenuItem closeAllTabsWindowItem;

    private static MenuItem copy;

    private static MenuItem cut;

    private static MenuItem export;

    private static MenuItem delete;

    private static MenuItem gotoNextTab;

    private static MenuItem gotoPreviousTab;

    private static MenuItem paste;

    private static MenuItem save;

    private static MenuItem saveAs;

    private static MenuItem selectAll;

    private static ToolItem saveToolItem;

    public static void handleEditMenuState() {

        Control control = GUI.display.getFocusControl();

        if (!WidgetUtil.isset(control)) {
            setEditMenuDisabled();
            return;
        }

        if (control instanceof Text) {
            boolean isEditable = ((Text) control).getEditable();
            boolean isTextSelected = ((Text) control).getSelectionCount() > 0;

            setEditMenuEnabled(isEditable && isTextSelected, isTextSelected,
                    false, isEditable, true);
            return;
        }

        if (control instanceof StyledText) {
            boolean isEditable = ((StyledText) control).getEditable();
            boolean isTextSelected = ((StyledText) control).getSelectionCount() > 0;

            setEditMenuEnabled(isEditable && isTextSelected, isTextSelected,
                    false, isEditable, true);
            return;
        }

        if (control instanceof Combo) {
            setEditMenuEnabled(true, true, false, true, true);
            return;
        }

        if (control instanceof Tree) {
            Tree tree = (Tree) control;

            if ((tree.getSelectionCount() > 0)
                    && (tree.getSelection()[0].getData() != null)) {
                Object data = tree.getSelection()[0].getData();

                if ((data instanceof TreeItemData)
                        && (((TreeItemData) data).isService() || ((TreeItemData) data)
                                .isResult())) {
                    setEditMenuEnabled(false, false, true, false, false);
                }

                return;
            }
        }

        setEditMenuDisabled();
    }

    public static void notifyState(int state) {

        switch (state) {

        case ZERO_TAB_OPENED:
            closeCurrentTab.setEnabled(false);
            closeAllTabs.setEnabled(false);
            if (WidgetUtil.isset(closeCurrentTabWindowItem)) {
                closeCurrentTabWindowItem.setEnabled(false);
            }
            if (WidgetUtil.isset(closeAllTabsWindowItem)) {
                closeAllTabsWindowItem.setEnabled(false);
            }
            gotoPreviousTab.setEnabled(false);
            gotoNextTab.setEnabled(false);
            break;

        case ONE_TAB_OPENED:
            closeCurrentTab.setEnabled(true);
            closeAllTabs.setEnabled(true);
            if (WidgetUtil.isset(closeCurrentTabWindowItem)) {
                closeCurrentTabWindowItem.setEnabled(true);
            }
            if (WidgetUtil.isset(closeAllTabsWindowItem)) {
                closeAllTabsWindowItem.setEnabled(true);
            }
            gotoPreviousTab.setEnabled(false);
            gotoNextTab.setEnabled(false);
            break;

        case MORE_THAN_ONE_TAB_OPENED:
            gotoPreviousTab.setEnabled(true);
            gotoNextTab.setEnabled(true);
            closeAllTabs.setEnabled(true);
            if (WidgetUtil.isset(closeAllTabsWindowItem)) {
                closeAllTabsWindowItem.setEnabled(true);
            }
            break;

        case SEARCH_TAB_FOCUSED:
            if (WidgetUtil.isset(saveToolItem)) {
                saveToolItem.setEnabled(true);
            }
            save.setEnabled(true);
            saveAs.setEnabled(true);
            export.setEnabled(true);
            break;

        case SPECIAL_TAB_FOCUSED:

            if (WidgetUtil.isset(saveToolItem)) {
                saveToolItem.setEnabled(false);
            }
            save.setEnabled(false);
            saveAs.setEnabled(false);
            export.setEnabled(false);
            break;

        case TREE_SELECTION_RESULT:
        case TREE_SELECTION_SERVICE:
            break;

        case NO_INTERNAL_BROWSER:
            break;
        }
    }

    public static void registerCloseTab(MenuItem closeCurrentTab,
            MenuItem closeAllTabs) {
        MenuManager.closeCurrentTab = closeCurrentTab;
        MenuManager.closeAllTabs = closeAllTabs;
    }

    public static void registerCloseTabWindowItems(MenuItem closeCurrentTab,
            MenuItem closeAllTabs) {
        MenuManager.closeCurrentTabWindowItem = closeCurrentTab;
        MenuManager.closeAllTabsWindowItem = closeAllTabs;
    }

    public static void registerEditMenu(MenuItem copy, MenuItem paste,
            MenuItem cut, MenuItem selectAll, MenuItem delete) {
        MenuManager.copy = copy;
        MenuManager.paste = paste;
        MenuManager.cut = cut;
        MenuManager.selectAll = selectAll;
        MenuManager.delete = delete;
    }

    public static void registerExport(MenuItem export) {
        MenuManager.export = export;
    }

    public static void registerGotoTab(MenuItem gotoPreviousTab,
            MenuItem gotoNextUnreadTab) {
        MenuManager.gotoPreviousTab = gotoPreviousTab;
        MenuManager.gotoNextTab = gotoNextUnreadTab;
    }

    public static void registerSave(MenuItem save, MenuItem saveAs) {
        MenuManager.save = save;
        MenuManager.saveAs = saveAs;
    }

    public static void registerSaveToolItem(ToolItem saveToolItem) {
        MenuManager.saveToolItem = saveToolItem;
    }

    private static void setEditMenuDisabled() {
        setEditMenuEnabled(false, false, false, false, false);
    }

    private static void setEditMenuEnabled(boolean cut, boolean copy,
            boolean delete, boolean paste, boolean selectAll) {
        MenuManager.cut.setEnabled(cut);
        MenuManager.copy.setEnabled(copy);
        MenuManager.delete.setEnabled(delete);
        MenuManager.paste.setEnabled(paste);
        MenuManager.selectAll.setEnabled(selectAll);
    }

    public MenuManager(GUI fetcherGui, Shell shell, EventManager eventManager) {
    }
}
