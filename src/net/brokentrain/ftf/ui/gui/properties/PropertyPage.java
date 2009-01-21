package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.ProxyUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class PropertyPage {

    protected static PropertyChangeManager propertyChangeManager;

    public static PropertyChangeManager getPropertyChangeManager() {
        return propertyChangeManager;
    }

    public static void initPropertyChangeManager(GUI fetcherGui) {
        propertyChangeManager = new PropertyChangeManager(fetcherGui);

        /* Browser */
        propertyChangeManager
                .setCustomBrowserPath(SettingsRegistry.customBrowser);
        propertyChangeManager
                .setCustomBrowserArguments(SettingsRegistry.customBrowserArguments);

        /* Connection */
        propertyChangeManager.setReadTimeout(SettingsRegistry.readTimeout);
        propertyChangeManager
                .setConnectionTimeout(SettingsRegistry.connectionTimeout);

        /* Proxy */
        propertyChangeManager.setProxySettings(ProxyUtil.proxySettings);

        /* Set default services */
        propertyChangeManager
                .setDefaultServices(SettingsRegistry.defaultServices);

        /* Tray */
        if (SettingsRegistry.useSystemTray()) {
            propertyChangeManager
                    .setShowSystrayIcon(SettingsRegistry.showSystrayIcon);
        }
        propertyChangeManager.setTrayOnStartup(SettingsRegistry.trayOnStartup);
        propertyChangeManager.setTrayOnExit(SettingsRegistry.trayOnExit);
        propertyChangeManager.setShowTrayPopup(SettingsRegistry.showTrayPopup);

        /* View */
        propertyChangeManager
                .setDisplaySingleTab(SettingsRegistry.displaySingleTab);
        propertyChangeManager.setSimpleTabs(SettingsRegistry.simpleTabs);
        propertyChangeManager
                .setShowTabCloseButton(SettingsRegistry.showTabCloseButton);
        propertyChangeManager.setFocusNewTabs(SettingsRegistry.focusNewTabs);
        propertyChangeManager
                .setTabPositionTop(SettingsRegistry.tabPositionIsTop);

        propertyChangeManager.setShowToolbar(SettingsRegistry.showToolbar);
        propertyChangeManager.setShowStatusbar(SettingsRegistry.showStatusbar);
        propertyChangeManager.setReportErrors(SettingsRegistry.reportErrors);

        /* Browser tab */
        propertyChangeManager
                .setDefaultHomepage(SettingsRegistry.defaultHomepage);

        /* Query tab */
        propertyChangeManager
                .setShowMetadataPane(SettingsRegistry.showMetadataPane);
        propertyChangeManager
                .setHighlightTerms(SettingsRegistry.highlightTerms);
        propertyChangeManager.setShowTooltip(SettingsRegistry.showTooltip);
        propertyChangeManager.setGlimpse(SettingsRegistry.glimpse);
        propertyChangeManager.setInvestigate(SettingsRegistry.investigate);

        /* Transfer tab */
        propertyChangeManager
                .setDownloadDirectoryPath(SettingsRegistry.downloadDirectoryPath);
    }

    protected Button apply;

    protected Composite composite;

    protected Font dialogFont = FontUtil.dialogFont;

    protected CLabel errorMessageLabel;

    protected Composite parent;

    protected Button restore;

    protected GUI fetcherGui;

    protected PropertyPage(Composite parent) {
        this(parent, null);
    }

    protected PropertyPage(Composite parent, GUI fetcherGui) {
        this.parent = parent;
        this.fetcherGui = fetcherGui;
        createComposite();
        initComponents();
        createButtons();
    }

    public abstract void applyButtonPressed();

    protected void createButtons() {

        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END
                | GridData.VERTICAL_ALIGN_END);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;

        Composite buttonHolder = new Composite(composite, SWT.NONE);
        buttonHolder.setLayoutData(gridData);
        buttonHolder.setLayout(LayoutUtil.createGridLayout(2, 0, 0));

        restore = new Button(buttonHolder, SWT.NONE);
        restore.setText("Restore defaults");
        restore.setFont(dialogFont);
        restore.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                restoreButtonPressed();
            }
        });

        apply = new Button(buttonHolder, SWT.NONE);
        apply.setText("Apply");
        apply.setFont(dialogFont);
        apply.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                applyButtonPressed();
            }
        });

        setButtonLayoutData(restore);
        setButtonLayoutData(apply);

        WidgetUtil.initMnemonics(new Button[] { restore, apply });
    }

    protected void createComposite() {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.FILL_BOTH));
    }

    public void dispose() {
        composite.dispose();
    }

    protected abstract void initComponents();

    protected abstract void restoreButtonPressed();

    protected void setApplyButtonState(boolean enabled) {
        apply.setEnabled(enabled);
    }

    protected void setButtonLayoutData(Button button) {
        setButtonLayoutData(button, new GridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING));
    }

    protected void setButtonLayoutData(Button button, GridData data) {

        GC gc = new GC(button);
        FontMetrics fontMetrics = gc.getFontMetrics();

        int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
                IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);

        gc.dispose();
    }

    protected void setErrorMessage(String errorMessage) {
        errorMessageLabel.setImage(errorMessage == null ? null
                : PaintUtil.iconError);
        errorMessageLabel.setText(errorMessage == null ? "" : errorMessage);
        errorMessageLabel.getParent().layout();
    }

    protected void setRestoreButtonState(boolean enabled) {
        restore.setEnabled(enabled);
    }

    protected void setSuccessMessage(String successMessage) {
        errorMessageLabel.setImage(successMessage == null ? null
                : PaintUtil.iconInformation);
        errorMessageLabel.setText(successMessage == null ? "" : successMessage);
        errorMessageLabel.getParent().layout();
    }

    protected void setWarningMessage(String warningMessage) {
        errorMessageLabel.setImage(warningMessage == null ? null
                : PaintUtil.iconWarning);
        errorMessageLabel.setText(warningMessage == null ? "" : warningMessage);
        errorMessageLabel.getParent().layout();
    }

    public abstract void updatePropertiesChangeManager();
}
