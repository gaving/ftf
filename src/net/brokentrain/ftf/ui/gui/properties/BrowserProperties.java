package net.brokentrain.ftf.ui.gui.properties;

import java.util.ArrayList;

import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BrowserProperties extends PropertyPage {

    private Text browserArguments;

    private Text browserPath;

    public BrowserProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {
        SettingsRegistry.customBrowser = browserPath.getText();
        SettingsRegistry.customBrowserArguments = browserArguments.getText();
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        ArrayList<Button> buttons = new ArrayList<Button>();

        Group externalBrowserGroup = new Group(composite, SWT.NONE);
        externalBrowserGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        externalBrowserGroup.setText("Select browser");
        externalBrowserGroup.setLayout(new GridLayout(2, false));
        externalBrowserGroup.setFont(FontUtil.dialogFont);

        Label messageLabel = new Label(externalBrowserGroup, SWT.WRAP);
        messageLabel.setText("Select external browser");
        messageLabel.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        messageLabel.setFont(dialogFont);

        browserPath = new Text(externalBrowserGroup, SWT.SINGLE | SWT.BORDER);
        browserPath.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        browserPath.setFont(dialogFont);
        browserPath.setText(propertyChangeManager.getCustomBrowserPath());
        browserPath.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                browserArguments.setEnabled(!browserPath.getText().equals(""));

                boolean fileExists = FileUtil.exists(browserPath.getText());

                if (StringUtil.isset(browserPath.getText())) {
                    setErrorMessage(fileExists ? null : "Program not found!");
                } else {
                    setErrorMessage(null);
                }

            }
        });

        Button searchBrowser = new Button(externalBrowserGroup, SWT.PUSH);
        searchBrowser.setText("Search...");
        searchBrowser.setFont(dialogFont);
        searchBrowser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String file = FileUtil.getFilePath(composite.getShell(), null,
                        null, SWT.OPEN, browserPath.getText(), "Browser");

                if (file != null) {
                    browserPath.setText(file);
                }
            }
        });

        setButtonLayoutData(searchBrowser);

        buttons.add(searchBrowser);

        errorMessageLabel = new CLabel(externalBrowserGroup, SWT.NONE);
        errorMessageLabel.setFont(dialogFont);
        errorMessageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));

        if (StringUtil.isset(browserPath.getText())) {
            setErrorMessage(FileUtil.exists(browserPath.getText()) ? null
                    : "Program not found!");
        }

        Group argumentsGroup = new Group(externalBrowserGroup, SWT.NONE);
        argumentsGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        argumentsGroup.setText("Arguments");
        argumentsGroup.setLayout(new GridLayout(2, false));
        argumentsGroup.setFont(FontUtil.dialogFont);

        browserArguments = new Text(argumentsGroup, SWT.SINGLE | SWT.BORDER);
        browserArguments.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        browserArguments.setFont(dialogFont);
        browserArguments.setText(propertyChangeManager
                .getCustomBrowserArguments());

        WidgetUtil.tweakTextWidget(browserArguments);

        browserArguments.setEnabled(!browserPath.getText().equals(""));

        Label argumentsLabel = new Label(argumentsGroup, SWT.WRAP);
        argumentsLabel.setText("Enter any explicit arguments for the browser");
        argumentsLabel.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        argumentsLabel.setFont(dialogFont);

        LayoutUtil.setDialogSpacer(composite, 2, 3);
    }

    @Override
    protected void restoreButtonPressed() {
        browserPath.setText("");
        browserArguments.setText("");
        updatePropertiesChangeManager();
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setCustomBrowserPath(browserPath.getText());
        propertyChangeManager.setCustomBrowserArguments(browserArguments
                .getText());
    }

}
