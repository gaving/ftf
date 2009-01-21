package net.brokentrain.ftf.ui.gui.tabs;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.tabs.components.LogAppender;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.MessageBoxUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class LogTab extends Tab {

    private EventManager eventManager;

    private Table table;

    private CTabFolder tabFolder;

    public LogTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager) {
        super(tabItem, tabManager);

        tabFolder = tabItem.getParent();
        eventManager = fetcherGui.getEventManager();

        initComponents();
    }

    public void clear() {

        if (!table.isDisposed()) {
            table.removeAll();
        }
    }

    public void initComponents() {

        tabItem.setText("Log");
        tabItem.setImage(PaintUtil.iconLog);
        tabItem.setToolTipText("Progress log");

        table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION
                | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tcLevel = new TableColumn(table, SWT.LEFT);
        tcLevel.setText("Level");
        tcLevel.setWidth(70);

        TableColumn tcLocation = new TableColumn(table, SWT.NULL);
        tcLocation.setText("File");
        tcLocation.setWidth(130);

        TableColumn tcMessage = new TableColumn(table, SWT.NULL);
        tcMessage.setText("Message");
        tcMessage.setWidth(200);

        table.pack();

        table.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                TableItem tableItem = table
                        .getItem(new Point(event.x, event.y));
                if (WidgetUtil.isset(tableItem)) {
                    LoggingEvent loggingEvent = (LoggingEvent) tableItem
                            .getData();
                    if (loggingEvent != null) {
                        Object logMessage = loggingEvent.getMessage();

                        if (logMessage instanceof String) {

                            String logMessageString = (String) logMessage;
                            if (StringUtil.isset(logMessageString)) {

                                /* Open up a new message dialog */
                                MessageBoxUtil.showMessage(GUI.shell,
                                        SWT.ICON_INFORMATION | SWT.OK,
                                        "Message", logMessageString);
                            }
                        }
                    }
                }
            }
        });

        final Menu menu = new Menu(table);
        table.setMenu(menu);
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {

                /* Get rid of existing menu items */
                MenuItem[] items = menu.getItems();
                for (MenuItem element : items) {
                    (element).dispose();
                }

                final TableItem[] selectedItems = table.getSelection();

                /* Return on empty selection */
                if (selectedItems.length < 0) {
                    return;
                }

                /* Get the data of the first selection */
                LoggingEvent loggingEvent = (LoggingEvent) selectedItems[0]
                        .getData();

                if (loggingEvent.getThrowableInformation() != null) {

                    /* View stacktrace */
                    MenuItem viewTraceItem = new MenuItem(menu, SWT.NONE);
                    viewTraceItem.setText("View stacktrace");
                    viewTraceItem.setImage(PaintUtil.iconException);
                    viewTraceItem.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            TableItem selectedItem = table.getSelection()[0];
                            if (WidgetUtil.isset(selectedItem)) {
                                LoggingEvent loggingEvent = (LoggingEvent) selectedItem
                                        .getData();
                                if (loggingEvent != null) {
                                    Throwable logThrowable = loggingEvent
                                            .getThrowableInformation()
                                            .getThrowable();
                                    if (logThrowable != null) {
                                        MessageBoxUtil
                                                .showMessage(
                                                        GUI.shell,
                                                        SWT.ICON_INFORMATION
                                                                | SWT.OK,
                                                        "Exception",
                                                        FileUtil
                                                                .getStackTrace(logThrowable));
                                    }
                                }
                            }
                        }
                    });
                }

                /* Copy to clipboard menu item */
                MenuItem copyItem = new MenuItem(menu, SWT.NONE);
                copyItem.setText("Copy to Clipboard");
                copyItem.setImage(PaintUtil.iconCopy);
                copyItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        TableItem selectedItem = table.getSelection()[0];
                        if (WidgetUtil.isset(selectedItem)) {
                            LoggingEvent loggingEvent = (LoggingEvent) selectedItem
                                    .getData();
                            if (loggingEvent != null) {
                                ThrowableInformation logInfo = loggingEvent
                                        .getThrowableInformation();
                                if (logInfo != null) {
                                    Throwable logInfoThrowable = logInfo
                                            .getThrowable();
                                    if (logInfoThrowable != null) {
                                        String logMessageString = FileUtil
                                                .getStackTrace(logInfoThrowable);
                                        if (StringUtil.isset(logMessageString)) {
                                            eventManager
                                                    .actionCopyToClipboard(logMessageString);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

            }
        });

        tabItem.setControl(table);

        /* Attach the log appender to the new table */
        new LogAppender(table);
    }

    public void setTabItem(CTabItem tabItem) {
        this.tabItem = tabItem;

        this.tabItem.setText("Log");
        this.tabItem.setImage(PaintUtil.iconLog);
        this.tabItem.setToolTipText("Progress log");

        if (WidgetUtil.isset(table)) {
            this.tabItem.setControl(table);
        }
    }
}
