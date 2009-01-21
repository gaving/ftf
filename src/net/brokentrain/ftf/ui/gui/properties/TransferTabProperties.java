package net.brokentrain.ftf.ui.gui.properties;

import java.util.ArrayList;

import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

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

public class TransferTabProperties extends PropertyPage {

    private Text directoryPath;

    public TransferTabProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        ArrayList<Button> buttons = new ArrayList<Button>();

        Group directoryGroup = new Group(composite, SWT.NONE);
        directoryGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        directoryGroup.setText("Download directory");
        directoryGroup.setLayout(new GridLayout(2, false));
        directoryGroup.setFont(FontUtil.dialogFont);

        Label directoryMessageLabel = new Label(directoryGroup, SWT.WRAP);
        directoryMessageLabel
                .setText("Choose a download directory to save downloaded papers");
        directoryMessageLabel.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        directoryMessageLabel.setFont(dialogFont);

        directoryPath = new Text(directoryGroup, SWT.SINGLE | SWT.BORDER);
        directoryPath.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        directoryPath.setFont(dialogFont);
        directoryPath.setText(propertyChangeManager.getDownloadDirectoryPath());
        directoryPath.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                boolean directoryExists = FileUtil.isDirectory(directoryPath
                        .getText());

                if (StringUtil.isset(directoryPath.getText())) {
                    setErrorMessage(directoryExists ? null
                            : "Folder not found!");
                } else {
                    setErrorMessage(null);
                }
            }
        });

        Button searchDirectory = new Button(directoryGroup, SWT.PUSH);
        searchDirectory.setText("Search...");
        searchDirectory.setFont(dialogFont);
        searchDirectory.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String directory = FileUtil.getDirectory(composite.getShell(),
                        directoryPath.getText(),
                        "Select directory to store downloaded papers");

                /* Set the directory if it is not empty */
                if (directory != null) {
                    directoryPath.setText(directory);
                }
            }
        });

        setButtonLayoutData(searchDirectory);

        buttons.add(searchDirectory);

        errorMessageLabel = new CLabel(directoryGroup, SWT.NONE);
        errorMessageLabel.setFont(dialogFont);
        errorMessageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));

        if (StringUtil.isset(directoryPath.getText())) {
            setErrorMessage(FileUtil.isDirectory(directoryPath.getText()) ? null
                    : "File not found!");
        }

        LayoutUtil.setDialogSpacer(composite, 2, 3);
    }

    @Override
    protected void restoreButtonPressed() {
        directoryPath.setText("");
        updatePropertiesChangeManager();
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setDownloadDirectoryPath(directoryPath.getText());
    }

}
