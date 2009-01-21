package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BrowserTabProperties extends PropertyPage {

    private Text textDefaultHomepage;

    public BrowserTabProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {
        SettingsRegistry.defaultHomepage = textDefaultHomepage.getText();
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {
        Group generalGroup = new Group(composite, SWT.NONE);
        generalGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        generalGroup.setText("General");
        generalGroup.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        generalGroup.setFont(FontUtil.dialogFont);

        Label labelHomepage = new Label(generalGroup, SWT.NONE);
        labelHomepage.setText("Homepage: ");
        labelHomepage.setFont(dialogFont);
        labelHomepage.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER
                | GridData.HORIZONTAL_ALIGN_BEGINNING));

        textDefaultHomepage = new Text(generalGroup, SWT.BORDER);
        textDefaultHomepage.setFont(dialogFont);
        textDefaultHomepage.setText(propertyChangeManager.getDefaultHomepage());
        textDefaultHomepage.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(textDefaultHomepage);
    }

    @Override
    protected void restoreButtonPressed() {
        textDefaultHomepage.setText("");
        updatePropertiesChangeManager();
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setDefaultHomepage(textDefaultHomepage.getText());
    }

}
