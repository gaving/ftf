package net.brokentrain.ftf.ui.gui.dialog;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.BrowserUtil;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.URLUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

    private static final int LOGO_SIZE = 250;

    private Label fetcherIcon;

    private String title;

    public AboutDialog(Shell parentShell, String dialogTitle) {
        super(parentShell);
        this.title = dialogTitle;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        ((GridLayout) parent.getLayout()).marginHeight = 10;
        ((GridLayout) parent.getLayout()).marginWidth = 10;

        createButton(parent, IDialogConstants.OK_ID, "OK", true).setFont(
                FontUtil.dialogFont);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(LayoutUtil.createGridLayout(1, 0, 0));
        composite.setBackground(GUI.display.getSystemColor(SWT.COLOR_WHITE));

        Composite bannerHolder = new Composite(composite, SWT.NONE);
        bannerHolder.setLayout(LayoutUtil.createFillLayout(0, 0));
        bannerHolder.setBackground(GUI.display.getSystemColor(SWT.COLOR_WHITE));

        fetcherIcon = new Label(bannerHolder, SWT.NONE);
        fetcherIcon.setImage(PaintUtil.iconFetcher);
        fetcherIcon.setCursor(GUI.display.getSystemCursor(SWT.CURSOR_HAND));
        fetcherIcon.setToolTipText(URLUtil.FTF_WEBPAGE);
        fetcherIcon.setBackground(GUI.display.getSystemColor(SWT.COLOR_WHITE));
        fetcherIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                BrowserUtil.openLink(URLUtil.FTF_WEBPAGE);
            }
        });

        Composite aboutTextHolder = new Composite(composite, SWT.NONE);
        aboutTextHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 1, LOGO_SIZE));
        aboutTextHolder.setLayout(LayoutUtil.createFillLayout(10, 10));
        aboutTextHolder.setBackground(GUI.display
                .getSystemColor(SWT.COLOR_WHITE));

        StyledText aboutTextLabel = new StyledText(aboutTextHolder, SWT.MULTI
                | SWT.WRAP | SWT.READ_ONLY);
        aboutTextLabel.setText((char) 169
                + " 2006-2007. All rights reserved.\n\n");
        aboutTextLabel.setFont(FontUtil.dialogFont);
        aboutTextLabel.setCaret(null);
        aboutTextLabel.setBackground(GUI.display
                .getSystemColor(SWT.COLOR_WHITE));

        Composite sepHolder = new Composite(parent, SWT.NONE);
        sepHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        sepHolder.setLayout(LayoutUtil.createGridLayout(1, 0, 0));
        sepHolder.setBackground(GUI.display.getSystemColor(SWT.COLOR_WHITE));

        Label separator = new Label(sepHolder, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return composite;
    }

    @Override
    protected int getShellStyle() {
        int style = SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL
                | getDefaultOrientation();
        return style;
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
