package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ViewProperties extends PropertyPage {

    private Button curvedTabRadio;

    private Button displayTabsCheck;

    private Button focusNewTabsCheck;

    private Button showStatusbarCheck;

    private Button showToolbarCheck;

    private Button showCloseButtonCheck;

    private Button reportErrorsCheck;

    private Button simpleTabRadio;

    private Button tabTopRadio;

    private Button tabBottomRadio;

    public ViewProperties(Composite parent, GUI fetcherGui) {
        super(parent, fetcherGui);
    }

    @Override
    public void applyButtonPressed() {

        if (SettingsRegistry.simpleTabs != simpleTabRadio.getSelection()) {
            SettingsRegistry.simpleTabs = simpleTabRadio.getSelection();
            fetcherGui.getTabManager().getTabFolder().setSimple(
                    SettingsRegistry.simpleTabs);
        }

        if (SettingsRegistry.tabPositionIsTop != tabTopRadio.getSelection()) {
            SettingsRegistry.tabPositionIsTop = tabTopRadio.getSelection();
            fetcherGui.getTabManager().getTabFolder().setTabPosition(SWT.TOP);
        }

        if (SettingsRegistry.displaySingleTab == displayTabsCheck
                .getSelection()) {
            SettingsRegistry.displaySingleTab = !displayTabsCheck
                    .getSelection();
            fetcherGui.getTabManager().getTabFolder().setTabHeight(
                    SettingsRegistry.displaySingleTab ? 0 : -1);

            if (SettingsRegistry.displaySingleTab) {
                fetcherGui.getTabManager().getTabFolder().setTabPosition(
                        SWT.BOTTOM);
            } else {
                fetcherGui.getTabManager().getTabFolder().setTabPosition(
                        SettingsRegistry.tabPositionIsTop ? SWT.TOP
                                : SWT.BOTTOM);
            }

            fetcherGui.getTabManager().getTabFolder().layout();
        }

        SettingsRegistry.showTabCloseButton = showCloseButtonCheck
                .getSelection();
        SettingsRegistry.focusNewTabs = focusNewTabsCheck.getSelection();

        if (SettingsRegistry.showToolbar != showToolbarCheck.getSelection()) {

            fetcherGui.getToolbar().setShowToolBar(
                    !SettingsRegistry.showToolbar);
            SettingsRegistry.showToolbar = showToolbarCheck.getSelection();
        }

        if (SettingsRegistry.showStatusbar != showStatusbarCheck.getSelection()) {

            fetcherGui.getStatusbar().setShowStatusBar(
                    !SettingsRegistry.showStatusbar);
            SettingsRegistry.showStatusbar = showStatusbarCheck.getSelection();
        }

        SettingsRegistry.reportErrors = reportErrorsCheck.getSelection();

        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        Group tabStyleGroup = new Group(composite, SWT.NONE);
        tabStyleGroup.setText("Tab Layout");
        tabStyleGroup.setLayout(new GridLayout(2, false));
        tabStyleGroup.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        tabStyleGroup.setFont(FontUtil.dialogFont);

        displayTabsCheck = new Button(tabStyleGroup, SWT.CHECK);
        displayTabsCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        displayTabsCheck.setSelection(!propertyChangeManager
                .isDisplaySingleTab());
        displayTabsCheck.setText("Display tabs");
        displayTabsCheck.setFont(dialogFont);
        displayTabsCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTabControlsEnabled(displayTabsCheck.getSelection());

                setWarningMessage((SettingsRegistry.displaySingleTab == displayTabsCheck
                        .getSelection()) ? "This requires a restart" : null);
            }
        });

        simpleTabRadio = new Button(tabStyleGroup, SWT.RADIO);
        simpleTabRadio.setText("Traditional tabs");
        simpleTabRadio.setSelection(propertyChangeManager.isSimpleTabs());
        simpleTabRadio.setFont(dialogFont);

        curvedTabRadio = new Button(tabStyleGroup, SWT.RADIO);
        curvedTabRadio.setText("Curved tabs");
        curvedTabRadio.setSelection(!propertyChangeManager.isSimpleTabs());
        curvedTabRadio.setFont(dialogFont);

        focusNewTabsCheck = new Button(tabStyleGroup, SWT.CHECK);
        focusNewTabsCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        focusNewTabsCheck.setSelection(propertyChangeManager.isFocusNewTabs());
        focusNewTabsCheck.setText("Focus tabs on open");
        focusNewTabsCheck.setFont(dialogFont);

        showCloseButtonCheck = new Button(tabStyleGroup, SWT.CHECK);
        showCloseButtonCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        showCloseButtonCheck.setSelection(propertyChangeManager
                .isShowTabCloseButton());
        showCloseButtonCheck.setText("Show close button");
        showCloseButtonCheck.setFont(dialogFont);
        showCloseButtonCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                setWarningMessage((SettingsRegistry.showTabCloseButton != showCloseButtonCheck
                        .getSelection()) ? "This option requires a restart in order to take effect."
                        : null);
            }
        });

        Group tabPositionGroup = new Group(tabStyleGroup, SWT.NONE);
        tabPositionGroup.setText("Tab Position");
        tabPositionGroup.setLayout(new GridLayout(2, false));
        tabPositionGroup.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        tabPositionGroup.setFont(FontUtil.dialogFont);

        tabTopRadio = new Button(tabPositionGroup, SWT.RADIO);
        tabTopRadio.setText("Top");
        tabTopRadio.setSelection(propertyChangeManager.isTabPositionTop());
        tabTopRadio.setFont(dialogFont);

        tabBottomRadio = new Button(tabPositionGroup, SWT.RADIO);
        tabBottomRadio.setText("Bottom");
        tabBottomRadio.setSelection(!propertyChangeManager.isTabPositionTop());
        tabBottomRadio.setFont(dialogFont);

        Group settingsGroup = new Group(composite, SWT.NONE);
        settingsGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        settingsGroup.setText("Miscellaneous");
        settingsGroup.setLayout(new GridLayout(2, false));
        settingsGroup.setFont(FontUtil.dialogFont);

        showToolbarCheck = new Button(settingsGroup, SWT.CHECK);
        showToolbarCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        showToolbarCheck.setSelection(propertyChangeManager.isShowToolbar());
        showToolbarCheck.setText("Show toolbar");
        showToolbarCheck.setFont(dialogFont);

        showStatusbarCheck = new Button(settingsGroup, SWT.CHECK);
        showStatusbarCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        showStatusbarCheck.setSelection(propertyChangeManager.isShowToolbar());
        showStatusbarCheck.setText("Show statusbar");
        showStatusbarCheck.setFont(dialogFont);

        reportErrorsCheck = new Button(settingsGroup, SWT.CHECK);
        reportErrorsCheck.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        reportErrorsCheck.setSelection(propertyChangeManager.isReportErrors());
        reportErrorsCheck.setText("Report errors");
        reportErrorsCheck.setFont(dialogFont);

        WidgetUtil.initMnemonics(new Button[] { simpleTabRadio, curvedTabRadio,
                focusNewTabsCheck, showCloseButtonCheck, reportErrorsCheck });

        /* Create a message label for displaying errors */
        errorMessageLabel = new CLabel(composite, SWT.NONE);
        errorMessageLabel.setFont(dialogFont);
        errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
                true, false, 2, 1));
        ((GridData) errorMessageLabel.getLayoutData()).heightHint = 32;

        setTabControlsEnabled(displayTabsCheck.getSelection());
    }

    @Override
    protected void restoreButtonPressed() {
        simpleTabRadio.setSelection(true);
        curvedTabRadio.setSelection(false);
        tabBottomRadio.setSelection(true);
        tabTopRadio.setSelection(false);
        displayTabsCheck.setSelection(true);
        showCloseButtonCheck.setSelection(true);
        reportErrorsCheck.setSelection(true);
        showStatusbarCheck.setSelection(true);
        showToolbarCheck.setSelection(true);
    }

    void setTabControlsEnabled(boolean enabled) {
        focusNewTabsCheck.setEnabled(enabled);
        simpleTabRadio.setEnabled(enabled);
        curvedTabRadio.setEnabled(enabled);
        showCloseButtonCheck.setEnabled(enabled);
        tabTopRadio.setEnabled(enabled);
        tabBottomRadio.setEnabled(enabled);
    }

    @Override
    public void updatePropertiesChangeManager() {

        propertyChangeManager.setDisplaySingleTab(!displayTabsCheck
                .getSelection());
        propertyChangeManager.setSimpleTabs(simpleTabRadio.getSelection());
        propertyChangeManager.setShowTabCloseButton(showCloseButtonCheck
                .getSelection());
        propertyChangeManager.setFocusNewTabs(focusNewTabsCheck.getSelection());
        propertyChangeManager.setTabPositionTop(tabTopRadio.getSelection());

        propertyChangeManager.setShowToolbar(showToolbarCheck.getSelection());
        propertyChangeManager.setShowStatusbar(showStatusbarCheck
                .getSelection());
        propertyChangeManager.setReportErrors(reportErrorsCheck.getSelection());
    }
}
