package net.brokentrain.ftf.ui.gui;

import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.components.MainMenu;
import net.brokentrain.ftf.ui.gui.components.MainToolbar;
import net.brokentrain.ftf.ui.gui.components.MenuManager;
import net.brokentrain.ftf.ui.gui.components.Statusbar;
import net.brokentrain.ftf.ui.gui.components.SystemTray;
import net.brokentrain.ftf.ui.gui.settings.SettingsLoader;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.settings.SettingsSaver;
import net.brokentrain.ftf.ui.gui.tabs.TabManager;
import net.brokentrain.ftf.ui.gui.util.BrowserUtil;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.HotkeyUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.MessageBoxUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.URLUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class GUI {

    public static Logger log = Logger.getLogger(GUI.class);

    public static Shell shell;

    public static Display display;

    public static boolean isClosing;

    public static GUI fetcherGui;

    public static boolean isAlive() {

        /* Make sure the GUI is responding */
        return (!display.isDisposed() && !shell.isDisposed() && !isClosing);
    }

    private EventManager eventManager;

    private MainMenu mainMenu;

    private MenuManager menuManager;

    private SettingsManager settingsManager;

    private Statusbar statusBar;

    private SystemTray fetcherSystray;

    private TabManager tabManager;

    private MainToolbar toolBar;

    public GUI(Display display) {
        GUI.display = display;
        GUI.fetcherGui = this;

        /* Trigger the startup process */
        startUp();
    }

    public void enableSystrayIcon(boolean enabled) {

        if (enabled) {

            /* Create a new system tray */
            fetcherSystray = new SystemTray(display, shell, this);
        } else if (fetcherSystray != null) {

            /* Disable the system tray */
            fetcherSystray.disable();
        }
    }

    /* Returns the event manager */
    public EventManager getEventManager() {
        return eventManager;
    }

    /* Returns the main window interface */
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    /* Returns the menu manager */
    public MenuManager getMenuManager() {
        return menuManager;
    }

    /* Returns the source manager */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    /* Returns the status bar */
    public Statusbar getStatusbar() {
        return statusBar;
    }

    /* Returns the system tray */
    public SystemTray getSystemTray() {
        return fetcherSystray;
    }

    /* Returns the tab folder */
    public TabManager getTabManager() {
        return tabManager;
    }

    /* Returns the tool bar */
    public MainToolbar getToolbar() {
        return toolBar;
    }

    private void initComponents() {

        /* Create the shell for the GUI */
        shell = new Shell(display);
        shell.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 3));
        shell.setText(WidgetUtil.getShellTitle());
        shell.setSize(800, 725);
        shell.setImage(PaintUtil.iconFetcher);

        /* Watch for when the shell is disposed */
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {

                /* Hide the shell */
                if (!shell.isDisposed() && shell.isVisible()) {
                    shell.setVisible(false);
                }

                /* Trigger dispose event */
                onDispose();
            }
        });

        /* Watch for when the shell is closed */
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event event) {

                /* Trigger close event */
                onClose(event);
            }
        });

        /* Create the event manager */
        eventManager = new EventManager(display, shell, this);

        /* Get the source manager */
        settingsManager = SettingsManager.getSettingsManager();

        /* Create the tool bar */
        toolBar = new MainToolbar(this, shell, eventManager);

        /* Create the menu manager */
        menuManager = new MenuManager(this, shell, eventManager);

        /* Create the main menu */
        mainMenu = new MainMenu(this, shell, eventManager);

        /* Create the tab folder */
        tabManager = new TabManager(this, shell, eventManager);

        /* Create the status bar */
        statusBar = new Statusbar(this, shell, eventManager);

        /* Inform the event manager of our components */
        eventManager.syncControls();
    }

    void onClose(Event event) {
        onClose(event, false);
    }

    public void onClose(Event event, boolean forceExit) {

        /* Only close if we don't want to tray on close */
        if (SettingsRegistry.useSystemTray()
                && SettingsRegistry.showSystrayIcon
                && SettingsRegistry.trayOnExit && !forceExit) {
            event.doit = false;

            /* Fire the iconify event */
            shell.notifyListeners(SWT.Iconify, new Event());
            return;
        }

        /* Set flag to say we're actually closing */
        isClosing = true;
    }

    void onDispose() {

        /* Trigger shutdown event */
        shutDown();
    }

    private void runEventLoop() {

        try {
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {

            GUI.log.error("An error occurred during runtime!", e);

            /* Only try and report errors if we've been asked too */
            if (SettingsRegistry.reportErrors) {

                /*
                 * Show a dialog box asking if the user wants to report the
                 * error
                 */
                int result = MessageBoxUtil.showMessage(shell, SWT.YES | SWT.NO
                        | SWT.ICON_ERROR, "Error",
                        "An unexpected error occurred which will require FTF to close!"
                                + "\n\nDo you wish to report this crash?");

                /* Handle yes */
                if (result == SWT.YES) {

                    /* Report the error */
                    BrowserUtil.openLink(URLUtil.createNewTicket(e));
                }
            }

            /* Dump error to a log */
            FileUtil.dumpCrashLog(e);
        }

        /* Dispose the display */
        display.dispose();
    }

    public void showGui() {

        /* Show the GUI only if we've not set to use a tray icon */
        if (!SettingsRegistry.useSystemTray()
                || !SettingsRegistry.showSystrayIcon
                || !SettingsRegistry.trayOnStartup) {
            shell.open();
        }

        /* Start the event loop */
        runEventLoop();
    }

    public void shutDown() {

        SettingsSaver settingsSaver = new SettingsSaver(this);

        /* Save user settings */
        settingsSaver.saveCoreSettings();

        /* Save gui settings */
        settingsSaver.saveGUISettings();

        /* Save service settings */
        settingsSaver.saveServiceSettings();

        /* Dispose of fonts */
        FontUtil.disposeFonts();

        /* Dispose of icons */
        PaintUtil.disposeIcons();
    }

    private void startUp() {

        /* Indicate the GUI is starting up */
        GUI.log.info("Starting FTF");

        /* Load the new user settings in */
        SettingsLoader settingsLoader = new SettingsLoader(this);

        /* Update the core settings */
        updateCoreSettings(settingsLoader);

        /* Update early GUI values */
        updateEarlyGUISettings(settingsLoader);

        /* Initialise the default accelerators */
        HotkeyUtil.initDefaultAccelerators();

        /* Initialise the icons */
        PaintUtil.initIcons();

        /* Initialise the colours */
        ColourUtil.initColours();

        /* Build core components of the interface */
        initComponents();

        /* Setup the user settings */
        updateGUISettings(settingsLoader);

        /* Notify on tray usage */
        if (SettingsRegistry.useSystemTray()
                && SettingsRegistry.showSystrayIcon
                && SettingsRegistry.trayOnStartup) {
            shell.notifyListeners(SWT.Iconify, new Event());
        }

        /* Update the menu states */
        tabManager.updateTabFolderState();
        tabManager.updateInterfaceState();
    }

    public void updateCoreSettings(SettingsLoader settingsLoader) {

        /* Load in connection settings */
        SettingsRegistry.readTimeout = Integer.parseInt(settingsLoader
                .getCoreValue("connection", "readTimeout", "3000"));
        SettingsRegistry.connectionTimeout = Integer.parseInt(settingsLoader
                .getCoreValue("connection", "connectionTimeout", "3000"));
        SettingsRegistry.glimpse = settingsLoader.getCoreBoolean("general",
                "glimpse", false);
        SettingsRegistry.investigate = settingsLoader.getCoreBoolean("general",
                "investigate", false);

        /* Load in proxy settings */
        settingsLoader.loadProxySettings();

        /* Load in service entries */
        SettingsRegistry.services = settingsLoader.loadServices();

        /* Load in available services names */
        SettingsRegistry.availableServices = settingsLoader
                .loadAvailableServices();

        /* Load in default services */
        SettingsRegistry.defaultServices = settingsLoader.loadDefaultServices();
    }

    public void updateEarlyGUISettings(SettingsLoader settingsLoader) {

        /* Initialise the fonts */
        FontUtil.updateFonts();

        /* Load in transfer tab properties */
        SettingsRegistry.downloadDirectoryPath = settingsLoader.getGUIValue(
                "transferTab", "downloadDirectory", System
                        .getProperty("user.dir"));

        /* Load in system tray properties */
        SettingsRegistry.showSystrayIcon = settingsLoader.getGUIBoolean("tray",
                "showSystrayIcon", false);

        /* Load in the view properties */
        SettingsRegistry.tabPositionIsTop = settingsLoader.getGUIBoolean(
                "view", "tabPositionIsTop", false);
        SettingsRegistry.displaySingleTab = settingsLoader.getGUIBoolean(
                "view", "displaySingleTab", false);
        SettingsRegistry.showTabCloseButton = settingsLoader.getGUIBoolean(
                "view", "showTabCloseButton", true);

        /* Load in browser tab properties */
        SettingsRegistry.defaultHomepage = settingsLoader.getGUIValue(
                "browserTab", "homepage", URLUtil.PUBMED_HOME);

        /* Load the previous search history */
        SettingsRegistry.openHistory = settingsLoader.loadRecentHistory();
    }

    public void updateGUISettings(SettingsLoader settingsLoader) {

        /* Load in browser properties */
        SettingsRegistry.customBrowser = settingsLoader.getGUIValue("browser",
                "customBrowser", "path");
        SettingsRegistry.customBrowserArguments = settingsLoader.getGUIValue(
                "browser", "customBrowserArguments", "value");

        /* Load in further tray properties */
        SettingsRegistry.trayOnStartup = settingsLoader.getGUIBoolean("tray",
                "trayOnStartup", false);
        SettingsRegistry.trayOnExit = settingsLoader.getGUIBoolean("tray",
                "trayOnExit", false);

        /* Load in further view properties */
        SettingsRegistry.focusNewTabs = settingsLoader.getGUIBoolean("view",
                "focusNewTabs", true);
        SettingsRegistry.simpleTabs = settingsLoader.getGUIBoolean("view",
                "simpleTabs", true);
        SettingsRegistry.reportErrors = settingsLoader.getGUIBoolean("view",
                "reportErrors", true);

        SettingsRegistry.showStatusbar = settingsLoader.getGUIBoolean("view",
                "showStatusbar", true);
        statusBar.createStatusBar();
        statusBar.setShowStatusBar(SettingsRegistry.showStatusbar);

        SettingsRegistry.showToolbar = settingsLoader.getGUIBoolean("view",
                "showToolbar", true);
        toolBar.createToolBar();
        toolBar.setShowToolBar(SettingsRegistry.showToolbar);

        /* Load in query tab properties */
        SettingsRegistry.showMetadataPane = settingsLoader.getGUIBoolean(
                "queryTab", "showMetadataPane", true);
        SettingsRegistry.highlightTerms = settingsLoader.getGUIBoolean(
                "queryTab", "highlightTerms", true);
        SettingsRegistry.showTooltip = settingsLoader.getGUIBoolean("queryTab",
                "showTooltip", true);

        /* Immediately set new view properties */
        tabManager.getTabFolder().setSimple(SettingsRegistry.simpleTabs);
        tabManager.getTabFolder().setTabHeight(
                SettingsRegistry.displaySingleTab ? 0 : -1);

        if (SettingsRegistry.displaySingleTab) {
            tabManager.getTabFolder().setTabPosition(SWT.BOTTOM);
        } else {
            tabManager.getTabFolder().setTabPosition(
                    (SettingsRegistry.tabPositionIsTop == true) ? SWT.TOP
                            : SWT.BOTTOM);
        }

        /* Set the system tray properties */
        if (SettingsRegistry.useSystemTray()
                && SettingsRegistry.showSystrayIcon && (fetcherSystray == null)) {

            /* Enable the tray icon */
            enableSystrayIcon(true);
        } else if (!SettingsRegistry.showSystrayIcon
                && (fetcherSystray != null)) {

            /* Disable the tray icon */
            enableSystrayIcon(false);
        }
    }
}
