package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.ProxyUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class ConnectionProperties extends PropertyPage {

    private static final int MIN_READ_TIMEOUT = 1;

    private static final int MAX_READ_TIMEOUT = 9999;

    private static final int MIN_CONNECT_TIMEOUT = 1;

    private static final int MAX_CONNECT_TIMEOUT = 9999;

    private Button checkUpdateAll;

    private Button checkUseProxy;

    private Spinner readTimeoutSpinner;

    private Spinner conTimeoutSpinner;

    private Text textHost;

    private Text textPort;

    Text textUsername;

    public ConnectionProperties(Composite parent, GUI fetcherGui) {
        super(parent, fetcherGui);
    }

    @Override
    public void applyButtonPressed() {
        SettingsRegistry.readTimeout = readTimeoutSpinner.getSelection();
        SettingsRegistry.connectionTimeout = conTimeoutSpinner.getSelection();
        updateProxySettings();
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        Group proxyGroup = new Group(composite, SWT.NONE);
        proxyGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        proxyGroup.setText("Proxy");
        proxyGroup.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        proxyGroup.setFont(FontUtil.dialogFont);

        Composite topHolder = new Composite(proxyGroup, SWT.NONE);
        topHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        topHolder.setLayout(new GridLayout(2, false));

        checkUseProxy = new Button(topHolder, SWT.CHECK);
        checkUseProxy.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        checkUseProxy.setText("Use proxy");
        checkUseProxy.setFont(dialogFont);

        Label labelHost = new Label(proxyGroup, SWT.NONE);
        labelHost.setText("Host: ");
        labelHost.setFont(dialogFont);
        labelHost.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER
                | GridData.HORIZONTAL_ALIGN_BEGINNING));

        textHost = new Text(proxyGroup, SWT.BORDER);
        textHost.setFont(dialogFont);
        textHost.setText(propertyChangeManager.getProxySettingsSave().get(
                "proxyHost"));
        textHost.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(textHost);

        Label labelPort = new Label(proxyGroup, SWT.NONE);
        labelPort.setText("Port: ");
        labelPort.setFont(dialogFont);
        labelPort.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER
                | GridData.HORIZONTAL_ALIGN_BEGINNING));

        textPort = new Text(proxyGroup, SWT.BORDER);
        textPort.setFont(dialogFont);
        textPort.setText(propertyChangeManager.getProxySettingsSave().get(
                "proxyPort"));
        textPort.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(textPort);

        checkUseProxy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabledComponents(checkUseProxy.getSelection());
            }
        });

        Composite bottomHolder = new Composite(proxyGroup, SWT.NONE);
        bottomHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        bottomHolder.setLayout(new GridLayout(2, false));

        if (propertyChangeManager.getProxySettingsSave().get("proxySet")
                .equals("false")) {
            setEnabledComponents(false);
        } else {
            checkUseProxy.setSelection(true);
        }

        Group connectionCountGroup = new Group(composite, SWT.NONE);
        connectionCountGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        connectionCountGroup.setText("Connection");
        connectionCountGroup
                .setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        connectionCountGroup.setFont(FontUtil.dialogFont);

        Label labelReadTimeout = new Label(connectionCountGroup, SWT.NONE);
        labelReadTimeout.setText("Read timeout: ");
        labelReadTimeout.setFont(dialogFont);
        labelReadTimeout.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_CENTER
                        | GridData.HORIZONTAL_ALIGN_BEGINNING));

        readTimeoutSpinner = new Spinner(connectionCountGroup, SWT.BORDER);
        readTimeoutSpinner.setMinimum(MIN_READ_TIMEOUT);
        readTimeoutSpinner.setMaximum(MAX_READ_TIMEOUT);
        readTimeoutSpinner.setSelection(propertyChangeManager
                .getMaxConnectionCount());
        readTimeoutSpinner.setFont(FontUtil.dialogFont);
        readTimeoutSpinner.setLayoutData(LayoutDataUtil.createGridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING, 1));

        Label labelConTimeout = new Label(connectionCountGroup, SWT.NONE);
        labelConTimeout.setText("Connection Timeout: ");
        labelConTimeout.setFont(dialogFont);
        labelConTimeout.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_CENTER
                        | GridData.HORIZONTAL_ALIGN_BEGINNING));

        conTimeoutSpinner = new Spinner(connectionCountGroup, SWT.BORDER);
        conTimeoutSpinner.setMinimum(MIN_CONNECT_TIMEOUT);
        conTimeoutSpinner.setMaximum(MAX_CONNECT_TIMEOUT);
        conTimeoutSpinner.setSelection(propertyChangeManager
                .getConnectionTimeout());
        conTimeoutSpinner.setFont(FontUtil.dialogFont);
        conTimeoutSpinner.setLayoutData(LayoutDataUtil.createGridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING, 1));

        WidgetUtil.initMnemonics(new Button[] { checkUseProxy });

    }

    @Override
    protected void restoreButtonPressed() {
        checkUseProxy.setSelection(false);
        setEnabledComponents(false);
        checkUpdateAll.setSelection(true);
        readTimeoutSpinner.setSelection(3000);
        conTimeoutSpinner.setSelection(3000);
        updatePropertiesChangeManager();
    }

    void setEnabledComponents(boolean enabled) {
        textHost.setEnabled(enabled);
        textPort.setEnabled(enabled);
        checkUseProxy.setSelection(enabled);
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.getProxySettingsSave().put("proxySet",
                String.valueOf(checkUseProxy.getSelection()));
        propertyChangeManager.getProxySettingsSave().put("proxyHost",
                textHost.getText());
        propertyChangeManager.getProxySettingsSave().put("proxyPort",
                textPort.getText());
        propertyChangeManager.setReadTimeout(readTimeoutSpinner.getSelection());
        propertyChangeManager.setConnectionTimeout(conTimeoutSpinner
                .getSelection());
    }

    private void updateProxySettings() {
        ProxyUtil.setUseProxy(String.valueOf(checkUseProxy.getSelection()));
        ProxyUtil.setHost(textHost.getText());
        ProxyUtil.setPort(textPort.getText());
    }
}
