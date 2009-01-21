package net.brokentrain.ftf.ui.gui.tabs.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

public class ToolTipHandler {

    private Shell parentShell;

    private Shell tipShell;

    private Label tipLabelImage, tipLabelText;

    private Widget tipWidget;

    private Point tipPosition;

    public ToolTipHandler(Shell parent) {
        final Display display = parent.getDisplay();
        this.parentShell = parent;

        tipShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 2;
        tipShell.setLayout(gridLayout);

        tipShell.setBackground(display
                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));

        tipLabelImage = new Label(tipShell, SWT.NONE);
        tipLabelImage.setForeground(display
                .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        tipLabelImage.setBackground(display
                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        tipLabelImage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_CENTER));

        tipLabelText = new Label(tipShell, SWT.NONE);
        tipLabelText.setForeground(display
                .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        tipLabelText.setBackground(display
                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        tipLabelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_CENTER));
    }

    public void activateHoverHelp(final Control control) {
        control.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (tipShell.isVisible()) {
                    tipShell.setVisible(false);
                }
            }
        });

        control.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseExit(MouseEvent e) {
                if (tipShell.isVisible()) {
                    tipShell.setVisible(false);
                }
                tipWidget = null;
            }

            @Override
            public void mouseHover(MouseEvent event) {
                Point pt = new Point(event.x, event.y);
                Widget widget = event.widget;
                if (widget instanceof ToolBar) {
                    ToolBar w = (ToolBar) widget;
                    widget = w.getItem(pt);
                }
                if (widget instanceof Table) {
                    Table w = (Table) widget;
                    widget = w.getItem(pt);
                }
                if (widget instanceof Tree) {
                    Tree w = (Tree) widget;
                    widget = w.getItem(pt);
                }
                if (widget == null) {
                    tipShell.setVisible(false);
                    tipWidget = null;
                    return;
                }
                if (widget == tipWidget) {
                    return;
                }
                tipWidget = widget;
                tipPosition = control.toDisplay(pt);
                String text = (String) widget.getData("TIP_TEXT");
                Image image = (Image) widget.getData("TIP_IMAGE");
                tipLabelText.setText(text != null ? text : "");
                tipLabelImage.setImage(image); // accepts null
                tipShell.pack();
                setHoverLocation(tipShell, tipPosition);
                tipShell.setVisible(true);
            }
        });

        control.addHelpListener(new HelpListener() {
            public void helpRequested(HelpEvent event) {
                if (tipWidget == null) {
                    return;
                }
                ToolTipHelpTextHandler handler = (ToolTipHelpTextHandler) tipWidget
                        .getData("TIP_HELPTEXTHANDLER");
                if (handler == null) {
                    return;
                }
                String text = handler.getHelpText(tipWidget);
                if (text == null) {
                    return;
                }

                if (tipShell.isVisible()) {
                    tipShell.setVisible(false);
                    Shell helpShell = new Shell(parentShell, SWT.SHELL_TRIM);
                    helpShell.setLayout(new FillLayout());
                    Label label = new Label(helpShell, SWT.NONE);
                    label.setText(text);
                    helpShell.pack();
                    setHoverLocation(helpShell, tipPosition);
                    helpShell.open();
                }
            }
        });
    }

    private void setHoverLocation(Shell shell, Point position) {
        Rectangle displayBounds = shell.getDisplay().getBounds();
        Rectangle shellBounds = shell.getBounds();
        shellBounds.x = Math.max(Math.min(position.x, displayBounds.width
                - shellBounds.width), 0);
        shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height
                - shellBounds.height), 0);
        shell.setBounds(shellBounds);
    }
}
