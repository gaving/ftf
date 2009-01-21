package net.brokentrain.ftf.ui.gui.components;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class SystemTray {

    private Display display;

    private Shell shell;

    private boolean isMinimizedToTray;

    private Listener shellIconifyListener;

    private MenuItem exitItem;

    private MenuItem openItem;

    private Menu systemTrayItemMenu;

    private TrayItem systemTrayItem;

    private Tray systemTray;

    public SystemTray(Display display, Shell shell, GUI fetcherGui) {
        this.display = display;
        this.shell = shell;
        isMinimizedToTray = false;

        initComponents();
    }

    public void disable() {
        if (WidgetUtil.isset(shell)) {
            shell.removeListener(SWT.Iconify, shellIconifyListener);
        }
    }

    private void initComponents() {

        systemTray = display.getSystemTray();

        systemTrayItem = new TrayItem(systemTray, SWT.NONE);
        systemTrayItem.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                if (WidgetUtil.isset(systemTrayItemMenu)) {
                    systemTrayItemMenu.setVisible(true);
                }
            }
        });

        if (systemTrayItem.getImage() != null) {
            systemTrayItem.getImage().dispose();
        }

        systemTrayItem.setImage(PaintUtil.loadImage("/img/core/tray.png"));
        systemTrayItem.setToolTipText("Full-text Fetcher");
        systemTrayItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

                if (isMinimizedToTray) {
                    restoreWindow();
                } else if (SettingsRegistry.isLinux()) {
                    minimizeWindow();
                }
            }
        });

        systemTrayItemMenu = new Menu(shell, SWT.POP_UP);

        openItem = new MenuItem(systemTrayItemMenu, SWT.NONE);
        openItem.setText("Show");
        openItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                restoreWindow();
            }
        });

        systemTrayItemMenu.setDefaultItem(openItem);

        new MenuItem(systemTrayItemMenu, SWT.SEPARATOR);

        exitItem = new MenuItem(systemTrayItemMenu, SWT.NONE);
        exitItem.setText("Exit");
        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (SettingsRegistry.isLinux() && !isMinimizedToTray) {
                    SettingsRegistry.shellBounds = shell.getBounds();
                    SettingsRegistry.isShellMaximized = shell.getMaximized();
                }

                GUI.isClosing = true;
                shell.dispose();
            }
        });

        shellIconifyListener = new Listener() {
            public void handleEvent(Event event) {
                minimizeWindow();
            }
        };
        shell.addListener(SWT.Iconify, shellIconifyListener);
    }

    public boolean isMinimizedToTray() {
        return isMinimizedToTray;
    }

    public void minimizeWindow() {

        if (SettingsRegistry.isWindows()) {
            systemTrayItem.setVisible(true);
        }

        shell.setVisible(false);
        isMinimizedToTray = true;
    }

    public void restoreWindow() {

        if (SettingsRegistry.isWindows()) {
            systemTrayItem.setVisible(false);
        }

        if (WidgetUtil.isset(shell)) {
            shell.setVisible(true);
            shell.setActive();
            shell.setMinimized(false);
        }

        isMinimizedToTray = false;
    }
}
