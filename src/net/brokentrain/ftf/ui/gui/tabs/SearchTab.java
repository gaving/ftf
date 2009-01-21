package net.brokentrain.ftf.ui.gui.tabs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Observer;

import net.brokentrain.ftf.Fetcher;
import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.ResultSet;
import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.core.data.FileData;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.components.MenuManager;
import net.brokentrain.ftf.ui.gui.components.Statusbar;
import net.brokentrain.ftf.ui.gui.model.QueryResult;
import net.brokentrain.ftf.ui.gui.model.SavedQuery;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.tabs.components.MessageArea;
import net.brokentrain.ftf.ui.gui.tabs.components.MetadataPane;
import net.brokentrain.ftf.ui.gui.tabs.components.SearchArea;
import net.brokentrain.ftf.ui.gui.tabs.components.ServiceTracker;
import net.brokentrain.ftf.ui.gui.tabs.components.ToolTipHandler;
import net.brokentrain.ftf.ui.gui.tabs.components.ToolTipHelpTextHandler;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.Formatter;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

abstract public class SearchTab extends Tab implements Observer {

    public static String DEFAULT_LABEL = "(Untitled)";

    public static int TAB_TYPE = 0;

    public static final int TAB_TYPE_QUERY = 1;

    public static final int TAB_TYPE_PROCESSOR = 2;

    public static int[] QUERY_SASH_WEIGHTS = new int[] { 80, 20 };

    private EventManager eventManager;

    private GUI fetcherGui;

    protected Fetcher ftf;

    private MessageArea messageArea;

    private MetadataPane metadataPane;

    private File file;

    protected String query;

    private Composite queryArea;

    protected ArrayList<QueryResult> results;

    private SashForm sashForm;

    private SearchArea searchArea;

    private ServiceManager serviceManager;

    protected ServiceTracker serviceTracker;

    private boolean showMessageArea;

    private Statusbar statusBar;

    private CTabFolder tabFolder;

    private ToolTipHandler tooltipHandler;

    protected Tree tree;

    public SearchTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager,
            boolean showMessageArea) {
        super(tabItem, tabManager);

        this.fetcherGui = fetcherGui;
        this.showMessageArea = showMessageArea;

        tabFolder = tabItem.getParent();
        eventManager = fetcherGui.getEventManager();
        statusBar = fetcherGui.getStatusbar();

        results = new ArrayList<QueryResult>();

        initComponents();
    }

    public void confirmArrival(String name) {
        statusBar.setText(name + " finished!");
    }

    abstract void createTree();

    abstract void decorateItem(TreeItem item, ResultSet resultSet);

    public void fetch() {

        /* Inform the source tracker of the sources involved */
        serviceTracker.setSourceRegistry(serviceManager.getSelectedServices());

        /* Create new Fetcher */
        ftf = new Fetcher();

        /* Give ftf a service manager that has been configured elsewhere */
        ftf.setServiceManager(serviceManager);

        /* Assign queries */
        ftf.setQuery(query);

        /* Tell results to come here */
        ftf.addObserver(this);

        if (this instanceof ProcessingTab) {

            SettingsManager settingsManager = SettingsManager
                    .getSettingsManager();
            settingsManager.setInvestigate(false);

            ftf.setProcessingAbstract(true);
        }

        /* Spawn new thread */
        Thread fetchingThread = new Thread() {
            @Override
            public void run() {

                /* Start fetching the results */
                ftf.fetch();
            }
        };

        fetchingThread.start();
    }

    public File getFile() {
        return file;
    }

    public MetadataPane getMetadataPane() {
        return metadataPane;
    }

    public String getQuery() {
        return query;
    }

    public Composite getQueryArea() {
        return queryArea;
    }

    protected LinkedHashMap<String, String> getResultMetadata(DataStore data) {
        LinkedHashMap<String, String> metadata = new LinkedHashMap<String, String>();

        /* Set filename */
        if (StringUtil.isset(data.getFilename())) {
            String file = data.getFilename();
            file = StringUtil.ellipsize(file, 20);
            metadata.put("Filename", file);
        }

        /* Size */
        if ((StringUtil.isset(data.getSize())) && (data.getSize() != "-1")) {
            long fileSize = Long.valueOf(data.getSize());
            metadata.put("Size", Formatter.formatFilesize(fileSize));
        }

        /* Content Type */
        if (data.getContentType() != null) {
            metadata.put("Type", data.getContentType().toString());
        }

        /* Last modified date */
        if (data.getLastModified() != null) {
            metadata.put("Last Modified", StringUtil.formatDate(data
                    .getLastModified()));
        }

        /* Encoding */
        if ((data instanceof URLData)
                && (StringUtil.isset(((URLData) data).getEncoding()))) {
            String dataEncoding = ((URLData) data).getEncoding();
            metadata.put("Encoding", dataEncoding);
        }

        return metadata;
    }

    public SavedQuery getSavedQuery() {

        SavedQuery savedQuery = new SavedQuery(results, query,
                SearchTab.TAB_TYPE);

        return savedQuery;
    }

    public TreeItemData getSelectedTreeItemData() {
        TreeItem selectedTreeItem[] = tree.getSelection();
        if (selectedTreeItem.length > 0) {
            return (TreeItemData) selectedTreeItem[0].getData();
        }
        return null;
    }

    public TreeItem[] getSelectedTreeItems() {
        TreeItem[] selectedTreeItems = tree.getSelection();
        if (selectedTreeItems.length > 0) {
            return selectedTreeItems;
        }
        return null;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public int getServiceRowIndex(String serviceName) {
        for (TreeItem item : tree.getItems()) {
            TreeItemData treeItemData = (TreeItemData) item.getData();
            if (treeItemData.isService()
                    && treeItemData.getName().equals(serviceName)) {
                return tree.indexOf(item);
            }
        }
        return -1;
    }

    @Override
    public String getTitle() {
        return query;
    }

    public Tree getTree() {
        return tree;
    }

    public String getTreePath(TreeItem selectedTree) {
        StringBuffer treePath = new StringBuffer("");
        ArrayList<String> treePathVector = new ArrayList<String>();

        TreeItemData data = (TreeItemData) selectedTree.getData();

        if (data.isResult() || data.isService()) {
            treePathVector.add(data.getName());
        }

        while (selectedTree.getParentItem() != null) {
            TreeItemData parentData = (TreeItemData) selectedTree
                    .getParentItem().getData();
            treePathVector.add(parentData.getName());
            selectedTree = selectedTree.getParentItem();
        }

        if (treePathVector.size() > 0) {
            treePath = new StringBuffer(treePathVector.get(treePathVector
                    .size() - 1));
            for (int a = treePathVector.size() - 2; a >= 0; a--) {
                treePath.append("=;=" + treePathVector.get(a));
            }
        }

        return treePath.toString();
    }

    public boolean hasResults() {
        return (results.size() > 0);
    }

    public void hideMessageArea() {
        messageArea.hide();
    }

    public void initComponents() {

        /* Create a source tracker */
        serviceTracker = new ServiceTracker(this);

        query = "Untitled";
        tabItem.setText(SearchTab.DEFAULT_LABEL);
        updateTitle();

        /* Sash to split results and potential metadata pane */
        sashForm = new SashForm(tabFolder, SWT.VERTICAL);

        /* Main result area */
        queryArea = new Composite(sashForm, SWT.NONE);
        queryArea.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 1, 0, true));

        /* Create the main window */
        searchArea = new SearchArea(queryArea, fetcherGui, tabFolder,
                eventManager, this);

        /* Handle the tooltips */
        tooltipHandler = new ToolTipHandler(GUI.shell);

        /* Create a new message area */
        messageArea = new MessageArea(queryArea, tabItem,
                MessageArea.MessageType.SEARCHING);

        createTree();

        /* Activate the tooltips if enabled */
        if (SettingsRegistry.showTooltip) {
            tooltipHandler.activateHoverHelp(tree);
        }

        /* Create a new meta data pane */
        metadataPane = new MetadataPane(sashForm, tabItem, this);

        /* Set our weightings between the metadata pane and main area */
        sashForm.setWeights(SearchTab.QUERY_SASH_WEIGHTS);

        /* Set the maximised control to the result list by default */
        sashForm.setMaximizedControl(queryArea);

        /* Make area visible */
        tabItem.setControl(sashForm);

        if (showMessageArea) {

            /* Show the message area */
            messageArea.show();
        }
    }

    public TreeItem insertChildItem(String name, TreeItem parent, Result result) {

        /* Create a new treeItem for the result */
        TreeItem treeItem = new TreeItem(parent, SWT.NONE);

        treeItem.setText(result.getURI().toString());
        treeItem.setData(TreeItemData.createResult(name, result));

        /* Set icon and tooltip */
        decorateItem(treeItem, null);

        return treeItem;
    }

    public TreeItem insertParentItem(String name, TreeItem parent,
            Result result, ResultSet resultSet) {

        /* Create a new treeItem for the result */

        TreeItem treeItem;

        if (parent == null) {
            treeItem = new TreeItem(tree, SWT.NONE);
        } else {
            treeItem = new TreeItem(parent, SWT.NONE);
        }

        Resource resource = result.getResource();

        /* Extract the resourceData from the resultSet */
        DataStore resourceData = resource.getData();

        /* Handle URL resourceData */
        if (resourceData instanceof URLData) {

            /* Extract any article information we need at this point */
            Article resultArticle = resource.getArticle();
            if (resultArticle != null) {

                /* Get the article title */
                String resultTitle = resultArticle.getArticleTitle();

                /* Set the title or indicate that it has none */
                if (StringUtil.isset(resultTitle)) {
                    treeItem.setText(0, resultTitle);
                } else {
                    treeItem.setText("(no data)");
                    treeItem.setForeground(ColourUtil.gray);
                }
            } else {

                /* No article at all means no title! */
                treeItem.setText("(no data)");
                treeItem.setForeground(ColourUtil.gray);
            }

            /* No smart way to do the file articles just now */
        } else if (resourceData instanceof FileData) {

            /* Handle file resourceData */
            treeItem.setText(resourceData.getFilename());
        }

        treeItem.setData(TreeItemData.createResult(name, result));

        /* Set icon and tooltip */
        decorateItem(treeItem, resultSet);

        return treeItem;
    }

    public void onMouseDoubleClick(Event event) {
        TreeItem treeItem = tree.getItem(new Point(event.x, event.y));
        if (treeItem != null) {
            eventManager.actionHandleItem(treeItem);
        }
    }

    public void onSelection() {

        if ((tree.getSelectionCount() > 0) && SettingsRegistry.showMetadataPane) {
            metadataPane.setPane(tree.getSelection()[0]);
        }

        selectionChanged();
    }

    public void performDeletion() {

        TreeItemData data = null;
        if (tree.getSelection().length > 0) {
            data = (TreeItemData) tree.getSelection()[0].getData();
        }

        if (data != null) {

            if (data.isService()) {
                eventManager.actionDeleteService();
            } else if (data.isResult()) {
                eventManager.actionDeleteResult();
            }
        }

        selectionChanged();
    }

    abstract public void populateTree(Dispatcher service);

    abstract public void populateTree(Object loadedResults);

    public void selectionChanged() {

        /* Get the current tree selection */
        TreeItem selection[] = tree.getSelection();

        if (selection.length > 0) {

            TreeItem selectedTreeItem = selection[0];
            TreeItemData data = (TreeItemData) selectedTreeItem.getData();

            if (data != null) {

                if (data.isResult()) {
                    MenuManager.notifyState(MenuManager.TREE_SELECTION_RESULT);
                } else if (data.isService()) {
                    MenuManager.notifyState(MenuManager.TREE_SELECTION_SERVICE);
                }
            }
        } else {
            MenuManager.notifyState(MenuManager.TREE_SELECTION_EMPTY);
        }
    }

    public void setBusy(boolean busy) {

        /* Inform the search area */
        searchArea.setBusy(busy);

        if (busy) {

            /* Indicate that we are searching */
            statusBar.setText("Searching...");

            /* Start progress bar */
            statusBar.showProgressBar(true);

        } else {

            /* Indicate that we are idle */
            statusBar.setText("Idle.");

            /* Stop progress bar */
            statusBar.showProgressBar(false);
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setImage(Image icon) {

        if (tabItem != null) {
            tabItem.setImage(icon);
        }
    }

    protected void setMetadata(Item item, LinkedHashMap<String, String> data) {
        StringBuffer tooltipBuffer = new StringBuffer("");

        /* Construct the popup using available metadata */
        for (String key : data.keySet()) {

            /* Get the metadata value for this key and add it */
            String value = data.get(key);

            /* Ensure the value isn't just empty or null */
            if (StringUtil.isset(value)) {

                /* Truncate long values which would skew the tooltip */
                String truncatedValue = StringUtil.ellipsize(value, 40);

                /* Append to the tooltip */
                tooltipBuffer.append(String.format("%s: %s\n", key,
                        truncatedValue));
            }
        }

        if (StringUtil.isset(tooltipBuffer.toString())) {

            /* Set the tooltip text if it is not empty */
            item.setData("TIP_TEXT", tooltipBuffer.toString().trim());
            item.setData("TIP_HELPTEXTHANDLER", new ToolTipHelpTextHandler() {
                public String getHelpText(Widget widget) {
                    Item item = (Item) widget;
                    return item.getText();
                }
            });
        }
    }

    public void setQuery(String query) {
        this.query = query;

        tabItem.setText(query);
        searchArea.setQueryInputText(query);
        updateTitle();
    }

    public void setServiceRegistry(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void showEmptyResults(Dispatcher source) {

        /* Populate the query tab with the new results */
        showNoResults();

        statusBar.showProgressBar(false);

        searchArea.setFetchButtonEnabled(true);
    }

    private void showMessageArea() {
        messageArea.show();
    }

    public void showNoResults() {

        /* Hide the message are */
        hideMessageArea();

        /* Change the message type */
        messageArea.setMessageState(MessageArea.MessageType.NO_RESULTS);

        /* Show the message area */
        showMessageArea();
    }

    public void showResults(Dispatcher source) {

        /* Populate the query tab with the new results */
        populateTree(source);

        /* Show the progress bar */
        statusBar.showProgressBar(true);

        searchArea.setFetchButtonEnabled(true);
    }

    public void stopFetch() {

        if ((ftf != null) && (ftf.isFetching())) {

            /* Stop the fetch if there if currently fetching */
            ftf.stop();
        }

    }

    public void update(final Observable o, final Object arg) {

        /* Make sure the display is reachable */
        if (GUI.isAlive() && (arg instanceof Dispatcher)) {

            /* Don't block our GUI with incoming events! */
            GUI.display.asyncExec(new Runnable() {
                public void run() {

                    Dispatcher dispatcher = (Dispatcher) arg;

                    /*
                     * If the dispatcher is still active it's just a status
                     * update
                     */
                    eventManager.actionUpdateServiceStatus(dispatcher);

                    /* Incoming dispatcher results! */
                    if (dispatcher.isDead()) {

                        /* Let the tracker handle the dispatcher */
                        serviceTracker.handleService(dispatcher);
                    }
                }
            });
        }
    }

    public void updateState() {
        searchArea.setFetchButtonEnabled(serviceManager.hasSelectedServices());
    }

    public void updateTitle() {
        if (GUI.isAlive()) {

            String shellTitle = String.format("%s - %s", StringUtil.ellipsize(
                    query, 15), WidgetUtil.getShellTitle());
            GUI.shell.setText(shellTitle);
        }
    }

}
