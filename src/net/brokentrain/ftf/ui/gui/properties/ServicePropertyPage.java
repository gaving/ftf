package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public abstract class ServicePropertyPage {

    protected Composite composite;

    protected Composite parent;

    protected SearchService service;

    protected Font dialogFont = FontUtil.dialogFont;

    protected ServicePropertyPage(Composite parent, SearchService service) {
        this.parent = parent;
        this.service = service;

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
