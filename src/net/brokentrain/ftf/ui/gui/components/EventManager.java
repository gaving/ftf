package net.brokentrain.ftf.ui.gui.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.ResultSet;
import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.services.WebSearchService;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.PMIDArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.dialog.AboutDialog;
import net.brokentrain.ftf.ui.gui.dialog.PreferencesDialog;
import net.brokentrain.ftf.ui.gui.dialog.ResultDialog;
import net.brokentrain.ftf.ui.gui.dialog.ServiceDialog;
import net.brokentrain.ftf.ui.gui.model.SavedQuery;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.svm.Model;
import net.brokentrain.ftf.ui.gui.tabs.BrowserTab;
import net.brokentrain.ftf.ui.gui.tabs.LogTab;
import net.brokentrain.ftf.ui.gui.tabs.ProcessingTab;
import net.brokentrain.ftf.ui.gui.tabs.SearchTab;
import net.brokentrain.ftf.ui.gui.tabs.StatusTab;
import net.brokentrain.ftf.ui.gui.tabs.TabManager;
import net.brokentrain.ftf.ui.gui.tabs.TransferTab;
import net.brokentrain.ftf.ui.gui.tabs.components.MetadataPane;
import net.brokentrain.ftf.ui.gui.util.BrowserUtil;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.MessageBoxUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.RegExUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.URLUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class EventManager {

    /* Cut action */
    static final int EDIT_ACTION_CUT = 0;

    /* Copy action */
    static final int EDIT_ACTION_COPY = 1;

    /* Paste action */
    static final int EDIT_ACTION_PASTE = 2;

    /* Delete action */
    static final int EDIT_ACTION_DELETE = 3;

    /* Select all action */
    static final int EDIT_ACTION_SELECTALL = 4;

    /* Text mode */
    public static final int SEARCH_TYPE_TEXT = 0;

    /* Local mode */
    public static final int SEARCH_TYPE_LOCAL = 1;

    /* DOI mode */
    public static final int SEARCH_TYPE_DOI = 2;

    /* PUBMED mode */
    public static final int SEARCH_TYPE_PROCESSING = 3;

    /* ARXIV mode */
    public static final int SEARCH_TYPE_ARXIV = 4;

    private Clipboard clipboard;

    private Display display;

    private GUI fetcherGui;

    private Shell shell;

    private Statusbar statusBar;

    private TabManager tabManager;

    private MainToolbar toolBar;

    private MainMenu mainMenu;

    public EventManager(Display display, Shell shell, GUI fetcherGui) {
        this.display = display;
        this.shell = shell;
        this.fetcherGui = fetcherGui;

        /* Create a new clipboard */
        clipboard = new Clipboard(display);
    }

    public void actionAdd() {

        /* Open dialog */
        final String filename = FileUtil.getFilePath(new String[] { "*.frs",
                "*.*" }, null, SWT.OPEN, null, "Select results file");

        actionOpen(filename, false);
    }

    public void actionAddDownload(String url) {

        /* Get the transfer tab */
        TransferTab transferTab = tabManager.getTransferTab();

        if ((transferTab == null) || (transferTab.isDisposed())) {

            /* Open the transfer tab if needed */
            tabManager.createTransferTab();
            transferTab = tabManager.getTransferTab();
        }

        if (RegExUtil.isValidURL(url)) {

            /* Add the new download */
            transferTab.addDownload(url);

            /* Focus the transfer tab */
            tabManager.setSelection(transferTab);
        }
    }

    public void actionCloseAll() {
        tabManager.closeAll();
    }

    public void actionCloseCurrent() {
        tabManager.closeCurrent();
    }

    public void actionCopyBibtex(Result result) {

        /* Extract the result data out of the result */
        Article resultArticle = result.getResource().getArticle();

        /* Get the article of the URL data */
        if (resultArticle != null) {

            /* Get the bibtex of the article and copy to clipboard */
            clipboard.setContents(new Object[] { resultArticle.toBibtex() },
                    new Transfer[] { TextTransfer.getInstance() });
        }
    }

    public void actionCopyToClipboard(String text) {

        /* Copy the text straight to the clipboard */
        clipboard.setContents(new Object[] { text },
                new Transfer[] { TextTransfer.getInstance() });
    }

    public void actionDeleteResult() {

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {
            TreeItem[] selectedTreeItems = searchTab.getSelectedTreeItems();

            /* Dispose of each of the selected items */
            for (TreeItem selectedTreeItem : selectedTreeItems) {
                if (WidgetUtil.isset(selectedTreeItem)) {
                    selectedTreeItem.dispose();
                }
            }
        }
    }

    public void actionDeleteService() {

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {
            TreeItem[] selectedTreeItems = searchTab.getSelectedTreeItems();

            /* Dispose of each of the selected items */
            for (TreeItem selectedTreeItem : selectedTreeItems) {
                if (WidgetUtil.isset(selectedTreeItem)) {
                    selectedTreeItem.dispose();
                }
            }
        }
    }

    public void actionDeselectAllServices() {

        final SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            ServiceManager serviceManager = searchTab.getServiceManager();

            /* Disable all sources */
            serviceManager.deselectAll();
        }
    }

    public void actionDoFetch(SearchTab searchTab, String query) {

        /* Get the status tab */
        StatusTab statusTab = tabManager.getStatusTab();

        if (statusTab != null) {

            /* Clear status table */
            statusTab.clear();
        }

        /* Get the log tab */
        LogTab logTab = tabManager.getLogTab();

        if (logTab != null) {

            /* Clear log table */
            logTab.clear();
        }

        searchTab.setQuery(query);

        searchTab.fetch();

        /* Update the main interface */
        searchTab.setBusy(true);
    }

    public void actionExit() {
        fetcherGui.onClose(new Event(), true);

        if (GUI.isClosing) {
            display.dispose();
        }
    }

    public void actionExportAsIDList() {
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if ((searchTab != null) && (searchTab instanceof ProcessingTab)) {

            GUI.log.debug("Exporting as ID list");

            ProcessingTab processingTab = (ProcessingTab) searchTab;
            ArrayList<ResultSet> results = processingTab.getResults();

            if ((results != null) && (!results.isEmpty())) {

                /* Get the date suitable for a filename */
                String currentDate = StringUtil.dateToFileName(StringUtil
                        .formatDate());

                /* Construct new filename */
                String fileName = "exported_results_" + currentDate + ".txt";
                String selectedFileName = FileUtil.getSavePath(fileName, "txt",
                        "Export IDs as");

                /* User has entered a filename */
                if (StringUtil.isset(selectedFileName)) {

                    try {
                        FileWriter fileStream = new FileWriter(selectedFileName);
                        BufferedWriter output = new BufferedWriter(fileStream);

                        for (ResultSet resultSet : results) {

                            Result result = resultSet.getOriginalLink();
                            Resource resource = result.getResource();
                            Article article = resource.getArticle();

                            if (article instanceof PMIDArticle) {

                                String pmid = ((PMIDArticle) article).getPMID();

                                if (StringUtil.isset(pmid)) {
                                    output.write(pmid);
                                    output.newLine();
                                }
                            }
                        }

                        output.close();
                    } catch (Exception e) {
                        GUI.log.error("Could not open file!", e);
                    }
                }
            } else {
                MessageBoxUtil.showError(GUI.shell, "No results to export!");
            }
        }
    }

    public void actionGotoNextTab() {
        tabManager.gotoNextTab();
    }

    public void actionGotoPreviousTab() {
        tabManager.gotoPreviousTab();
    }

    public void actionHandleItem(TreeItem item) {

        /* Extract the data from the treeitem */
        TreeItemData data = (TreeItemData) item.getData();

        /* Exit immediately if we have no information */
        if (data == null) {
            return;
        }

        if (data.isResult()) {

            Result result = data.getResult();

            /* Extract the URL from the result */
            String uri = result.getURI().toString();

            /* Ensure the URL is well formed */
            if (StringUtil.isset(uri) && RegExUtil.isValidURL(uri)) {

                GUI.log.info("Handling: " + uri);

                Resource resource = result.getResource();

                /* Retrieve the content type */
                DataStore.ContentType contentType = resource.getData()
                        .getContentType();

                if (contentType != null) {

                    /* Handle based on file type */
                    switch (contentType) {

                    /* Text or html file */
                    case TEXT:
                    case HTML:
                        BrowserUtil.openLink(uri);
                        break;

                    /* PDF file */
                    case PDF:
                        actionAddDownload(uri);
                        break;

                    /* Default to launching the browser anyway */
                    default:
                        BrowserUtil.openLink(uri);
                    }
                } else {
                    BrowserUtil.openLink(uri);
                }
            }
        } else if (data.isService()) {
            SearchService service = data.getSearchService();

            if (service != null) {

                /* Launch query string in browser if web search service */
                if (service instanceof WebSearchService) {

                    /* Extract the URL from the service */
                    String uri = ((WebSearchService) service).getQueryString();

                    /* Ensure the URL is well formed */
                    if (StringUtil.isset(uri) && RegExUtil.isValidURL(uri)) {
                        GUI.log.info("Handling: " + uri);
                        BrowserUtil.openLink(uri);
                    }
                } else {

                    /* Just open properties dialog */
                    new ServiceDialog(service, shell, "Service Properties")
                            .open();
                }
            } else {
                MessageBoxUtil
                        .showMessage(shell, SWT.OK | SWT.ICON_INFORMATION,
                                "Information",
                                "Service information is not available from items that have been imported!");
            }
        }
    }

    public void actionLaunchURL(String url) {

        /* Open link with external browser */
        BrowserUtil.openLink(url);
    }

    public SearchTab actionNewTab(int type) {

        /* Clone existing service manager with default services */
        ServiceManager serviceManager = new ServiceManager();

        SearchTab searchTab = null;

        switch (type) {

        /* Standard query options */
        case SEARCH_TYPE_ARXIV:
        case SEARCH_TYPE_DOI:
        case SEARCH_TYPE_LOCAL:
        case SEARCH_TYPE_TEXT:
            searchTab = tabManager.createFullTextTab();
            break;

        /* Abstract processing tabs */
        case SEARCH_TYPE_PROCESSING:
            searchTab = tabManager.createProcessingTab();
            break;
        }

        switch (type) {

        /* Enable the text services */
        case SEARCH_TYPE_TEXT:
            serviceManager.enableAll();
            serviceManager.selectAll();
            serviceManager.getServiceEntry("DOILookup").setEnabled(false);
            serviceManager.getServiceEntry("DOILookup").setSelected(false);
            serviceManager.getServiceEntry("ArXivLookup").setEnabled(false);
            serviceManager.getServiceEntry("ArXivLookup").setSelected(false);
            searchTab.setImage(PaintUtil.iconNewText);
            break;

        /* Enable the local services */
        case SEARCH_TYPE_LOCAL:
            serviceManager.deselectAll();
            serviceManager.disableAll();
            serviceManager.getServiceEntry("Terrier").setEnabled(true);
            serviceManager.getServiceEntry("Terrier").setSelected(true);
            serviceManager.getServiceEntry("GoogleDesktopSearch").setEnabled(
                    true);
            serviceManager.getServiceEntry("GoogleDesktopSearch").setSelected(
                    true);
            searchTab.setImage(PaintUtil.iconNewLocal);
            break;

        /* Enable the DOI lookup service */
        case SEARCH_TYPE_DOI:
            serviceManager.deselectAll();
            serviceManager.disableAll();
            serviceManager.getServiceEntry("DOILookup").setEnabled(true);
            serviceManager.getServiceEntry("DOILookup").setSelected(true);
            searchTab.setImage(PaintUtil.iconNewDOI);
            break;

        /* Enable the PUBMED services */
        case SEARCH_TYPE_PROCESSING:
            serviceManager.deselectAll();
            serviceManager.disableAll();
            serviceManager.getServiceEntry("PubMed").setEnabled(true);
            serviceManager.getServiceEntry("PubMed").setSelected(true);
            serviceManager.getServiceEntry("PubMedCentral").setEnabled(true);
            serviceManager.getServiceEntry("WebOfKnowledge").setEnabled(true);
            searchTab.setImage(PaintUtil.iconNewProcessing);
            break;

        /* Enable the Arxiv lookup service */
        case SEARCH_TYPE_ARXIV:
            serviceManager.deselectAll();
            serviceManager.disableAll();
            serviceManager.getServiceEntry("ArXivLookup").setEnabled(true);
            serviceManager.getServiceEntry("ArXivLookup").setSelected(true);
            searchTab.setImage(PaintUtil.iconNewArxiv);
            break;
        }

        searchTab.setServiceRegistry(serviceManager);

        /* Display processing buttons or not */
        tabManager.updateTabFolderState();
        tabManager.updateInterfaceState();
        mainMenu.buildMenuBar();

        return searchTab;
    }

    public void actionNext() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        if ((searchTab != null) && (searchTab instanceof ProcessingTab)) {

            ProcessingTab processingTab = (ProcessingTab) searchTab;

            /* Train current ticked examples */
            processingTab.next();
        }
    }

    public void actionOpen() {

        /* Open dialog */
        final String filename = FileUtil.getFilePath(new String[] { "*.frs",
                "*.*" }, null, SWT.OPEN, null, "Select results file");

        actionOpen(filename, true);
    }

    public void actionOpen(final String filename, final boolean newTab) {

        if (!StringUtil.isset(filename)) {
            return;
        }

        final File file = new File(filename);
        final String absoluteFile = file.getAbsolutePath();

        /* Show progress bar and display message on status line */
        statusBar.showProgressBar(true);
        statusBar.setText(String.format("Loading %s...", absoluteFile));

        Thread fetchingThread = new Thread() {
            @Override
            public void run() {
                /* Save the information to file */
                final Object savedQuery = FileUtil.readObject(file);

                GUI.display.syncExec(new Runnable() {
                    public void run() {
                        if ((savedQuery != null)
                                && (savedQuery instanceof SavedQuery)) {

                            /* Hide the progress bar on the status line */
                            statusBar.showProgressBar(false);

                            /* Create a new tab */
                            SearchTab searchTab = null;

                            if (newTab) {
                                int tabType = ((SavedQuery) savedQuery)
                                        .getTabType();

                                switch (tabType) {

                                case SearchTab.TAB_TYPE_QUERY:
                                    searchTab = actionNewTab(EventManager.SEARCH_TYPE_TEXT);
                                    break;
                                case SearchTab.TAB_TYPE_PROCESSOR:
                                    searchTab = actionNewTab(EventManager.SEARCH_TYPE_PROCESSING);
                                    break;

                                default:
                                    searchTab = actionNewTab(EventManager.SEARCH_TYPE_TEXT);
                                }
                            } else {
                                searchTab = tabManager.getActiveSearchTab();

                                if (searchTab == null) {
                                    return;
                                }
                            }

                            searchTab.setFile(file);

                            /* Populate the tab */
                            searchTab.populateTree(savedQuery);

                            /* Update status bar */
                            statusBar.setText("Loaded " + absoluteFile);

                            /* Add this to our open history */
                            if (SettingsRegistry.openHistory != null) {

                                if (!SettingsRegistry.openHistory
                                        .contains(absoluteFile)) {

                                    SettingsRegistry.openHistory
                                            .add(absoluteFile);
                                }
                            }
                            tabManager.updateInterfaceState();
                        } else {

                            /* Hide the progress bar on the status line */
                            statusBar.showProgressBar(false);

                            /* Show error message */
                            MessageBoxUtil
                                    .showError(
                                            GUI.shell,
                                            "Could not load file!\nThe file may be from an older version of FTF or has been corrupted.");

                            /* Update status bar */
                            statusBar.setText("Unable to load " + absoluteFile
                                    + "!");
                        }
                    }

                });
            }
        };

        fetchingThread.start();
    }

    public void actionOpenAbout() {
        new AboutDialog(shell, "About FTF").open();
    }

    public void actionOpenDownloadDirectory() {

        /* Get the download directory */
        String downloadDir = SettingsRegistry.downloadDirectoryPath;

        if (StringUtil.isset(downloadDir)) {
            Program.launch(downloadDir);
        } else {
            Program.launch(".");
        }
    }

    public void actionOpenFAQ() {

        /* Open FTF FAQ */
        BrowserUtil.openLink(URLUtil.FTF_FAQ);
    }

    public void actionOpenFile(String filename) {

        /* Get the download directory */
        String downloadDir = SettingsRegistry.downloadDirectoryPath;

        if (StringUtil.isset(downloadDir)) {

            /* FIXME: Some sort of path.join operator here */
            Program.launch(downloadDir + "/" + filename);
        } else {
            Program.launch(filename);
        }
    }

    public void actionOpenHomepage() {

        /* Open FTF homepage */
        BrowserUtil.openLink(URLUtil.FTF_WEBPAGE);
    }

    public void actionOpenInInternalBrowser(String location, String query) {

        /* Get the browser tab */
        BrowserTab browserTab = tabManager.getBrowserTab();

        /* Make sure that the tab is available and our url is valid */
        if (browserTab != null) {

            browserTab.reset();

            /*
             * Give the browser tab the query for any highlighting, and do it
             * now.
             */
            browserTab.setQuery(query, true);

            /* Tell the browser tab to navigate to this url */
            if (browserTab.setURL(location)) {

                /* Focus the browser tab */
                tabManager.setSelection(browserTab);
            }
        }
    }

    public void actionOpenModel() {

        /* Open dialog */
        final String filename = FileUtil.getFilePath(new String[] { "*.fml",
                "*.*" }, null, SWT.OPEN, null, "Select model");

        actionOpenModel(filename);
    }

    public void actionOpenModel(final String filename) {

        if (!StringUtil.isset(filename)) {
            return;
        }

        final File file = new File(filename);
        final String absoluteFile = file.getAbsolutePath();

        /* Show progress bar and display message on status line */
        statusBar.showProgressBar(true);
        statusBar.setText(String.format("Loading %s...", absoluteFile));

        Thread fetchingThread = new Thread() {
            @Override
            public void run() {

                /* Save the information to file */
                final Object object = FileUtil.readObject(file);

                GUI.display.syncExec(new Runnable() {
                    public void run() {
                        if ((object != null) && (object instanceof Model)) {

                            /* Hide the progress bar on the status line */
                            statusBar.showProgressBar(false);

                            SearchTab searchTab = tabManager
                                    .getActiveSearchTab();

                            if ((searchTab != null)
                                    || (searchTab instanceof ProcessingTab)) {

                                ProcessingTab processingTab = (ProcessingTab) searchTab;
                                Model model = (Model) object;

                                processingTab.setModelFile(file);

                                /* Populate the tab */
                                processingTab.setModel(model);

                                /* Update status bar */
                                statusBar.setText("Loaded " + absoluteFile);
                            }
                        } else {

                            /* Hide the progress bar on the status line */
                            statusBar.showProgressBar(false);

                            /* Show error message */
                            MessageBoxUtil
                                    .showError(
                                            GUI.shell,
                                            "Could not load model!\nThe file may be from an older version of FTF or has been corrupted.");

                            /* Update status bar */
                            statusBar.setText("Unable to load " + absoluteFile
                                    + "!");
                        }
                    }

                });
            }
        };

        fetchingThread.start();
    }

    public void actionOpenNewTicket() {

        /* Open a new ticket */
        BrowserUtil.openLink(URLUtil.FTF_NEW_TICKET);
    }

    public void actionOpenPreferences() {
        new PreferencesDialog(shell, "Preferences", fetcherGui).open();
    }

    public void actionOpenTutorial() {

        /* Open FTF tutorial */
        BrowserUtil.openLink(URLUtil.FTF_TUTORIAL);
    }

    public void actionPredict() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        /* Ensure we're dealing with the right type of query(!) */
        /* TODO: Change this to a dedicated pubmed tab */
        if ((searchTab != null) && (searchTab instanceof ProcessingTab)) {

            ProcessingTab processingTab = (ProcessingTab) searchTab;

            /* Predict */
            processingTab.predict();
        }
    }

    public void actionPrevious() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        if ((searchTab != null) && (searchTab instanceof ProcessingTab)) {

            ProcessingTab processingTab = (ProcessingTab) searchTab;

            /* Train current ticked examples */
            processingTab.previous();
        }
    }

    public void actionSave() {

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            File resultsFile = searchTab.getFile();

            if ((resultsFile != null) && (resultsFile.exists())) {

                String absoluteFile = resultsFile.getAbsolutePath();

                /* Save the information to file */
                FileUtil.writeObject(searchTab.getSavedQuery(), resultsFile);

                statusBar.setText("Saved " + absoluteFile);
            } else {
                actionSaveAs();
            }

        } else {
            GUI.log.error("This type of tab can't be saved!");
        }
    }

    public void actionSaveAll() {
        GUI.log
                .debug("TODO: Save all current query tabs to a 'session' type file");
    }

    public void actionSaveAs() {

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            String tabTitle = searchTab.getTitle();

            String name = "";
            if (StringUtil.isset(tabTitle)) {

                /* Use the name of the query tab for the filename */
                name = StringUtil.createFileName(tabTitle);
            } else {
                name = "untitled";
            }

            /* Get the date suitable for a filename */
            String currentDate = StringUtil.dateToFileName(StringUtil
                    .formatDate());

            /* Construct new filename */
            String fileName = name + "_results_" + currentDate + ".frs";
            String selectedFileName = FileUtil.getSavePath(fileName, "frs",
                    "Save results");

            /* User has entered a filename */
            if (StringUtil.isset(selectedFileName)) {

                File resultsFile = new File(selectedFileName);

                /* Save the information to file */
                FileUtil.writeObject(searchTab.getSavedQuery(), resultsFile);

                /* Associate this file with the search tab */
                searchTab.setFile(resultsFile);

                String absoluteFile = resultsFile.getAbsolutePath();

                statusBar.setText("Saved " + absoluteFile);
            }
        } else {
            GUI.log.error("This type of tab can't be saved!");
        }
    }

    public void actionSaveModel() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        if ((searchTab == null) || (!(searchTab instanceof ProcessingTab))) {
            return;
        }

        ProcessingTab processingTab = (ProcessingTab) searchTab;

        Model model = processingTab.getModel();

        if (model != null) {

            File modelFile = processingTab.getModelFile();

            if ((modelFile != null) && (modelFile.exists())) {

                String absoluteFile = modelFile.getAbsolutePath();

                FileUtil.writeObject(model, modelFile);

                statusBar.setText("Saved " + absoluteFile);
            } else {
                actionSaveModelAs();
            }
        } else {

            /* Show error message */
            MessageBoxUtil.showError(GUI.shell, "No model to save!");
        }
    }

    public void actionSaveModelAs() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        if ((searchTab == null) || (!(searchTab instanceof ProcessingTab))) {
            return;
        }

        ProcessingTab processingTab = (ProcessingTab) searchTab;

        Model model = processingTab.getModel();

        if (model != null) {

            /* Use the name of the query tab for the filename */
            String name = StringUtil.createFileName(searchTab.getTitle());

            /* Get the date suitable for a filename */
            String currentDate = StringUtil.dateToFileName(StringUtil
                    .formatDate());

            /* Construct new filename */
            String fileName = name + "_model_" + currentDate + ".fml";
            String selectedFileName = FileUtil.getSavePath(fileName, "fml",
                    "Save model");

            /* Filename was entered */
            if (StringUtil.isset(selectedFileName)) {

                File modelFile = new File(selectedFileName);

                /* Save the information to file */
                FileUtil.writeObject(model, modelFile);

                processingTab.setModelFile(modelFile);

                String absoluteFile = modelFile.getAbsolutePath();

                statusBar.setText("Saved " + absoluteFile);
            }
        } else {

            /* Show error message */
            MessageBoxUtil.showError(GUI.shell, "No model to save!");
        }
    }

    public void actionSelectAllServices() {

        final SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            ServiceManager serviceManager = searchTab.getServiceManager();

            /* Disable all sources */
            serviceManager.selectAll();
        }
    }

    public void actionSetStatusText(String text) {
        statusBar.setText(text);
    }

    public void actionShowStatusbar(boolean show) {
        statusBar.setShowStatusBar(show);
    }

    public void actionShowToolbar(boolean show) {
        toolBar.setShowToolBar(show);
    }

    public void actionStopFetch(SearchTab searchTab) {

        searchTab.stopFetch();

        /* Tell the main window we're not busy */
        searchTab.setBusy(false);
    }

    public void actionToggleHighlightTerms() {

        SettingsRegistry.highlightTerms = (!SettingsRegistry.highlightTerms);

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            MetadataPane metadataPane = searchTab.getMetadataPane();

            if (metadataPane != null) {
                metadataPane.toggleHighlight();
            }
        }
    }

    public void actionToggleMetadata() {

        SettingsRegistry.showMetadataPane = (!SettingsRegistry.showMetadataPane);

        /* Get the active query tab */
        SearchTab searchTab = tabManager.getActiveSearchTab();

        if (searchTab != null) {

            /* Toggle the pane for this particular selection */
            searchTab.onSelection();
        }
    }

    public void actionToggleSource(ServiceEntry serviceEntry) {

        /* Toggle the source on or off */
        serviceEntry.toggleSelected();
    }

    public void actionTrain() {

        SearchTab searchTab = tabManager.getActiveSearchTab();

        /* Ensure we're dealing with the right type of query(!) */
        /* TODO: Change this to a dedicated pubmed tab */
        if ((searchTab != null) && (searchTab instanceof ProcessingTab)) {

            ProcessingTab processingTab = (ProcessingTab) searchTab;

            /* Train current ticked examples */
            processingTab.train();
        }
    }

    public void actionUpdateServiceStatus(Dispatcher source) {

        /* Get the status tab */
        StatusTab statusTab = tabManager.getStatusTab();

        if (statusTab != null) {

            /* Update the status of the service in the status table */
            statusTab.updateStatus(source);
        }
    }

    public void actionViewProperties(TreeItemData data) {

        if (data.isResult()) {

            /* View result properties */
            new ResultDialog(data.getResult(), shell, "Result Properties")
                    .open();
        } else if (data.isService()) {

            /* View service properties */
            new ServiceDialog(data.getSearchService(), shell,
                    "Service Properties").open();
        }
    }

    public void handleEditAction(int action) {

        /* Get the currently focused control */
        Control control = display.getFocusControl();

        /* Return if it is not available */
        if (!WidgetUtil.isset(control)) {
            return;
        }

        switch (action) {

        /* Cut action */
        case EDIT_ACTION_CUT:
            if (control instanceof Text) {
                ((Text) control).cut();
            } else if (control instanceof StyledText) {
                ((StyledText) control).cut();
            } else if (control instanceof Combo) {
                ((Combo) control).cut();
            }
            break;

        /* Copy action */
        case EDIT_ACTION_COPY:
            if (control instanceof Text) {
                ((Text) control).copy();
            } else if (control instanceof StyledText) {
                ((StyledText) control).copy();
            } else if (control instanceof Combo) {
                ((Combo) control).copy();
            }
            break;

        /* Paste action */
        case EDIT_ACTION_PASTE:
            if (control instanceof Text) {
                ((Text) control).paste();
            } else if (control instanceof StyledText) {
                ((StyledText) control).paste();
            } else if (control instanceof Combo) {
                ((Combo) control).paste();
            }
            break;

        /* Select all action */
        case EDIT_ACTION_SELECTALL:
            if (control instanceof Text) {
                ((Text) control).selectAll();
            } else if (control instanceof StyledText) {
                ((StyledText) control).selectAll();
            } else if (control instanceof Combo) {
                ((Combo) control).setSelection(new Point(0, ((Combo) control)
                        .getText().length()));
            }
            break;

        /* Delete action */
        case EDIT_ACTION_DELETE:
            if (control instanceof Tree) {
                Tree tree = (Tree) control;
                if ((tree.getSelectionCount() > 0)
                        && (tree.getSelection()[0].getData() != null)) {
                    Object data = tree.getSelection()[0].getData();
                    if (data instanceof TreeItemData) {

                        /* Get the active query tab */
                        SearchTab searchTab = tabManager.getActiveSearchTab();

                        if (searchTab != null) {

                            /* Delete the selected items */
                            searchTab.performDeletion();
                        }
                    }
                }
            }
            break;
        }
    }

    public void syncControls() {
        tabManager = fetcherGui.getTabManager();
        statusBar = fetcherGui.getStatusbar();
        toolBar = fetcherGui.getToolbar();
        mainMenu = fetcherGui.getMainMenu();
    }

}
