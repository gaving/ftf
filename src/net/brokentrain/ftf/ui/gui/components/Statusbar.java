package net.brokentrain.ftf.ui.gui.components;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class Statusbar {

    private Shell shell;

    private CLabel statusTextLabel;

    private Composite statusArea;

    private Composite statusBarHolder;

    private Composite statusUpdate;

    private ProgressBar statusProgress;

    public Statusbar(GUI fetcherGui, Shell shell, EventManager eventManager) {

        this.shell = shell;
        initComponents();
    }

    public void createStatusBar() {

        /* Create the actual status area */
        statusArea = new Composite(statusBarHolder, SWT.NULL);
        statusArea.setLayout(new StackLayout());
        statusArea.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_BOTH, 1));

        /* Create the text area */
        statusUpdate = new Composite(statusArea, SWT.NULL);
        statusUpdate.setLayout(LayoutUtil.createGridLayout(2, 0, 0, false));
        statusUpdate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_FILL));

        /* Create a text label */
        statusTextLabel = new CLabel(statusUpdate, SWT.SHADOW_IN);
        statusTextLabel.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        /* Create a new progress bar and hide it by default */
        statusProgress = new ProgressBar(statusUpdate, SWT.HORIZONTAL
                | SWT.INDETERMINATE);
        GridData statusProgressData = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.GRAB_VERTICAL);
        statusProgressData.exclude = true;
        statusProgress.setLayoutData(statusProgressData);
        statusProgress.setVisible(false);

        ((StackLayout) statusArea.getLayout()).topControl = statusUpdate;

        statusArea.layout();
        shell.layout();
    }

    public void initComponents() {

        /* Create new status line holder */
        statusBarHolder = new Composite(shell, SWT.NONE);
        statusBarHolder.setLayout(LayoutUtil.createGridLayout(4, 0, 0, 0, 0,
                false));
        statusBarHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 4));
    }

    public void setShowStatusBar(boolean show) {

        if (!show && statusBarHolder.getVisible()) {
            setShowStatusComposite(false);
        } else if (show && !statusBarHolder.getVisible()) {
            setShowStatusComposite(true);
        }

        if (WidgetUtil.isset(shell) && GUI.isAlive()) {
            shell.layout();
        }

        SettingsRegistry.showStatusbar = show;
    }

    public void setShowStatusComposite(boolean show) {

        statusBarHolder.setVisible(show);

        ((GridData) statusBarHolder.getLayoutData()).exclude = !show;
    }

    public void setText(String text) {
        if (WidgetUtil.isset(statusTextLabel)) {

            /* Set the new text with a timestamp */
            String datenewformat = new SimpleDateFormat("H:mm:ss")
                    .format(new Date());
            statusTextLabel.setText("[" + datenewformat + "]: " + text);
        }
    }

    public void showProgressBar(boolean show) {
        if (WidgetUtil.isset(statusProgress)) {

            if (show) {
                statusProgress.setVisible(true);
                ((GridData) statusProgress.getLayoutData()).exclude = false;
            } else {
                statusProgress.setVisible(false);
                ((GridData) statusProgress.getLayoutData()).exclude = true;
            }
            statusProgress.getParent().layout();
        }
    }
}
