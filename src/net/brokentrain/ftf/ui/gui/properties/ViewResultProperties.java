package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ViewResultProperties extends ResultPropertyPage {

    public ViewResultProperties(Composite parent, Result result) {
        super(parent, result);
    }

    @Override
    protected void initComponents() {

        Label resultURLLabel = new Label(composite, SWT.NONE);
        resultURLLabel.setText("URI");
        resultURLLabel.setFont(FontUtil.dialogFont);

        final Text resultURL = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        resultURL.setText(result.getURI().toString());
        resultURL.setFont(FontUtil.dialogFont);
        resultURL.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (StringUtil.isset(result.getURI().toString())) {
            resultURL.setCursor(GUI.display.getSystemCursor(SWT.CURSOR_HAND));
            resultURL.setToolTipText("Open this URL");
            resultURL.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseUp(MouseEvent e) {

                    if ((e.button == 1) && (resultURL.getSelectionCount() == 0)) {
                        GUI.fetcherGui.getEventManager().actionLaunchURL(
                                result.getURI().toString());
                    }
                }
            });
        }

        WidgetUtil.tweakTextWidget(resultURL);

        Label resultFilenameLabel = new Label(composite, SWT.NONE);
        resultFilenameLabel.setText("Filename:");
        resultFilenameLabel.setFont(FontUtil.dialogFont);

        final Text resultFilename = new Text(composite, SWT.READ_ONLY
                | SWT.BORDER);
        resultFilename.setText(resourceData.getFilename());
        resultFilename.setFont(FontUtil.dialogFont);
        resultFilename.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(resultFilename);

        Label resultContentTypeLabel = new Label(composite, SWT.NONE);
        resultContentTypeLabel.setText("Content Type:");
        resultContentTypeLabel.setFont(FontUtil.dialogFont);

        Text resultContentType = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        resultContentType.setFont(FontUtil.dialogFont);
        resultContentType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (resourceData.getContentType() != null) {
            resultContentType.setText(resourceData.getContentType().toString());
        } else {
            resultContentType.setText("Unknown");
            resultContentType.setEnabled(false);
        }

        WidgetUtil.tweakTextWidget(resultContentType);

        Label resultLastModifiedDateLabel = new Label(composite, SWT.NONE);
        resultLastModifiedDateLabel.setText("Last Modified:");
        resultLastModifiedDateLabel.setFont(FontUtil.dialogFont);

        Text resultLastModifiedDate = new Text(composite, SWT.READ_ONLY
                | SWT.BORDER);
        resultLastModifiedDate.setFont(FontUtil.dialogFont);
        resultLastModifiedDate.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL));

        if (resourceData.getLastModified() != null) {
            resultLastModifiedDate.setText(StringUtil.formatDate(resourceData
                    .getLastModified()));
        } else {
            resultLastModifiedDate.setText("Unknown");
            resultLastModifiedDate.setEnabled(false);
        }

        WidgetUtil.tweakTextWidget(resultLastModifiedDate);

        Label resultSizeLabel = new Label(composite, SWT.NONE);
        resultSizeLabel.setText("Size:");
        resultSizeLabel.setFont(FontUtil.dialogFont);

        Text resultSize = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        resultSize.setFont(FontUtil.dialogFont);
        resultSize.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if ((StringUtil.isset(resourceData.getSize()))
                && (!resourceData.getSize().equals("-1"))) {
            resultSize.setText(resourceData.getSize() + " bytes");
        } else {
            resultSize.setText("Unknown");
            resultSize.setEnabled(false);
        }

        WidgetUtil.tweakTextWidget(resultSize);
    }
}
