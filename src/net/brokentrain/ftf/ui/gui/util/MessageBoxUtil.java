package net.brokentrain.ftf.ui.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MessageBoxUtil {

    public static void showError(Shell shell, Exception e) {
        showError(shell, e.getLocalizedMessage());
    }

    public static void showError(Shell shell, String errorMsg) {

        if (shell.isDisposed()) {
            shell = new Shell(Display.getCurrent());
        }

        MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
        box.setText("Error");
        box.setMessage(errorMsg);
        box.open();
    }

    public static int showMessage(Shell shell, int style, String title,
            String message) {

        if (shell.isDisposed()) {
            shell = new Shell(Display.getCurrent());
        }

        MessageBox box = new MessageBox(shell, style);

        box.setText(title);
        box.setMessage(message);

        return box.open();
    }

    private MessageBoxUtil() {
    }
}
