package net.brokentrain.ftf.ui.gui.components;

import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.tabs.FullTextTab;
import net.brokentrain.ftf.ui.gui.tabs.ProcessingTab;
import net.brokentrain.ftf.ui.gui.tabs.SearchTab;
import net.brokentrain.ftf.ui.gui.tabs.TabManager;
import net.brokentrain.ftf.ui.gui.util.HotkeyUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Level;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainMenu {

    public static Menu menu;

    private Shell shell;

    private GUI fetcherGui;

    private EventManager eventManager;

    private SettingsManager settingsManager;

    private MenuItem fileMenuItem;

    private MenuItem gotoMenuItem;

    private MenuItem helpMenuItem;

    private MenuItem logMenuItem;

    private MenuItem modelMenuItem;

    private MenuItem windowMenuItem;

    private MenuItem servicesMenuItem;

    private MenuItem crawlerMenuItem;

    private MenuItem viewMenuItem;

    private MenuItem exportAsIDsMenuItem;

    private MenuItem toolbarItem;

    private MenuItem statusbarItem;

    private MenuItem browserTabItem;

    private MenuItem logTabItem;

    private MenuItem statusTabItem;

    private MenuItem transferTabItem;

    private MenuItem saveMenuItem;

    private MenuItem saveAsMenuItem;

    private MenuItem closeCurrentTab;

    private MenuItem closeCurrentTabWindowItem;

    private MenuItem closeAllTabs;

    private MenuItem closeAllTabsWindowItem;

    public MainMenu(GUI fetcherGui, Shell shell, EventManager eventManager) {

        this.fetcherGui = fetcherGui;
        this.shell = shell;
        this.eventManager = eventManager;
        this.settingsManager = SettingsManager.getSettingsManager();

        initComponents();
    }

    private void buildCrawlerMenu(Menu subMenu) {

        for (MenuItem crawlerItem : subMenu.getItems()) {
            crawlerItem.dispose();
        }

        MenuItem investigateItem = new MenuItem(subMenu, SWT.CHECK);
        investigateItem.setText("&Look for PDFs + prices");
        investigateItem.setSelection(settingsManager.isInvestigating());
        investigateItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                settingsManager.setInvestigate(!settingsManager
                        .isInvestigating());
            }
        });

        MenuItem glimpseItem = new MenuItem(subMenu, SWT.CHECK);
        glimpseItem.setText("Read content information for each result");
        glimpseItem.setSelection(settingsManager.isGlimpsing());
        glimpseItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                settingsManager.setGlimpse(!settingsManager.isGlimpsing());
            }
        });
    }

    private void buildLogMenu(Menu subMenu) {

        Level[] logLevels = new Level[] { Level.ALL, Level.DEBUG, Level.ERROR,
                Level.FATAL, Level.INFO, Level.OFF, Level.WARN };

        for (final Level levelName : logLevels) {

            /* Create a new bunch of menu items for each source */
            final MenuItem logItem = new MenuItem(subMenu, SWT.RADIO);

            switch (levelName.toInt()) {

            case Level.ALL_INT:
                logItem.setText("All");
                break;
            case Level.DEBUG_INT:
                logItem.setText("Debug");
                break;
            case Level.ERROR_INT:
                logItem.setText("Error");
                break;
            case Level.INFO_INT:
                logItem.setText("Info");
                break;
            case Level.FATAL_INT:
                logItem.setText("Fatal");
                break;
            case Level.OFF_INT:
                logItem.setText("Off");
                break;
            case Level.WARN_INT:
                logItem.setText("Warn");
                break;
            }

            logItem.setSelection(GUI.log.getEffectiveLevel().equals(levelName));

            /* Allowing toggling on/off of log level */
            logItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    GUI.log.debug("Setting new log level: " + levelName);
                    GUI.log.setLevel(levelName);
                }
            });
        }
    }

    public void buildMenuBar() {

        for (MenuItem menuItem : menu.getItems()) {
            menuItem.dispose();
        }

        /* File menu */
        fileMenuItem = new MenuItem(menu, SWT.CASCADE);
        fileMenuItem.setText("&File");

        Menu fileSubMenu = new Menu(fileMenuItem);
        fileMenuItem.setMenu(fileSubMenu);

        MenuItem newMenuItem = new MenuItem(fileSubMenu, SWT.CASCADE);
        newMenuItem.setText("&New");
        newMenuItem.setImage(PaintUtil.iconNew);

        Menu fileTypeSubMenu = new Menu(newMenuItem);
        newMenuItem.setMenu(fileTypeSubMenu);

        MenuItem newTextMenuItem = new MenuItem(fileTypeSubMenu, SWT.NONE);
        newTextMenuItem.setImage(PaintUtil.iconNewText);
        newTextMenuItem.setText("&Textual query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_TEXT"));
        newTextMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_NEW_TEXT"));
        newTextMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_TEXT);
            }
        });

        MenuItem newLocalItem = new MenuItem(fileTypeSubMenu, SWT.NONE);
        newLocalItem.setText("&Local query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_LOCAL"));
        newLocalItem.setImage(PaintUtil.iconNewLocal);
        newLocalItem
                .setAccelerator(HotkeyUtil.getHotkeyValue("MENU_NEW_LOCAL"));
        newLocalItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_LOCAL);
            }
        });

        MenuItem newProcessingMenuItem = new MenuItem(fileTypeSubMenu, SWT.NONE);
        newProcessingMenuItem.setText("&Processing query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_PROCESSING"));
        newProcessingMenuItem.setImage(PaintUtil.iconNewProcessing);
        newProcessingMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_NEW_PROCESSING"));
        newProcessingMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_PROCESSING);
            }
        });

        new MenuItem(fileTypeSubMenu, SWT.SEPARATOR);

        MenuItem newArxivMenuItem = new MenuItem(fileTypeSubMenu, SWT.NONE);
        newArxivMenuItem.setText("&Arxiv Identifier");
        newArxivMenuItem.setImage(PaintUtil.iconNewArxiv);
        newArxivMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_ARXIV);
            }
        });

        MenuItem newDOIMenuItem = new MenuItem(fileTypeSubMenu, SWT.NONE);
        newDOIMenuItem.setText("&Digital Object Identifier");
        newDOIMenuItem.setImage(PaintUtil.iconNewDOI);
        newDOIMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_DOI);
            }
        });

        MenuItem openMenuItem = new MenuItem(fileSubMenu, SWT.CASCADE);
        openMenuItem.setImage(PaintUtil.iconOpen);
        openMenuItem.setText("&Open..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_OPEN"));
        openMenuItem.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_OPEN"));
        openMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpen();
            }
        });

        new MenuItem(fileSubMenu, SWT.SEPARATOR);

        saveMenuItem = new MenuItem(fileSubMenu, SWT.NONE);
        saveMenuItem.setImage(PaintUtil.iconSave);
        saveMenuItem.setText("&Save..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_SAVE"));
        saveMenuItem.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_SAVE"));
        saveMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionSave();
            }
        });

        saveAsMenuItem = new MenuItem(fileSubMenu, SWT.NONE);
        saveAsMenuItem.setImage(PaintUtil.iconSaveAs);
        saveAsMenuItem.setText("Save &As..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_SAVE_AS"));
        saveAsMenuItem
                .setAccelerator(HotkeyUtil.getHotkeyValue("MENU_SAVE_AS"));
        saveAsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionSaveAs();
            }
        });

        MenuManager.registerSave(saveMenuItem, saveAsMenuItem);

        new MenuItem(fileSubMenu, SWT.SEPARATOR);

        MenuItem exportMenuItem = new MenuItem(fileSubMenu, SWT.CASCADE);
        exportMenuItem.setImage(PaintUtil.iconExport);
        exportMenuItem.setText("&Export");

        Menu exportSubMenu = new Menu(exportMenuItem);
        exportMenuItem.setMenu(exportSubMenu);

        exportAsIDsMenuItem = new MenuItem(exportSubMenu, SWT.NONE);
        exportAsIDsMenuItem.setText("As PMID list");
        exportAsIDsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionExportAsIDList();
            }
        });

        MenuManager.registerExport(exportMenuItem);

        new MenuItem(fileSubMenu, SWT.SEPARATOR);

        closeCurrentTab = new MenuItem(fileSubMenu, SWT.NONE);
        closeCurrentTab.setText("&Close" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_CLOSE"));
        closeCurrentTab.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_CLOSE"));
        closeCurrentTab.setImage(PaintUtil.iconClose);
        closeCurrentTab.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionCloseCurrent();
            }
        });

        closeAllTabs = new MenuItem(fileSubMenu, SWT.NONE);
        closeAllTabs.setText("&Close All" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_CLOSE_ALL"));
        closeAllTabs
                .setAccelerator(HotkeyUtil.getHotkeyValue("MENU_CLOSE_ALL"));
        closeAllTabs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionCloseAll();
            }
        });

        MenuManager.registerCloseTab(closeCurrentTab, closeAllTabs);

        MenuItem quitMenuItem = new MenuItem(fileSubMenu, SWT.NONE);
        quitMenuItem.setImage(PaintUtil.iconQuit);
        quitMenuItem.setText("&Quit" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_EXIT"));
        quitMenuItem.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_EXIT"));
        quitMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionExit();
            }
        });

        /* Edit menu */
        MenuItem editMenuItem = new MenuItem(menu, SWT.CASCADE);
        editMenuItem.setText("&Edit");

        Menu editSubMenu = new Menu(shell, SWT.DROP_DOWN);
        editMenuItem.setMenu(editSubMenu);

        editSubMenu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {
                MenuManager.handleEditMenuState();
            }
        });

        MenuItem cutMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        cutMenuItem.setImage(PaintUtil.iconCut);
        cutMenuItem.setText("Cut" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_EDIT_CUT"));
        cutMenuItem.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_EDIT_CUT"));
        cutMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.handleEditAction(EventManager.EDIT_ACTION_CUT);
            }
        });

        MenuItem copyMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        copyMenuItem.setImage(PaintUtil.iconCopy);
        copyMenuItem.setText("&Copy" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_EDIT_CUT"));
        copyMenuItem.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_EDIT_CUT"));
        copyMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.handleEditAction(EventManager.EDIT_ACTION_COPY);
            }
        });

        MenuItem pasteMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        pasteMenuItem.setImage(PaintUtil.iconPaste);
        pasteMenuItem.setText("&Paste" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_EDIT_PASTE"));
        pasteMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_EDIT_PASTE"));
        pasteMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.handleEditAction(EventManager.EDIT_ACTION_PASTE);
            }
        });

        new MenuItem(editSubMenu, SWT.SEPARATOR);

        MenuItem deleteMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        deleteMenuItem.setText("&Delete" + "\t" + "Del");
        deleteMenuItem.setImage(PaintUtil.iconDelete);
        deleteMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.handleEditAction(EventManager.EDIT_ACTION_DELETE);
            }
        });

        MenuItem selectAllMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        selectAllMenuItem.setImage(PaintUtil.iconSelectAll);
        selectAllMenuItem.setText("&Select All" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_EDIT_SELECT_ALL"));
        selectAllMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_EDIT_SELECT_ALL"));
        selectAllMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager
                        .handleEditAction(EventManager.EDIT_ACTION_SELECTALL);
            }
        });

        new MenuItem(editSubMenu, SWT.SEPARATOR);

        MenuItem preferencesMenuItem = new MenuItem(editSubMenu, SWT.NONE);
        preferencesMenuItem.setText("&Preferences");
        preferencesMenuItem.setImage(PaintUtil.iconPrefs);
        preferencesMenuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenPreferences();
            }
        });

        MenuManager.registerEditMenu(copyMenuItem, pasteMenuItem, cutMenuItem,
                selectAllMenuItem, deleteMenuItem);

        /* View menu */
        viewMenuItem = new MenuItem(menu, SWT.CASCADE);
        viewMenuItem.setText("&View");
        final Menu viewSubMenu = new Menu(viewMenuItem);
        viewMenuItem.setMenu(viewSubMenu);
        viewMenuItem.addArmListener(new ArmListener() {
            public void widgetArmed(ArmEvent e) {
                buildViewMenu(viewSubMenu);
            }
        });

        gotoMenuItem = new MenuItem(menu, SWT.CASCADE);
        gotoMenuItem.setText("&Go");
        Menu gotoSubMenu = new Menu(shell, SWT.DROP_DOWN);
        gotoMenuItem.setMenu(gotoSubMenu);

        MenuItem gotoPreviousTab = new MenuItem(gotoSubMenu, SWT.NONE);
        gotoPreviousTab.setImage(PaintUtil.iconBackward);
        gotoPreviousTab.setText("Previous tab" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_PREVIOUS_TAB"));
        gotoPreviousTab.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_PREVIOUS_TAB"));
        gotoPreviousTab.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionGotoPreviousTab();
            }
        });

        MenuItem gotoNextTab = new MenuItem(gotoSubMenu, SWT.NONE);
        gotoNextTab.setImage(PaintUtil.iconForward);
        gotoNextTab.setText("Next tab" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEXT_TAB"));
        gotoNextTab.setAccelerator(HotkeyUtil.getHotkeyValue("MENU_NEXT_TAB"));
        gotoNextTab.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionGotoNextTab();
            }
        });

        MenuManager.registerGotoTab(gotoPreviousTab, gotoNextTab);

        /* Log menu */
        logMenuItem = new MenuItem(menu, SWT.CASCADE);
        logMenuItem.setText("&Log");
        final Menu logSubMenu = new Menu(logMenuItem);
        logMenuItem.setMenu(logSubMenu);
        logMenuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                buildLogMenu(logSubMenu);
            }
        });

        final TabManager tabManager = fetcherGui.getTabManager();

        if (tabManager != null) {

            final SearchTab searchTab = tabManager.getActiveSearchTab();

            if (searchTab != null) {

                /* Services menu */
                servicesMenuItem = new MenuItem(menu, SWT.CASCADE);
                servicesMenuItem.setText("&Services");
                final Menu serviceManagerSubMenu = new Menu(servicesMenuItem);
                servicesMenuItem.setMenu(serviceManagerSubMenu);
                servicesMenuItem.addArmListener(new ArmListener() {
                    public void widgetArmed(ArmEvent e) {
                        buildServiceMenu(serviceManagerSubMenu);
                    }
                });

                /* Populate service menu */
                buildServiceMenu(serviceManagerSubMenu);

                if (searchTab instanceof ProcessingTab) {

                    /* Model menu */
                    modelMenuItem = new MenuItem(menu, SWT.CASCADE);
                    modelMenuItem.setText("&Model");
                    final Menu modelSubMenu = new Menu(modelMenuItem);
                    modelMenuItem.setMenu(modelSubMenu);
                    modelMenuItem.addArmListener(new ArmListener() {
                        public void widgetArmed(ArmEvent e) {
                            buildModelMenu(modelSubMenu);
                        }
                    });

                    /* Populate the model menu */
                    buildModelMenu(modelSubMenu);
                } else if (searchTab instanceof FullTextTab) {

                    /* Services menu */
                    crawlerMenuItem = new MenuItem(menu, SWT.CASCADE);
                    crawlerMenuItem.setText("&Crawler");
                    final Menu crawlerSubMenu = new Menu(crawlerMenuItem);
                    crawlerMenuItem.setMenu(crawlerSubMenu);
                    crawlerMenuItem.addArmListener(new ArmListener() {
                        public void widgetArmed(ArmEvent e) {
                            buildCrawlerMenu(crawlerSubMenu);
                        }
                    });

                    /* Populate service menu */
                    buildCrawlerMenu(crawlerSubMenu);

                }
            }
        }

        /* Search menu */
        windowMenuItem = new MenuItem(menu, SWT.CASCADE);
        windowMenuItem.setText("&Window");
        final Menu windowSubMenu = new Menu(windowMenuItem);
        windowMenuItem.setMenu(windowSubMenu);
        windowMenuItem.addArmListener(new ArmListener() {
            public void widgetArmed(ArmEvent e) {
                buildWindowMenu(windowSubMenu);
            }
        });

        /* Help menu */
        helpMenuItem = new MenuItem(menu, SWT.CASCADE);
        helpMenuItem.setText("&Help");
        Menu helpSubMenu = new Menu(helpMenuItem);
        helpMenuItem.setMenu(helpSubMenu);

        MenuItem tutorialMenuItem = new MenuItem(helpSubMenu, SWT.NONE);
        tutorialMenuItem.setText("&Tutorial");
        tutorialMenuItem.setImage(PaintUtil.iconTutorial);
        tutorialMenuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenTutorial();
            }
        });

        MenuItem faqMenuItem = new MenuItem(helpSubMenu, SWT.NONE);
        faqMenuItem.setText("&FAQ");
        faqMenuItem.setImage(PaintUtil.iconFAQ);
        faqMenuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenFAQ();
            }
        });

        MenuItem reportBugItem = new MenuItem(helpSubMenu, SWT.NONE);
        reportBugItem.setText("&Report a bug");
        reportBugItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenNewTicket();
            }
        });

        new MenuItem(helpSubMenu, SWT.SEPARATOR);

        MenuItem openHomepageItem = new MenuItem(helpSubMenu, SWT.NONE);
        openHomepageItem.setText("&Visit Homepage");
        openHomepageItem.setImage(PaintUtil.iconBrowserHome);
        openHomepageItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenHomepage();
            }
        });

        new MenuItem(helpSubMenu, SWT.SEPARATOR);

        MenuItem aboutMenuItem = new MenuItem(helpSubMenu, SWT.NONE);
        aboutMenuItem.setText("&About FTF");
        aboutMenuItem.setImage(PaintUtil.iconAbout);
        aboutMenuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenAbout();
            }
        });

        /* Populate log menu */
        buildLogMenu(logSubMenu);

        buildWindowMenu(windowSubMenu);

        MenuManager.registerCloseTabWindowItems(closeCurrentTabWindowItem,
                closeAllTabsWindowItem);

        if (tabManager != null) {

            CTabFolder tabFolder = tabManager.getTabFolder();

            if (tabFolder.getItemCount() > 1) {
                MenuManager.notifyState(MenuManager.MORE_THAN_ONE_TAB_OPENED);
            } else if (tabFolder.getItemCount() == 1) {
                MenuManager.notifyState(MenuManager.ONE_TAB_OPENED);
            } else {
                MenuManager.notifyState(MenuManager.ZERO_TAB_OPENED);
            }

            if (tabManager.getActiveSearchTab() != null) {
                MenuManager.notifyState(MenuManager.SEARCH_TAB_FOCUSED);
            } else {
                MenuManager.notifyState(MenuManager.SPECIAL_TAB_FOCUSED);
            }
        }
    }

    private void buildModelMenu(Menu subMenu) {

        final TabManager tabManager = fetcherGui.getTabManager();

        if (tabManager == null) {
            return;
        }

        for (MenuItem searchItem : subMenu.getItems()) {
            searchItem.dispose();
        }

        final ProcessingTab processingTab = tabManager.getActiveProcessingTab();

        if (processingTab == null) {
            return;
        }

        MenuItem loadMenuItem = new MenuItem(subMenu, SWT.CASCADE);
        loadMenuItem.setImage(PaintUtil.iconOpen);
        loadMenuItem.setText("&Load..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_OPEN_MODEL"));
        loadMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_OPEN_MODEL"));
        loadMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionOpenModel();
            }
        });

        MenuItem saveMenuItem = new MenuItem(subMenu, SWT.NONE);
        saveMenuItem.setImage(PaintUtil.iconSaveAs);
        saveMenuItem.setText("Save..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_SAVE_MODEL"));
        saveMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_SAVE_MODEL"));
        saveMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionSaveModel();
            }
        });

        MenuItem saveAsMenuItem = new MenuItem(subMenu, SWT.NONE);
        saveAsMenuItem.setImage(PaintUtil.iconSaveAs);
        saveAsMenuItem.setText("Save As..." + "\t"
                + HotkeyUtil.getHotkeyName("MENU_SAVE_MODEL_AS"));
        saveAsMenuItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_SAVE_MODEL_AS"));
        saveAsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionSaveModelAs();
            }
        });

    }

    private void buildServiceMenu(Menu subMenu) {

        for (MenuItem viewItem : subMenu.getItems()) {
            viewItem.dispose();
        }

        final TabManager tabManager = fetcherGui.getTabManager();

        if (tabManager == null) {
            return;
        }

        final SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab == null) {
            return;
        }

        ServiceManager serviceManager = searchTab.getServiceManager();

        final MenuItem allItem = new MenuItem(subMenu, SWT.NONE);
        allItem.setText("All");
        allItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                eventManager.actionSelectAllServices();

                /* Update the state of the tab */
                searchTab.updateState();
            }
        });

        final MenuItem noneItem = new MenuItem(subMenu, SWT.NONE);
        noneItem.setText("None");
        noneItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                eventManager.actionDeselectAllServices();

                /* Update the state of the tab */
                searchTab.updateState();
            }
        });

        new MenuItem(subMenu, SWT.SEPARATOR);

        /* Loop over all available services */
        for (final ServiceEntry serviceEntry : serviceManager
                .getAvailableServices()) {

            /* Create a new menu item for each service */
            final MenuItem serviceItem = new MenuItem(subMenu, SWT.CHECK);
            serviceItem.setText(serviceEntry.getDescription());
            serviceItem.setSelection(serviceEntry.isSelected());
            serviceItem.setEnabled(serviceEntry.isEnabled());

            /* Attempt to assign an icon for the service by name */
            serviceItem.setImage(PaintUtil.getServiceIcon(serviceEntry
                    .getSection()));

            /* Allowing toggling on/off of serviceManager */
            serviceItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    eventManager.actionToggleSource(serviceEntry);

                    /* Update the state of the tab */
                    searchTab.updateState();
                }
            });
        }

    }

    private void buildViewMenu(Menu subMenu) {

        for (MenuItem viewItem : subMenu.getItems()) {
            viewItem.dispose();
        }

        toolbarItem = new MenuItem(subMenu, SWT.CHECK);
        toolbarItem.setText("Toolbar");
        toolbarItem.setSelection(SettingsRegistry.showToolbar);
        toolbarItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                eventManager.actionShowToolbar(!SettingsRegistry.showToolbar);
            }
        });

        statusbarItem = new MenuItem(subMenu, SWT.CHECK);
        statusbarItem.setText("Statusbar");
        statusbarItem.setSelection(SettingsRegistry.showStatusbar);
        statusbarItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                eventManager
                        .actionShowStatusbar(!SettingsRegistry.showStatusbar);
            }
        });

        final TabManager tabManager = fetcherGui.getTabManager();

        if (tabManager == null) {
            return;
        }

        new MenuItem(subMenu, SWT.SEPARATOR);

        browserTabItem = new MenuItem(subMenu, SWT.CHECK);
        browserTabItem.setText("Browser");
        browserTabItem.setSelection(!tabManager.getBrowserTab().isDisposed());
        browserTabItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                tabManager.toggleBrowserTab();
            }
        });

        logTabItem = new MenuItem(subMenu, SWT.CHECK);
        logTabItem.setText("Log");
        logTabItem.setSelection(!tabManager.getLogTab().isDisposed());
        logTabItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                tabManager.toggleLogTab();
            }
        });

        statusTabItem = new MenuItem(subMenu, SWT.CHECK);
        statusTabItem.setText("Status");
        statusTabItem.setSelection(!tabManager.getStatusTab().isDisposed());
        statusTabItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                tabManager.toggleStatusTab();
            }
        });

        transferTabItem = new MenuItem(subMenu, SWT.CHECK);
        transferTabItem.setText("Transfer");
        transferTabItem.setSelection(!tabManager.getTransferTab().isDisposed());
        transferTabItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                tabManager.toggleTransferTab();
            }
        });

        if (tabManager.getActiveSearchTab() != null) {

            /* Draw additional seperator */
            new MenuItem(subMenu, SWT.SEPARATOR);

            final MenuItem viewItem = new MenuItem(subMenu, SWT.CHECK);

            viewItem.setText("&Show metadata");
            viewItem.setSelection(SettingsRegistry.showMetadataPane);
            viewItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    eventManager.actionToggleMetadata();
                }
            });

            final MenuItem highlightItem = new MenuItem(subMenu, SWT.CHECK);
            highlightItem.setText("&Highlight terms");
            highlightItem.setSelection(SettingsRegistry.highlightTerms);
            highlightItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    eventManager.actionToggleHighlightTerms();
                }
            });
        }
    }

    private void buildWindowMenu(Menu subMenu) {

        final TabManager tabManager = fetcherGui.getTabManager();

        if (tabManager == null) {
            return;
        }

        for (MenuItem searchItem : subMenu.getItems()) {
            searchItem.dispose();
        }

        closeCurrentTabWindowItem = new MenuItem(subMenu, SWT.NONE);
        closeCurrentTabWindowItem.setText("&Close" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_CLOSE"));
        closeCurrentTabWindowItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_CLOSE"));
        closeCurrentTabWindowItem.setImage(PaintUtil.iconClose);
        closeCurrentTabWindowItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionCloseCurrent();
            }
        });

        closeAllTabsWindowItem = new MenuItem(subMenu, SWT.NONE);
        closeAllTabsWindowItem.setText("&Close All" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_CLOSE_ALL"));
        closeAllTabsWindowItem.setAccelerator(HotkeyUtil
                .getHotkeyValue("MENU_CLOSE_ALL"));
        closeAllTabsWindowItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionCloseAll();
            }
        });
        MenuManager.registerCloseTabWindowItems(closeCurrentTabWindowItem,
                closeAllTabsWindowItem);

        CTabItem[] searchItems = tabManager.getItems();

        if (searchItems.length > 1) {

            new MenuItem(subMenu, SWT.SEPARATOR);

            for (final CTabItem searchItem : searchItems) {

                final MenuItem newItem = new MenuItem(subMenu, SWT.RADIO);
                newItem.setText(StringUtil.ellipsize(searchItem.getText(), 15));
                newItem.setSelection(tabManager.isActiveTab(searchItem));
                newItem.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        tabManager.setSelection(searchItem);
                    }
                });
            }
        }
    }

    public void initComponents() {

        /* Create new menu bar and attach it to main window */
        menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        buildMenuBar();
    }
}
