package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public abstract class ResultPropertyPage {

    protected Composite composite;

    protected Composite parent;

    protected Resource resource;

    protected DataStore resourceData;

    protected Font dialogFont = FontUtil.dialogFont;

    protected Result result;

    protected ResultPropertyPage(Composite parent, Result result) {
        this.parent = parent;
        this.result = result;

        resource = result.getResource();
        resourceData = result.getResource().getData();

        createComposite();
        initComponents();
    }

    protected void createComposite() {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(2, 0, 10, 10));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    protected abstract void initComponents();
}
