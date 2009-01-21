package net.brokentrain.ftf.ui.gui.tabs;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class StatusTab extends Tab {

    private CTabFolder tabFolder;

    private Table table;

    public StatusTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager) {
        super(tabItem, tabManager);

        this.tabItem = tabItem;

        tabFolder = tabItem.getParent();

        initComponents();
    }

    public void clear() {

        if (WidgetUtil.isset(table)) {
            table.removeAll();
        }
    }

    public int getRowIndex(Table table, Dispatcher dispatcher) {

        if (WidgetUtil.isset(table)) {

            for (TableItem item : table.getItems()) {
                Dispatcher itemData = (Dispatcher) item.getData();
                if (itemData == dispatcher) {
                    return table.indexOf(item);
                }
            }
        }

        return -1;
    }

    public void initComponents() {

        tabItem.setText("Status");
        tabItem.setImage(PaintUtil.iconStatus);
        tabItem.setToolTipText("View status of active sources");

        table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tcService = new TableColumn(table, SWT.NULL);
        tcService.setText("Service");
        tcService.setWidth(120);

        TableColumn tcProgress = new TableColumn(table, SWT.NULL);
        tcProgress.setText("Status");
        tcProgress.setWidth(200);

        TableColumn tcTerm = new TableColumn(table, SWT.LEFT);
        tcTerm.setText("Term(s)");
        tcTerm.setWidth(100);

        table.pack();
        tabItem.setControl(table);
    }

    public void setTabItem(CTabItem tabItem) {
        this.tabItem = tabItem;

        tabItem.setText("Status");
        tabItem.setImage(PaintUtil.iconStatus);
        tabItem.setToolTipText("View search status");

        if (WidgetUtil.isset(table)) {
            this.tabItem.setControl(table);
        }
    }

    public void updateStatus(Dispatcher dispatcher) {

        /* If the status window isn't there, forget this */
        if (!WidgetUtil.isset(table)) {
            return;
        }

        TableItem item;

        /* Get the existing row item so we can just change its status */
        int rowIndex = getRowIndex(table, dispatcher);
        if (rowIndex != -1) {

            item = table.getItem(rowIndex);
        } else {

            /* Create a whole new item */
            item = new TableItem(table, SWT.NONE);

            /* Get dispatcher information */
            String searchTerm = dispatcher.getQueryTerm();
            String description = dispatcher.getDescription();
            String name = dispatcher.getName();

            /* Set all the dispatcher info we can at first */
            item.setImage(PaintUtil.getServiceIcon(name));
            item.setText(0, description);
            item.setText(2, searchTerm);

            /* Assign the dispatcher object for later */
            item.setData(dispatcher);
        }

        /* Get dispatcher status */
        Dispatcher.EventType status = dispatcher.getStatus();

        /* Colour item according to status */
        switch (status) {
        case INIT:
            item.setText(1, "Starting up");
            item.setForeground(1, ColourUtil.darkYellow);
            break;

        case WORKING:
            item.setText(1, "Searching");
            item.setForeground(1, ColourUtil.red);
            break;

        case SLEEP:
            item.setText(1, "Sleeping");
            item.setForeground(1, ColourUtil.blue);
            break;

        case DEAD:
            item.setText(1, "Finished");
            item.setForeground(1, ColourUtil.gray);
            break;

        case ERROR:
            item.setText(1, "Error");
            item.setForeground(1, ColourUtil.darkRed);
            break;
        }

        /* Pack the service column */
        table.getColumns()[0].pack();

        /* Pack the status column */
        table.getColumns()[1].pack();
    }

}
