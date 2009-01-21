package net.brokentrain.ftf.ui.gui.tabs;

import java.util.HashMap;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.components.MenuManager;
import net.brokentrain.ftf.ui.gui.components.MainToolbar;
import net.brokentrain.ftf.ui.gui.components.MainMenu;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class TabManager {

    private GUI fetcherGui;

    private Shell shell;

    private MainMenu mainMenu;

    private BrowserTab browserTab;

    private LogTab logTab;

    private StatusTab statusTab;

    private TransferTab transferTab;

    private CTabFolder tabFolder;

    private HashMap<CTabItem, SearchTab> searchTabs;

    private HashMap<CTabItem, ProcessingTab> processingTabs;

    private MenuItem close;

    private MenuItem closeAll;

    private MenuItem closeAllKeepCurrent;

    private MenuItem tabPositionBottom;

    private MenuItem tabPositionMenu;

    private MenuItem tabPositionTop;

    private Menu tabFolderMenu;

    private MainToolbar toolBar;

    private SashForm contentPane;

    public TabManager(GUI fetcherGui, Shell shell, EventManager eventManager) {

        this.fetcherGui = fetcherGui;
        this.shell = shell;

        toolBar = fetcherGui.getToolbar();
        mainMenu = fetcherGui.getMainMenu();
        searchTabs = new HashMap<CTabItem, SearchTab>();
        processingTabs = new HashMap<CTabItem, ProcessingTab>();

        initComponents();
    }

    public void closeAll() {
        closeAll(false);
    }

    public void closeAll(boolean keepCurrent) {
        CTabItem items[] = tabFolder.getItems();
        int selectedIndex = tabFolder.getSelectionIndex();

        for (int a = 0; a < items.length; a++) {

            if (keepCurrent && (a != selectedIndex)) {
                closeTab(items[a]);
            } else if (!keepCurrent) {
                closeTab(items[a]);
            }
        }
    }

    public void closeCurrent() {
        if (tabFolder.getSelection() != null) {
            closeTab(tabFolder.getSelection());
        }
    }

    public void closeTab(CTabItem tabItem) {
        int index = tabFolder.getSelectionIndex();
        boolean isTabSelected = ((tabFolder.getSelection() != null) && tabFolder
                .getSelection().equals(tabItem));

        if (!WidgetUtil.isset(tabItem)) {
            return;
        }

        if (searchTabs.containsKey(tabItem)) {

            /* Remove the search tab from the list */
            searchTabs.remove(tabItem);

            /* Dispose of the control, no use in keeping it around */
            if (tabItem.getControl() != null) {
                tabItem.getControl().dispose();
            }
        }

        tabItem.dispose();

        if (isTabSelected && (index >= 0) && (index < tabFolder.getItemCount())) {
            tabFolder.setSelection(index);
        }

        updateTabFolderState();
        updateInterfaceState();
    }

    public void createBrowserTab(String location, boolean firstLoad) {
        if ((browserTab == null) || browserTab.isDisposed()) {
            CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
            browserTab = new BrowserTab(fetcherGui, tabItem, this, location,
                    firstLoad);
        }
    }

    public FullTextTab createFullTextTab() {
        CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabFolder.setSelection(tabItem);
        FullTextTab fullTextTab = new FullTextTab(fetcherGui, tabItem, this,
                false);
        searchTabs.put(tabItem, fullTextTab);
        return fullTextTab;
    }

    public void createLogTab() {
        if ((logTab == null) || logTab.isDisposed()) {
            CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
            if (logTab == null) {
                logTab = new LogTab(fetcherGui, tabItem, this);
            } else if (logTab.isDisposed()) {
                logTab.setTabItem(tabItem);
            }
        }
    }

    public ProcessingTab createProcessingTab() {
        CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
        tabFolder.setSelection(tabItem);
        ProcessingTab processingTab = new ProcessingTab(fetcherGui, tabItem,
                this, false);
        processingTabs.put(tabItem, processingTab);
        searchTabs.put(tabItem, processingTab);
        return processingTab;
    }

    public void createStatusTab() {
        if ((statusTab == null) || statusTab.isDisposed()) {
            CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
            if (statusTab == null) {
                statusTab = new StatusTab(fetcherGui, tabItem, this);
            } else if (statusTab.isDisposed()) {
                statusTab.setTabItem(tabItem);
            }
        }
    }

    public void createTransferTab() {
        if ((transferTab == null) || transferTab.isDisposed()) {
            CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
            if (transferTab == null) {
                transferTab = new TransferTab(fetcherGui, tabItem, this);
            } else if (transferTab.isDisposed()) {
                transferTab.setTabItem(tabItem);
            }
        }
    }

    public ProcessingTab getActiveProcessingTab() {

        CTabItem tabItem = tabFolder.getSelection();

        if ((WidgetUtil.isset(tabItem))
                && (processingTabs.containsKey(tabItem))) {
            return processingTabs.get(tabItem);
        }
        return null;
    }

    public SearchTab getActiveSearchTab() {

        CTabItem tabItem = tabFolder.getSelection();

        if ((WidgetUtil.isset(tabItem)) && (searchTabs.containsKey(tabItem))) {
            return searchTabs.get(tabItem);
        }
        return null;
    }

    public CTabItem getActiveTab() {

        CTabItem tabItem = tabFolder.getSelection();

        if (WidgetUtil.isset(tabItem)) {
            return tabItem;
        }
        return null;
    }

    public BrowserTab getBrowserTab() {
        return browserTab;
    }

    public CTabItem[] getItems() {
        return tabFolder.getItems();
    }

    public LogTab getLogTab() {
        return logTab;
    }

    public StatusTab getStatusTab() {
        return statusTab;
    }

    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    public TransferTab getTransferTab() {
        return transferTab;
    }

    public void gotoNextTab() {
        int selection = tabFolder.getSelectionIndex();
        int newSelection = -1;

        if (tabFolder.getItemCount() > selection + 1) {
            newSelection = selection + 1;
        } else if (tabFolder.getItemCount() > 1) {
            newSelection = 0;
        }

        if (newSelection >= 0) {
            tabFolder.showItem(tabFolder.getItem(newSelection));
            tabFolder.setSelection(tabFolder.getItem(newSelection));
            updateTabFolderState();
            updateInterfaceState();
        }
    }

    public void gotoPreviousTab() {
        int selection = tabFolder.getSelectionIndex();
        int newSelection = -1;

        if (selection > 0) {
            newSelection = selection - 1;
        } else if ((selection == 0) && (tabFolder.getItemCount() > 1)) {
            newSelection = tabFolder.getItemCount() - 1;
        }

        if (newSelection >= 0) {
            tabFolder.showItem(tabFolder.getItem(newSelection));
            tabFolder.setSelection(tabFolder.getItem(newSelection));
            updateTabFolderState();
            updateInterfaceState();
        }
    }

    public void initComponents() {

        /* Create parent content pane */
        contentPane = new SashForm(shell, SWT.VERTICAL | SWT.SMOOTH);
        contentPane.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_BOTH, 4));

        /* Create tab folder, handling every tab */
        tabFolder = new CTabFolder(contentPane,
                !SettingsRegistry.showTabCloseButton ? SWT.NONE : SWT.CLOSE);
        tabFolder.setSimple(SettingsRegistry.simpleTabs);
        tabFolder
                .setTabPosition((SettingsRegistry.tabPositionIsTop == true) ? SWT.TOP
                        : SWT.BOTTOM);
        tabFolder.setUnselectedCloseVisible(true);
        tabFolder.setLayoutData(new FillLayout());
        tabFolder.setMRUVisible(true);
        tabFolder.setMinimumCharacters(10);
        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTabFolderState();
                updateInterfaceState();
            }
        });

        tabFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {

                /* Close tab on middle button */
                if (e.button == 2) {

                    /* Retrieve tab for this location */
                    CTabItem item = tabFolder.getItem(new Point(e.x, e.y));

                    /* Close the tab if available */
                    if (WidgetUtil.isset(item)) {
                        closeTab(item);
                    }
                }
            }
        });

        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {

                /* Stop default close mechanism */
                event.doit = false;

                /* Close tab */
                closeTab((CTabItem) event.item);
            }
        });

        /* Create context menu for tab folder */
        initTabFolderPopup();

        /* Create browser pane and optionally show a welcome page */
        createBrowserTab(null, true);

        /* Create a new tab for the log information */
        createLogTab();

        /* Create a new tab for the status information */
        createStatusTab();

        /* Create a new tab for the transfer */
        createTransferTab();

        toggleBrowserTab();
        toggleLogTab();
        toggleStatusTab();
        toggleTransferTab();
    }

    private void initTabFolderPopup() {
        /* Create menu */
        tabFolderMenu = new Menu(tabFolder);
        tabFolderMenu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {

                /* Hide the menu if there is no tabs */
                if ((tabFolder.getItemCount() <= 0)
                        || (tabFolder.getSelectionIndex() == -1)) {
                    tabFolderMenu.setVisible(false);
                    return;
                }

                /* Disable close all if there is only the one tab */
                if (tabFolder.getItemCount() == 1) {
                    closeAllKeepCurrent.setEnabled(false);
                } else if (!closeAllKeepCurrent.getEnabled()) {
                    closeAllKeepCurrent.setEnabled(true);
                }
            }
        });

        /* Close menu item */
        close = new MenuItem(tabFolderMenu, SWT.POP_UP);
        close.setText("Close");
        close.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                /* Only if there is a current selection */
                if (tabFolder.getSelection() != null) {
                    closeCurrent();
                }
            }
        });

        new MenuItem(tabFolderMenu, SWT.SEPARATOR);

        /* Close all but keep the current menu item */
        closeAllKeepCurrent = new MenuItem(tabFolderMenu, SWT.POP_UP);
        closeAllKeepCurrent.setText("Keep current");
        closeAllKeepCurrent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                closeAll(true);
            }
        });

        /* Close all */
        closeAll = new MenuItem(tabFolderMenu, SWT.POP_UP);
        closeAll.setText("Close all");
        closeAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                closeAll();
            }
        });

        new MenuItem(tabFolderMenu, SWT.SEPARATOR);

        /* Position menu */
        tabPositionMenu = new MenuItem(tabFolderMenu, SWT.CASCADE);
        tabPositionMenu.setText("Position");

        Menu selectTabPosition = new Menu(shell, SWT.DROP_DOWN);
        tabPositionMenu.setMenu(selectTabPosition);

        tabPositionTop = new MenuItem(selectTabPosition, SWT.RADIO);
        tabPositionTop.setText("Top");
        tabPositionTop.setSelection(SettingsRegistry.tabPositionIsTop);
        tabPositionTop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tabPositionTop.getSelection()) {
                    tabFolder.setTabPosition(SWT.TOP);
                    SettingsRegistry.tabPositionIsTop = true;
                    tabFolder.layout();
                }
            }
        });

        tabPositionBottom = new MenuItem(selectTabPosition, SWT.RADIO);
        tabPositionBottom.setText("Bottom");
        tabPositionBottom.setSelection(!SettingsRegistry.tabPositionIsTop);
        tabPositionBottom.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tabPositionBottom.getSelection()) {
                    tabFolder.setTabPosition(SWT.BOTTOM);
                    SettingsRegistry.tabPositionIsTop = false;
                    tabFolder.layout();
                }
            }
        });

        selectTabPosition.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {
                tabPositionTop.setSelection(SettingsRegistry.tabPositionIsTop);
                tabPositionBottom
                        .setSelection(!SettingsRegistry.tabPositionIsTop);
            }
        });

        tabFolder.setMenu(tabFolderMenu);
    }

    public boolean isActiveTab(CTabItem tabItem) {

        CTabItem selectedTab = tabFolder.getSelection();

        if (WidgetUtil.isset(selectedTab)) {
            return tabItem.equals(selectedTab);
        }
        return false;
    }

    public void setSelection(CTabItem tabItem) {

        if (WidgetUtil.isset(tabItem)) {
            tabFolder.setSelection(tabItem);
        }

        updateInterfaceState();
    }

    public void setSelection(Tab tab) {

        CTabItem tabItem = tab.getTabItem();

        if (WidgetUtil.isset(tabItem)) {
            tabFolder.setSelection(tabItem);
        }
    }

    public void toggleBrowserTab() {
        if ((browserTab == null) || browserTab.isDisposed()) {
            createBrowserTab(null, false);
            tabFolder.setSelection(browserTab.getTabItem());
        } else {
            closeTab(browserTab.getTabItem());
        }
    }

    public void toggleLogTab() {
        if ((logTab == null) || logTab.isDisposed()) {
            createLogTab();
            tabFolder.setSelection(logTab.getTabItem());
        } else {
            closeTab(logTab.getTabItem());
        }
    }

    public void toggleStatusTab() {
        if ((statusTab == null) || statusTab.isDisposed()) {
            createStatusTab();
            tabFolder.setSelection(statusTab.getTabItem());
        } else {
            closeTab(statusTab.getTabItem());
        }
    }

    public void toggleTransferTab() {
        if ((transferTab == null) || transferTab.isDisposed()) {
            createTransferTab();
            tabFolder.setSelection(transferTab.getTabItem());
        } else {
            closeTab(transferTab.getTabItem());
        }
    }

    public void updateInterfaceState() {
        SearchTab searchTab = getActiveSearchTab();

        if (searchTab != null) {
            searchTab.updateTitle();
        } else {
            GUI.shell.setText(WidgetUtil.getShellTitle());
        }

        /* Rebuild the menu bar with proper items */
        mainMenu.buildMenuBar();

        /* Show or hide the processing tab toolitems as needed */
        toolBar.setProcessingToolbarVisible(getActiveProcessingTab() != null);
    }

    public void updateTabFolderState() {

        if (tabFolder.getItemCount() > 1) {
            MenuManager.notifyState(MenuManager.MORE_THAN_ONE_TAB_OPENED);
        } else if (tabFolder.getItemCount() == 1) {
            MenuManager.notifyState(MenuManager.ONE_TAB_OPENED);
        } else {
            MenuManager.notifyState(MenuManager.ZERO_TAB_OPENED);
        }

        if (getActiveSearchTab() != null) {
            MenuManager.notifyState(MenuManager.SEARCH_TAB_FOCUSED);
        } else {
            MenuManager.notifyState(MenuManager.SPECIAL_TAB_FOCUSED);
        }
    }

}
