package net.brokentrain.ftf.ui.gui.dialog;

import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.services.WebSearchService;
import net.brokentrain.ftf.ui.gui.properties.ViewServiceProperties;
import net.brokentrain.ftf.ui.gui.properties.ViewWebServiceProperties;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ServiceDialog extends Dialog {

    private static final int dialogMinWidth = 400;

    private SearchService service;

    private String title;

    private TabFolder tabFolder;

    private TabItem infoItem;

    public ServiceDialog(SearchService service, Shell parentShell, String title) {
        super(parentShell);
        this.title = title;
        this.service = service;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        shell.setLayout(LayoutUtil.createGridLayout(1, 0, 5));

        shell.setText(title);
        shell.setSize(0, 0);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        ((GridLayout) parent.getLayout()).marginHeight = 5;
        ((GridLayout) parent.getLayout()).marginWidth = 10;

        createButton(parent, IDialogConstants.OK_ID, "OK", true).setFont(
                FontUtil.dialogFont);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite baseComposite = (Composite) super.createDialogArea(parent);
        baseComposite.setLayout(LayoutUtil.createFillLayout(5, 5));
        baseComposite
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tabFolder = new TabFolder(baseComposite, SWT.TOP);
        tabFolder.setFont(FontUtil.dialogFont);

        infoItem = new TabItem(tabFolder, SWT.NONE);
        infoItem.setText("Information");

        Composite infoContentHolder = new Composite(tabFolder, SWT.NONE);
        infoContentHolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                true));
        infoContentHolder.setLayout(LayoutUtil.createGridLayout(1, 5, 0));

        /* View basic service properties */
        new ViewServiceProperties(infoContentHolder, service);

        if (service instanceof WebSearchService) {

            TabItem webItem = new TabItem(tabFolder, SWT.NONE);
            webItem.setText("Web Properties");

            Composite webContentHolder = new Composite(tabFolder, SWT.NONE);
            webContentHolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                    true, true));
            webContentHolder.setLayout(LayoutUtil.createGridLayout(1, 5, 0));

            new ViewWebServiceProperties(webContentHolder, service);

            webItem.setControl(webContentHolder);
        }

        infoItem.setControl(infoContentHolder);

        return baseComposite;
    }

    @Override
    protected int getShellStyle() {
        int style = SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.APPLICATION_MODAL
                | getDefaultOrientation();

        return style;
    }

    @Override
    protected void initializeBounds() {
        initializeBounds(true);
    }

    protected void initializeBounds(boolean updateLocation) {
        super.initializeBounds();
        Point currentSize = getShell().getSize();

        Point bestSize = getShell().computeSize(
                convertHorizontalDLUsToPixels(dialogMinWidth), SWT.DEFAULT);

        Point location = (updateLocation == true) ? getInitialLocation(bestSize)
                : getShell().getLocation();

        if (bestSize.y > currentSize.y) {
            getShell()
                    .setBounds(location.x, location.y, bestSize.x, bestSize.y);
        }

        getShell().setMinimumSize(bestSize.x, bestSize.y);
    }

    @Override
    protected void setButtonLayoutData(Button button) {
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
    }
}
