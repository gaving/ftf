package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

public class CrawlerProperties extends PropertyPage {

    public CrawlerProperties(Composite parent, GUI fetcherGui) {
        super(parent, fetcherGui);
    }

    @Override
    public void applyButtonPressed() {
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        Group tokensGroup = new Group(composite, SWT.NONE);
        tokensGroup.setText("Tokens");
        tokensGroup.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        tokensGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        tokensGroup.setFont(FontUtil.dialogFont);

        List list = new List(tokensGroup, SWT.BORDER | SWT.SINGLE
                | SWT.V_SCROLL | SWT.H_SCROLL);
        list.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
    }

    @Override
    protected void restoreButtonPressed() {
    }

    void setTabControlsEnabled(boolean enabled) {
    }

    @Override
    public void updatePropertiesChangeManager() {
    }
}
