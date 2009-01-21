package net.brokentrain.ftf.ui.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;

public class LayoutDataUtil {

    public static FormData createFormData(int marginLeft, int marginRight,
            int marginTop, int marginBottom) {
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, marginTop);
        formData.left = new FormAttachment(0, marginLeft);
        formData.right = new FormAttachment(100, marginRight);
        formData.bottom = new FormAttachment(100, marginBottom);
        return formData;
    }

    public static GridData createGridData(int style, int horizontalSpan) {
        return createGridData(style, horizontalSpan, SWT.DEFAULT);
    }

    public static GridData createGridData(int style, int horizontalSpan,
            int widthHint) {
        return createGridData(style, horizontalSpan, widthHint, SWT.DEFAULT);
    }

    public static GridData createGridData(int style, int horizontalSpan,
            int widthHint, int heightHint) {
        GridData g = new GridData(style);
        g.horizontalSpan = horizontalSpan;
        g.widthHint = widthHint;
        g.heightHint = heightHint;
        return g;
    }

    private LayoutDataUtil() {
    }
}
