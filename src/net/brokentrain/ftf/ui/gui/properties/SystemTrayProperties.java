package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class SystemTrayProperties extends PropertyPage {

    private Button trayFetcherCheck;

    private Button trayOnExitCheck;

    private Button trayOnStartupCheck;

    private Button trayPopupCheck;

    private Button trayPopupFade;

    public SystemTrayProperties(Composite parent, GUI fetcherGui) {
        super(parent, fetcherGui);
        setApplyButtonState(SettingsRegistry.useSystemTray());
        setRestoreButtonState(SettingsRegistry.useSystemTray());
    }

    @Override
    public void applyButtonPressed() {

        if (SettingsRegistry.useSystemTray()
                && (SettingsRegistry.showSystrayIcon != trayFetcherCheck
                        .getSelection())) {
            SettingsRegistry.showSystrayIcon = trayFetcherCheck.getSelection();

            fetcherGui.enableSystrayIcon(SettingsRegistry.showSystrayIcon);
        }

        if (SettingsRegistry.useSystemTray()) {
            SettingsRegistry.trayOnStartup = trayOnStartupCheck.getSelection();
            SettingsRegistry.trayOnExit = trayOnExitCheck.getSelection();
            // GlobalSettings.showTrayPopup = trayPopupCheck.getSelection();
        }

        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        Group trayGroup = new Group(composite, SWT.NONE);
        trayGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        trayGroup.setText("System Tray");
        trayGroup.setLayout(new GridLayout(2, false));
        trayGroup.setFont(FontUtil.dialogFont);

        trayFetcherCheck = new Button(trayGroup, SWT.CHECK);
        trayFetcherCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        trayFetcherCheck
                .setSelection(propertyChangeManager.isShowSystrayIcon());
        trayFetcherCheck.setText("Minimize to the System Tray");
        trayFetcherCheck.setFont(dialogFont);
        trayFetcherCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTrayControlsEnabled(trayFetcherCheck.getSelection());
            }
        });

        trayOnStartupCheck = new Button(trayGroup, SWT.CHECK);
        trayOnStartupCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        trayOnStartupCheck
                .setSelection(propertyChangeManager.isTrayOnStartup());
        trayOnStartupCheck.setText("Place in system tray on startup");
        trayOnStartupCheck.setFont(dialogFont);

        trayOnExitCheck = new Button(trayGroup, SWT.CHECK);
        trayOnExitCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        trayOnExitCheck.setSelection(propertyChangeManager.isTrayOnExit());
        trayOnExitCheck.setText("Place in system tray on exit");
        trayOnExitCheck.setFont(dialogFont);

        // trayPopupCheck = new Button(trayGroup, SWT.CHECK);
        // trayPopupCheck.setLayoutData(LayoutDataShop.createGridData(GridData.FILL_HORIZONTAL,
        // 2));
        // trayPopupCheck.setSelection(propertyChangeManager.isShowTrayPopup());
        // trayPopupCheck.setText("Show popup");
        // trayPopupCheck.setFont(dialogFont);
        // trayPopupCheck.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // }
        // });

        // if (!GlobalSettings.useSystemTray()) {
        // errorMessageLabel = new CLabel(composite, SWT.NONE);
        // errorMessageLabel.setFont(dialogFont);
        // errorMessageLabel.setLayoutData(LayoutDataShop.createGridData(GridData.HORIZONTAL_ALIGN_BEGINNING,
        // 2));
        // setErrorMessage("Not supported on your operating system!");
        // }

        LayoutUtil.setDialogSpacer(composite, 2, 4);

        if (SettingsRegistry.useSystemTray()) {
            setTrayControlsEnabled(trayFetcherCheck.getSelection());
        }

        else {
            trayFetcherCheck.setSelection(false);
            trayOnStartupCheck.setSelection(false);
            trayOnExitCheck.setSelection(false);
            trayPopupCheck.setSelection(false);
            trayPopupFade.setSelection(false);
            trayFetcherCheck.setEnabled(false);
            setTrayControlsEnabled(false);
        }

        // WidgetShop.initMnemonics(new Button[] { trayFetcherCheck,
        // trayOnStartupCheck, trayOnExitCheck, trayPopupCheck,
        // trayPopupAnimate, trayPopupFade });
    }

    @Override
    protected void restoreButtonPressed() {
        if (SettingsRegistry.useSystemTray()) {
            trayFetcherCheck.setSelection(false);
            trayOnStartupCheck.setSelection(false);
            trayOnExitCheck.setSelection(false);
            trayPopupCheck.setSelection(true);

            setTrayControlsEnabled(trayFetcherCheck.getSelection());
        }
    }

    void setTrayControlsEnabled(boolean enabled) {
        trayOnStartupCheck.setEnabled(enabled);
        trayOnExitCheck.setEnabled(enabled);
        // trayPopupCheck.setEnabled(enabled);
    }

    @Override
    public void updatePropertiesChangeManager() {
        if (SettingsRegistry.useSystemTray()) {
            propertyChangeManager.setShowSystrayIcon(trayFetcherCheck
                    .getSelection());
            propertyChangeManager.setTrayOnStartup(trayOnStartupCheck
                    .getSelection());
            propertyChangeManager.setTrayOnExit(trayOnExitCheck.getSelection());
            // propertyChangeManager.setShowTrayPopup(trayPopupCheck.getSelection());
        }
    }
}
