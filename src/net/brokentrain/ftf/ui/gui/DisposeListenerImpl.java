package net.brokentrain.ftf.ui.gui;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public class DisposeListenerImpl implements DisposeListener {

    private static DisposeListenerImpl disposeListenerImpl;

    public static DisposeListenerImpl getInstance() {
        if (disposeListenerImpl == null) {
            disposeListenerImpl = new DisposeListenerImpl();
        }
        return disposeListenerImpl;
    }

    private DisposeListenerImpl() {
    }

    public void widgetDisposed(DisposeEvent e) {

        /* CLabel */
        if (e.widget instanceof CLabel) {
            if (((CLabel) e.widget).getImage() != null) {
                ((CLabel) e.widget).getImage().dispose();
            }
        }

        /* Label */
        else if (e.widget instanceof Label) {
            if (((Label) e.widget).getImage() != null) {
                ((Label) e.widget).getImage().dispose();
            }
        }

        /* CTabItem */
        else if (e.widget instanceof CTabItem) {
            if (((CTabItem) e.widget).getImage() != null) {
                ((CTabItem) e.widget).getImage().dispose();
            }
        }

        /* MenuItem */
        else if (e.widget instanceof MenuItem) {
            if (((MenuItem) e.widget).getImage() != null) {
                ((MenuItem) e.widget).getImage().dispose();
            }
        }

        /* ToolItem */
        else if (e.widget instanceof ToolItem) {
            if (((ToolItem) e.widget).getImage() != null) {
                ((ToolItem) e.widget).getImage().dispose();
            }
            if (((ToolItem) e.widget).getDisabledImage() != null) {
                ((ToolItem) e.widget).getDisabledImage().dispose();
            }
        }

        /* Button */
        else if (e.widget instanceof Button) {
            if (((Button) e.widget).getImage() != null) {
                ((Button) e.widget).getImage().dispose();
            }
        }
    }
}
