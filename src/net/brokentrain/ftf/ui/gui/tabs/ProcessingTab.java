package net.brokentrain.ftf.ui.gui.tabs;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import net.brokentrain.ftf.Fetcher;
import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.ResultSet;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.ProcessingArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.model.QueryResult;
import net.brokentrain.ftf.ui.gui.model.SavedQuery;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.svm.Model;
import net.brokentrain.ftf.ui.gui.tabs.components.ToolTipHelpTextHandler;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.MessageBoxUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class ProcessingTab extends SearchTab {

    private EventManager eventManager;

    private Model model;

    private HashMap<String, ArrayList<Dispatcher>> dispatcherStore;

    private ArrayList<ResultSet> previousResults;

    private ArrayList<ResultSet> data;

    private ArrayList<ResultSet> resultsStore;

    private HashMap<Article, Double> predictions;

    private File modelFile;

    public ProcessingTab(GUI fetcherGui, CTabItem tabItem,
            TabManager tabManager, boolean showMessageArea) {
        super(fetcherGui, tabItem, tabManager, showMessageArea);

        eventManager = fetcherGui.getEventManager();
        dispatcherStore = new HashMap<String, ArrayList<Dispatcher>>();
        previousResults = new ArrayList<ResultSet>();
        data = new ArrayList<ResultSet>();
        resultsStore = new ArrayList<ResultSet>();

        SearchTab.TAB_TYPE = SearchTab.TAB_TYPE_PROCESSOR;

        model = new Model(3, 25);
    }

    public void addResults(ArrayList<ResultSet> results, boolean predict) {

        resultsStore.addAll(results);

        /* Hide the message area if it's the first result */
        hideMessageArea();

        ArrayList<Article> articles = new ArrayList<Article>();

        /* Populate the new tree with the results */
        for (ResultSet resultSet : results) {

            Result result = resultSet.getOriginalLink();
            Resource resource = result.getResource();
            Article article = resource.getArticle();

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            if (StringUtil.isset(abstractText)) {
                articles.add(article);
            }
        }

        if (predict) {
            model.predict(articles);
        }

        /* Populate the new tree with the results */
        for (ResultSet resultSet : results) {

            Result result = resultSet.getOriginalLink();
            Resource resource = result.getResource();
            Article article = resource.getArticle();

            TreeItem item = insertParentItem("what", null, result, resultSet);

            if (((ProcessingArticle) article).hasFullTextLink()) {
                item.setImage(PaintUtil.iconHasFullText);
            } else {
                item.setImage(PaintUtil.iconHasNoFullText);
            }

            if (predict) {

                /* Read the predictions in for these articles */
                predictions = model.getPredictions();

                if ((predictions != null) && (!predictions.isEmpty())) {
                    Double value = predictions.get(article);
                    String strValue = String.valueOf(value);
                    item.setText(1, ((value != null) ? strValue : "(none)"));

                    if (value != null) {
                        if (value > 0.50) {
                            item.setForeground(ColourUtil.darkGreen);
                        } else if (value > 0.40) {
                            item.setForeground(ColourUtil.darkYellow);
                        } else if (value > 0.30) {
                            item.setForeground(ColourUtil.darkRed);
                        }
                    }
                }
            } else {
                item.setText(1, "N/A");
            }
        }

        if (predict) {
            tree.setSortColumn(tree.getColumns()[1]);
            tree.setSortDirection(SWT.UP);
        }
    }

    public void buildResultMenu(Menu menu, final Result result,
            final TreeItem[] selection) {

        if (selection.length > 1) {

            MenuItem checkAllItem = new MenuItem(menu, SWT.NONE);
            checkAllItem.setText("Toggle selection");
            checkAllItem.setImage(PaintUtil.iconCheckAll);
            checkAllItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    for (TreeItem treeItem : selection) {
                        treeItem.setChecked(!treeItem.getChecked());
                    }
                }
            });

            new MenuItem(menu, SWT.SEPARATOR);
        }

        /* Open in internal browser */
        MenuItem openItem = new MenuItem(menu, SWT.CASCADE);
        openItem.setText("Open");
        openItem.setImage(PaintUtil.iconOpen);

        Menu openMenu = new Menu(openItem);
        openItem.setMenu(openMenu);

        /* Open in internal browser */
        MenuItem abstractItem = new MenuItem(openMenu, SWT.NONE);
        abstractItem.setText("Abstract");
        abstractItem.setImage(PaintUtil.iconText);
        abstractItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource resource = result.getResource();
                ProcessingArticle article = (ProcessingArticle) resource
                        .getArticle();
                String abstractLink = article.getAbstractLink();
                if (StringUtil.isset(abstractLink)) {
                    eventManager.actionOpenInInternalBrowser(abstractLink,
                            query);
                }
            }
        });

        /* Open in internal browser */
        MenuItem fulltextItem = new MenuItem(openMenu, SWT.NONE);
        fulltextItem.setText("Full-text");
        fulltextItem.setImage(PaintUtil.iconHTML);
        fulltextItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource resource = result.getResource();
                ProcessingArticle article = (ProcessingArticle) resource
                        .getArticle();
                String fullTextLink = article.getFullTextLink();
                if (StringUtil.isset(fullTextLink)) {
                    eventManager.actionOpenInInternalBrowser(fullTextLink,
                            query);
                }
            }
        });

        /* Launch in external browser */
        MenuItem launchItem = new MenuItem(menu, SWT.CASCADE);
        launchItem.setText("Launch");
        launchItem.setImage(PaintUtil.iconLaunch);

        Menu launchMenu = new Menu(launchItem);
        launchItem.setMenu(launchMenu);

        /* Open in internal browser */
        abstractItem = new MenuItem(launchMenu, SWT.NONE);
        abstractItem.setText("Abstract");
        abstractItem.setImage(PaintUtil.iconText);
        abstractItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource resource = result.getResource();
                ProcessingArticle article = (ProcessingArticle) resource
                        .getArticle();
                String abstractLink = article.getAbstractLink();
                if (StringUtil.isset(abstractLink)) {
                    eventManager.actionLaunchURL(abstractLink);
                }
            }
        });

        /* Open in internal browser */
        fulltextItem = new MenuItem(launchMenu, SWT.NONE);
        fulltextItem.setText("Full-text");
        fulltextItem.setImage(PaintUtil.iconHTML);
        fulltextItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource resource = result.getResource();
                ProcessingArticle article = (ProcessingArticle) resource
                        .getArticle();
                String fullTextLink = article.getFullTextLink();
                if (StringUtil.isset(fullTextLink)) {
                    eventManager.actionLaunchURL(fullTextLink);
                }
            }
        });

        /* Launch in external browser */
        MenuItem searchItem = new MenuItem(menu, SWT.CASCADE);
        searchItem.setText("Search");
        searchItem.setImage(PaintUtil.iconFetch);

        Menu searchMenu = new Menu(searchItem);
        searchItem.setMenu(searchMenu);

        /* Open in internal browser */
        MenuItem searchTitleItem = new MenuItem(searchMenu, SWT.NONE);
        searchTitleItem.setText("by title");
        searchTitleItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Resource resource = result.getResource();
                Article article = resource.getArticle();
                String articleTitle = article.getArticleTitle();
                if (StringUtil.isset(articleTitle)) {
                    SearchTab fullTextTab = eventManager
                            .actionNewTab(EventManager.SEARCH_TYPE_TEXT);
                    fullTextTab.setQuery(articleTitle);
                    SettingsManager settingsManager = SettingsManager
                            .getSettingsManager();
                    settingsManager.setInvestigate(true);
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        /* Open in internal browser */
        // MenuItem relatedItem = new MenuItem(menu, SWT.NONE);
        // relatedItem.setText("Show Related");
        // relatedItem.setImage(PaintUtil.iconRelated);
        // relatedItem.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent event) {
        // GUI.log.debug("TODO: Grab related articles");
        // }
        // });
        // new MenuItem(menu, SWT.SEPARATOR);
        MenuItem bibtexItem = new MenuItem(menu, SWT.NONE);
        bibtexItem.setText("Copy Bibtex");
        bibtexItem.setImage(PaintUtil.iconCopyBibtex);
        bibtexItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                eventManager.actionCopyBibtex(result);
            }
        });
    }

    @Override
    public void createTree() {

        /* Create actual results tree */
        tree = new Tree(getQueryArea(), SWT.BORDER | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK
                | SWT.VIRTUAL);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        /* Status of the download */
        final TreeColumn tcTitle = new TreeColumn(tree, SWT.NULL);
        tcTitle.setText("Title");
        tcTitle.setWidth(600);

        /* Status of the download */
        final TreeColumn tcRelevance = new TreeColumn(tree, SWT.NULL);
        tcRelevance.setText("Relevance");
        tcRelevance.setWidth(50);
        tcRelevance.pack();

        tree.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event e) {
                onMouseDoubleClick(e);
            }
        });

        tree.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onSelection();
            }
        });

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                TreeColumn sortColumn = tree.getSortColumn();
                TreeColumn currentColumn = (TreeColumn) e.widget;
                int dir = tree.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                } else {
                    tree.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }
                TreeItem[] items = tree.getItems();
                Collator collator = Collator.getInstance(Locale.getDefault());
                TreeColumn column = (TreeColumn) e.widget;
                int index = column == tcTitle ? 0 : 1;
                for (int i = 1; i < items.length; i++) {
                    String value1 = items[i].getText(index);
                    for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(index);

                        boolean isGreater = (collator.compare(value1, value2) < 0);

                        if ((dir == SWT.DOWN && isGreater)
                                || (dir == SWT.UP && !isGreater)) {

                            String[] values = { items[i].getText(0),
                                    items[i].getText(1) };

                            /* Save all the treeitems data (this is gross) */
                            TreeItemData data = (TreeItemData) items[i]
                                    .getData();
                            Color colour = items[i].getForeground();
                            Image image = items[i].getImage();
                            Image tipImage = (Image) items[i]
                                    .getData("TIP_IMAGE");
                            String tipString = (String) items[i]
                                    .getData("TIP_TEXT");
                            boolean isChecked = items[i].getChecked();

                            /* Dispose the old treeitem */
                            items[i].dispose();

                            /* Create a new treeitem with the existing data */
                            final TreeItem item = new TreeItem(tree, SWT.NONE,
                                    j);
                            item.setData(data);
                            item.setForeground(colour);
                            item.setImage(image);
                            item.setText(values);
                            item.setChecked(isChecked);
                            item.setData("TIP_IMAGE", tipImage);
                            item.setData("TIP_TEXT", tipString);
                            item.setData("TIP_HELPTEXTHANDLER",
                                    new ToolTipHelpTextHandler() {
                                        public String getHelpText(Widget widget) {
                                            return item.getText();
                                        }
                                    });
                            items = tree.getItems();
                            break;
                        }
                    }
                }
                tree.setSortDirection(dir);
            }
        };

        tcTitle.addListener(SWT.Selection, sortListener);
        tcRelevance.addListener(SWT.Selection, sortListener);
        tree.setSortColumn(tcTitle);
        tree.setSortDirection(SWT.UP);

        final Menu menu = new Menu(tree);
        tree.setMenu(menu);
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {

                /* Get rid of existing menu items */
                MenuItem[] items = menu.getItems();
                for (MenuItem element : items) {
                    (element).dispose();
                }

                final TreeItem[] selectedItems = tree.getSelection();

                /* Return on empty selection */
                if (selectedItems.length < 1) {
                    return;
                }

                /* Get the data of the first selection */
                TreeItemData treeItemData = (TreeItemData) selectedItems[0]
                        .getData();

                if (treeItemData == null) {

                    /* Exit immediately for items without data */
                    return;
                }

                if (treeItemData.isResult()) {

                    Result treeItemResult = treeItemData.getResult();

                    /* Build the context menu for a result */
                    buildResultMenu(menu, treeItemResult, selectedItems);

                    /* Append seperator */
                    new MenuItem(menu, SWT.SEPARATOR);
                }

                MenuItem propertiesItem = new MenuItem(menu, SWT.NONE);
                propertiesItem.setText("Properties");
                propertiesItem.setImage(PaintUtil.iconProperties);
                propertiesItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        TreeItem selectedItem = selectedItems[0];
                        Object treeData = selectedItem.getData();
                        if (treeData != null) {

                            /* View the properties of a service or result */
                            eventManager
                                    .actionViewProperties((TreeItemData) treeData);
                        }
                    }
                });
            }
        });

        tree.pack();
    }

    @Override
    public void decorateItem(TreeItem item, ResultSet resultSet) {

        /* Set the bigger tooltip image */
        item.setData("TIP_IMAGE", PaintUtil.iconTextBig);

        TreeItemData itemData = (TreeItemData) item.getData();

        Result result = itemData.getResult();
        Resource resource = result.getResource();
        Article article = resource.getArticle();

        /* Insert metadata into tooltip */
        LinkedHashMap<String, String> metadata = article.getValues();

        if ((metadata != null) && (!metadata.isEmpty())) {
            setMetadata(item, metadata);
        }
    }

    public void fetch(final List<Dispatcher> dispatchers) {

        /* Create a new bunch of entries for the service tracker */
        ArrayList<ServiceEntry> serviceEntries = new ArrayList<ServiceEntry>();

        for (Dispatcher dispatcher : dispatchers) {

            /* Add each service entry for our running services */
            serviceEntries.add(dispatcher.getServiceEntry());

        }

        /* Inform the source tracker of the sources involved */
        serviceTracker.setSourceRegistry(serviceEntries);

        /* Create new Fetcher */
        ftf = new Fetcher();

        /* Tell results to come here */
        ftf.addObserver(this);

        ftf.setProcessingAbstract(true);

        /* Spawn new thread */
        Thread fetchingThread = new Thread() {
            @Override
            public void run() {

                /* Start fetching the results */

                /*
                 * NOTE: No query or anything needed since we are just
                 * re-using the dispatchers which have been used before.
                 * Result come in via the standard route and passed to
                 * populateTree()
                 */
                ftf.fetch(dispatchers);
            }
        };

        fetchingThread.start();
    }

    public Model getModel() {
        return model;
    }

    public File getModelFile() {
        return modelFile;
    }

    public ArrayList<ResultSet> getResults() {
        return resultsStore;
    }

    @Override
    public SavedQuery getSavedQuery() {
        ArrayList<QueryResult> queryResults = new ArrayList<QueryResult>();
        QueryResult queryResult = new QueryResult(resultsStore);
        queryResults.add(queryResult);
        SavedQuery savedQuery = new SavedQuery(queryResults, query,
                SearchTab.TAB_TYPE);

        return savedQuery;
    }

    public void next() {

        GUI.log.info("Fetching next abstracts");

        if (!dispatcherStore.isEmpty()) {

            /* Add all these to the previous results */
            previousResults.addAll(data);

            /* Remove all results from the window */
            tree.removeAll();

            /* Clear the actual store of results */
            data.clear();

            for (String name : dispatcherStore.keySet()) {
                ArrayList<Dispatcher> dispatcherList = dispatcherStore
                        .get(name);

                /* Fetch the next abstracts */
                fetch(dispatcherList);

                /* Update the main interface */
                setBusy(true);
            }
        } else {
            GUI.log.debug("No previous query to resume.");
        }
    }

    @Override
    public void onMouseDoubleClick(Event event) {
        TreeItem treeItem = tree.getItem(new Point(event.x, event.y));
        if (treeItem != null) {

            if ((event.stateMask & SWT.SHIFT) != 0) {
                TreeItemData treeItemData = (TreeItemData) treeItem.getData();
                Result result = treeItemData.getResult();
                Resource resource = result.getResource();
                ProcessingArticle article = (ProcessingArticle) resource
                        .getArticle();
                String fullTextLink = article.getFullTextLink();
                if (StringUtil.isset(fullTextLink)) {
                    eventManager.actionLaunchURL(fullTextLink);
                }
            } else {

                eventManager.actionHandleItem(treeItem);
            }
        }
    }

    @Override
    public void populateTree(Dispatcher dispatcher) {

        /* Register the dispatcher and query with the tab */
        String queryTerm = dispatcher.getQueryTerm();

        ArrayList<Dispatcher> dispatcherList = dispatcherStore.get(queryTerm);
        if (dispatcherList == null) {
            dispatcherList = new ArrayList<Dispatcher>();
            dispatcherList.add(dispatcher);
            dispatcherStore.put(queryTerm, dispatcherList);
        } else if (!dispatcherList.contains(dispatcher)) {
            dispatcherList.add(dispatcher);
        }

        ArrayList<ResultSet> results = dispatcher.getResults();

        /* Add dispatcher to our collection */
        data.addAll(results);

        /* Populate the tree with the new results */
        addResults(results, true);
    }

    @Override
    public void populateTree(Object loadedResults) {

        /* Check form of the object being passed */
        if (loadedResults instanceof SavedQuery) {

            SavedQuery savedQuery = (SavedQuery) loadedResults;

            /* Set the query that was used to get these */
            setQuery(savedQuery.getQuery());

            ArrayList<QueryResult> queryResults = savedQuery.getData();

            if (!queryResults.isEmpty()) {

                for (QueryResult result : queryResults) {

                    addResults(result.getData(), true);

                    /* Add new results to existing ones (if they exist!) */
                    data.addAll(result.getData());
                }
            }
        }
    }

    public void predict() {

        if (!model.hasTrained()) {
            MessageBoxUtil
                    .showMessage(
                            GUI.shell,
                            SWT.OK | SWT.ICON_INFORMATION,
                            "Information",
                            "No suitable model found!\n"
                                    + "Please either load one from file or train some data first.");
            return;
        }

        /* Get all the items in the query results tab */
        List<TreeItem> treeItems = Arrays.asList(tree.getItems());
        ArrayList<Article> articles = new ArrayList<Article>();

        /* For each result (with hopefully an abstract) */
        for (TreeItem item : treeItems) {

            /* Retrieve the associated article */
            TreeItemData treeItemData = (TreeItemData) item.getData();
            Result result = treeItemData.getResult();
            Resource resource = result.getResource();
            Article article = resource.getArticle();

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            if (StringUtil.isset(abstractText)) {
                articles.add(article);
            }
        }

        model.predict(articles);

        refresh();
    }

    public void previous() {
        GUI.log.info("Restoring previous abstracts");

        if (!previousResults.isEmpty()) {

            /* Remove all results from the window */
            tree.removeAll();

            /* Repopulate the tree with the previous results */
            addResults(previousResults, false);

        } else {
            GUI.log.debug("No previous results.");
        }
    }

    public void refresh() {

        /* Remove everything from the tree */
        tree.removeAll();

        /* Re-add our already available results */
        addResults(data, true);
    }

    public void setModel(Model model) {
        GUI.log.debug("Attaching new model to results!");
        this.model = model;
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public void train() {

        /* Get all the items in the query results tab */
        List<TreeItem> treeItems = Arrays.asList(tree.getItems());
        for (TreeItem item : treeItems) {
            TreeItemData treeItemData = (TreeItemData) item.getData();
            Resource resource = treeItemData.getResult().getResource();
            Article article = resource.getArticle();

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            if (StringUtil.isset(abstractText)) {
                model.addTrainingSample(abstractText);
            }
        }

        LinkedHashMap<Article, Boolean> samples = new LinkedHashMap<Article, Boolean>();

        /* For each result (with hopefully an abstract) */
        for (TreeItem item : treeItems) {

            /* Retrieve the associated article */
            TreeItemData treeItemData = (TreeItemData) item.getData();
            Resource resource = treeItemData.getResult().getResource();
            Article article = resource.getArticle();

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            if (StringUtil.isset(abstractText)) {
                samples.put(article, Boolean.valueOf(item.getChecked()));
            }
        }

        /* Train the model */
        model.train(samples);

        int totalTrained = model.getTotalTrained();

        String trainedMessage = String.format("Trained %d abstracts."
                + " (%d trained in total)", samples.size(), totalTrained);
        eventManager.actionSetStatusText(trainedMessage);
    }
}
