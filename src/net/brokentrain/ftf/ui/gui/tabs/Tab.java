package net.brokentrain.ftf.ui.gui.tabs;

import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

abstract public class Tab {

    protected CTabItem tabItem;

    protected CTabFolder tabFolder;

    public Tab(CTabItem tabItem, TabManager tabManager) {
        this.tabItem = tabItem;

        tabFolder = tabManager.getTabFolder();
    }

    public void dispose() {

        if (!tabItem.isDisposed()) {
            tabItem.dispose();
        }
    }

    public CTabItem getTabItem() {
        return tabItem;
    }

    public String getTitle() {
        return tabItem.getText();
    }

    public boolean isDisposed() {
        return tabItem.isDisposed();
    }

    public void toggleHintStatus() {

        if (!WidgetUtil.isset(tabItem)) {
            return;
        }

        tabItem
                .setFont((tabItem == tabFolder.getSelection()) ? FontUtil.headerBoldFont
                        : FontUtil.headerFont);
    }

    public void toggleVisibility() {

    }
}
