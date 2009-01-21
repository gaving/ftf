package net.brokentrain.ftf.ui.gui.tabs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import net.brokentrain.ftf.core.downloader.Download;
import net.brokentrain.ftf.core.downloader.DownloadManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TransferTab extends Tab implements Observer {

    private EventManager eventManager;

    private DownloadManager downloadManager;

    private CTabFolder tabFolder;

    private Table table;

    private HashMap<Download, TableItem> downloadList;

    private HashMap<TableItem, ProgressBar> progressList;

    public TransferTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager) {
        super(tabItem, tabManager);

        tabFolder = tabItem.getParent();
        eventManager = fetcherGui.getEventManager();
        downloadManager = DownloadManager.getDownloadManager();
        downloadList = new HashMap<Download, TableItem>();
        progressList = new HashMap<TableItem, ProgressBar>();

        initComponents();
    }

    public void addDownload(String url) {

        if (!WidgetUtil.isset(tabItem)) {
            return;
        }

        URL downloadURL;

        try {

            /* Attempt to create URL from the string */
            downloadURL = new URL(url);
        } catch (MalformedURLException mue) {
            GUI.log.error("Given an invalid URL to download!");
            return;
        }

        /* Start the new download */
        Download download = new Download(downloadURL);

        /* Observe for updates */
        download.addObserver(this);

        /* Create a new item in the transfer list */
        Table table = (Table) tabItem.getControl();

        TableItem item = new TableItem(table, SWT.NONE);

        /* Set the download information */
        item.setText(0, download.getFileName(downloadURL, true));
        item.setText(3, download.getStatus());

        /* Add a progress bar */
        ProgressBar progressBar = new ProgressBar(table, SWT.SMOOTH);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        /* Add the progressbar to a particular cell */
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = true;
        editor.grabVertical = true;
        editor.setEditor(progressBar, item, 2);

        table.getColumn(2).pack();

        /* Store widgets for later updates */
        downloadList.put(download, item);
        progressList.put(item, progressBar);
    }

    public void initComponents() {

        /* Set transfer tab header */
        tabItem.setText("Transfer");
        tabItem.setImage(PaintUtil.iconTransfer);
        tabItem.setToolTipText("View current or completed transfers");

        /* Create table for transfer storage */
        table = new Table(tabFolder, SWT.BORDER | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        /* Filename column */
        TableColumn tcFilename = new TableColumn(table, SWT.LEFT);
        tcFilename.setText("Filename");
        tcFilename.setWidth(100);

        /* Size column */
        TableColumn tcSize = new TableColumn(table, SWT.NULL);
        tcSize.setText("Size");
        tcSize.setWidth(70);

        /* Progress Column */
        TableColumn tcProgress = new TableColumn(table, SWT.NULL);
        tcProgress.setText("% Complete");
        tcProgress.setWidth(100);

        /* Status of the download */
        TableColumn tcStatus = new TableColumn(table, SWT.NULL);
        tcStatus.setText("Status");
        tcStatus.setWidth(200);

        /* Squeeze the columns */
        table.pack();

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

                MenuItem openItem = new MenuItem(menu, SWT.NONE);
                openItem.setText("Open");
                openItem.setImage(PaintUtil.iconOpen);
                openItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        TableItem[] selectedItems = table.getSelection();
                        if (selectedItems.length > 0) {
                            for (TableItem selectedItem : selectedItems) {
                                if (StringUtil.isset(selectedItem.getText(2))) {
                                    eventManager.actionOpenFile(selectedItem
                                            .getText(2));
                                }
                            }
                        }
                    }
                });

                MenuItem openDirectoryItem = new MenuItem(menu, SWT.NONE);
                openDirectoryItem.setText("Open Directory");
                openDirectoryItem.setImage(PaintUtil.iconOpen);
                openDirectoryItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        eventManager.actionOpenDownloadDirectory();
                    }
                });
            }
        });

        /* Launch the URL in the table on double click */
        table.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                TableItem item = table.getItem(new Point(event.x, event.y));
                if ((item != null) && StringUtil.isset(item.getText(0))) {
                    eventManager.actionOpenFile(item.getText(0));
                }
            }
        });

        /* Give control to the table */
        tabItem.setControl(table);

        downloadManager.setDownloadDir(SettingsRegistry.downloadDirectoryPath);
    }

    public void setTabItem(CTabItem tabItem) {
        this.tabItem = tabItem;

        /* Set transfer tab header */
        tabItem.setText("Transfer");
        tabItem.setImage(PaintUtil.iconTransfer);
        tabItem.setToolTipText("View current or completed transfers");

        if (WidgetUtil.isset(table)) {
            this.tabItem.setControl(table);
        }
    }

    public void update(final Observable o, final Object arg) {

        /* Make sure the display is reachable */
        if ((GUI.isAlive()) && (o instanceof Download)) {

            final Download download = (Download) o;

            /* Get the table item for this particular download */
            final TableItem tableItem = downloadList.get(o);

            /* Get the progress progressBar for this table tableItem */
            final ProgressBar progressBar = progressList.get(tableItem);

            /* Quickly update the GUI with the new value */
            GUI.display.asyncExec(new Runnable() {
                public void run() {

                    if ((!WidgetUtil.isset(progressBar))
                            || (!WidgetUtil.isset(tableItem))) {
                        return;
                    }

                    /* Update the selection */
                    progressBar.setSelection(((Float) download.getProgress())
                            .intValue());

                    /* Set the amount completed */
                    tableItem.setText(1, Math.round(download.getDownloaded())
                            + "/" + Math.round(download.getSize()) + "KB");

                    /* Set the status */
                    tableItem.setText(3, download.getStatus());
                }
            });
        }
    }
}
