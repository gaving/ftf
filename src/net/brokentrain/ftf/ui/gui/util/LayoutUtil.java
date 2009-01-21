package net.brokentrain.ftf.ui.gui.util;

import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LayoutUtil {

    public static void centerShell(Display display, Shell shell) {
        Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
        Rectangle shellBounds = shell.getBounds();
        int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
        int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
        shell.setLocation(x, y);
    }

    public static FillLayout createFillLayout(int marginWidth, int marginHeight) {
        FillLayout f = new FillLayout();
        f.marginHeight = marginHeight;
        f.marginWidth = marginWidth;
        return f;
    }

    public static GridLayout createGridLayout(int cols) {
        return createGridLayout(cols, 5, 5, 5, 5, false);
    }

    public static GridLayout createGridLayout(int cols, int marginWidth) {
        return createGridLayout(cols, marginWidth, 5, 5, 5, false);
    }

    public static GridLayout createGridLayout(int cols, int marginWidth,
            int marginHeight) {
        return createGridLayout(cols, marginWidth, marginHeight, 5, 5, false);
    }

    public static GridLayout createGridLayout(int cols, int marginWidth,
            int marginHeight, boolean makeColumnsEqualWidth) {
        return createGridLayout(cols, marginWidth, marginHeight, 5, 5,
                makeColumnsEqualWidth);
    }

    public static GridLayout createGridLayout(int cols, int marginWidth,
            int marginHeight, int verticalSpacing) {
        return createGridLayout(cols, marginWidth, marginHeight,
                verticalSpacing, 5, false);
    }

    public static GridLayout createGridLayout(int cols, int marginWidth,
            int marginHeight, int verticalSpacing, int horizontalSpacing,
            boolean makeColumnsEqualWidth) {
        GridLayout g = new GridLayout(cols, makeColumnsEqualWidth);
        g.marginHeight = marginHeight;
        g.marginWidth = marginWidth;
        g.verticalSpacing = verticalSpacing;
        g.horizontalSpacing = horizontalSpacing;
        return g;
    }

    public static void positionShell(Shell shell, boolean computeSize,
            int sameDialogCount) {
        Rectangle containerBounds = GUI.shell.getBounds();
        Point initialSize = (computeSize == true) ? shell.computeSize(
                SWT.DEFAULT, SWT.DEFAULT, true) : shell.getSize();
        int x = Math.max(0, containerBounds.x
                + (containerBounds.width - initialSize.x) >> 1);
        int y = Math.max(0, containerBounds.y
                + (containerBounds.height - initialSize.y) / 3);
        shell.setLocation(x + sameDialogCount * 20, y + sameDialogCount * 20);
    }

    public static void setDialogSpacer(Composite composite, int cols, int rows) {
        for (int a = 0; a < rows; a++) {
            Label spacer = new Label(composite, SWT.NONE);
            spacer.setLayoutData(LayoutDataUtil.createGridData(
                    GridData.HORIZONTAL_ALIGN_BEGINNING, cols));
            spacer.setFont(FontUtil.dialogFont);
        }
    }

    public static void setLayoutForAll(Control control) {
        if (control instanceof Composite) {
            Control[] childs = ((Composite) control).getChildren();
            for (Control element : childs) {
                setLayoutForAll(element);
            }

            ((Composite) control).layout();
        }
    }

    private LayoutUtil() {
    }
}
