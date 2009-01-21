package net.brokentrain.ftf.ui.gui.tabs.components;

import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.swt.widgets.Widget;

/**
 * This interface is responsible for returning the text tooltip associated with
 * a particular item in a Tree or Table view.
 * 
 * @see GUI
 */
public interface ToolTipHelpTextHandler {

    /**
     * Return the text for a particular widget.
     * 
     * @param widget
     *            The widget to read from.
     * @return The tooltip text for this widget
     */
    public String getHelpText(Widget widget);
}
