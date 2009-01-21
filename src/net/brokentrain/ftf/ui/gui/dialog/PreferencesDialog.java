package net.brokentrain.ftf.ui.gui.dialog;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.properties.BrowserTabProperties;
import net.brokentrain.ftf.ui.gui.properties.ConnectionProperties;
import net.brokentrain.ftf.ui.gui.properties.PropertyPage;
import net.brokentrain.ftf.ui.gui.properties.QueryTabProperties;
import net.brokentrain.ftf.ui.gui.properties.SystemTrayProperties;
import net.brokentrain.ftf.ui.gui.properties.TransferTabProperties;
import net.brokentrain.ftf.ui.gui.properties.ViewProperties;
import net.brokentrain.ftf.ui.gui.properties.services.ArXivProperties;
import net.brokentrain.ftf.ui.gui.properties.services.DOIProperties;
import net.brokentrain.ftf.ui.gui.properties.services.GoogleDesktopSearchProperties;
import net.brokentrain.ftf.ui.gui.properties.services.GoogleScholarProperties;
import net.brokentrain.ftf.ui.gui.properties.services.GoogleWebSearchProperties;
import net.brokentrain.ftf.ui.gui.properties.services.PlosJournalsProperties;
import net.brokentrain.ftf.ui.gui.properties.services.PubMedCentralProperties;
import net.brokentrain.ftf.ui.gui.properties.services.PubMedProperties;
import net.brokentrain.ftf.ui.gui.properties.services.ScirusProperties;
import net.brokentrain.ftf.ui.gui.properties.services.ServicesProperties;
import net.brokentrain.ftf.ui.gui.properties.services.TerrierProperties;
import net.brokentrain.ftf.ui.gui.properties.services.WebOfKnowledgeProperties;
import net.brokentrain.ftf.ui.gui.properties.services.YahooWebSearchProperties;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.settings.SettingsSaver;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/* 
 * TODO: This entire class could REALLY do with a re-think - since there were
 * few preference pages to think about they were just hard  coded, then it
 * started to grow and grow leading to this mess.  This should probably be made
 * dynamic and just build the *service* preference pages by seeing what is in
 * SettingsRegistry.services rather than hard coding them explicitly :(! This
 * kind of applies to the entire services package that is a sub-package of the
 * properties package, too.
 * NOTE: This is heavily based on RSSOwls code!
 * */
public class PreferencesDialog extends Dialog {

    public static int lastOpenedPropertyPage = 0;

    private static final int dialogMinWidth = 460;

    private GUI fetcherGui;

    private Composite buttonHolder;

    private Composite contentHolder;

    private Composite prefTitleHolder;

    private Label labelImgHolder;

    private Label labelPrefTitle;

    private PropertyPage activePropertyPage;

    private String lastSelectedItemText;

    private String title;

    private TreeItem arxiv;

    private TreeItem browserTab;

    private TreeItem connection;

    private TreeItem crawler;

    private TreeItem doi;

    private TreeItem google;

    private TreeItem googleDesktop;

    private TreeItem googleScholar;

    private TreeItem plosjournals;

    private TreeItem pubmed;

    private TreeItem pubmedCentral;

    private TreeItem queryTab;

    private TreeItem scirus;

    private TreeItem services;

    private TreeItem systemTray;

    private TreeItem terrier;

    private TreeItem transferTab;

    private TreeItem view;

    private TreeItem webofknowledge;

    private TreeItem yahoo;

    private Tree tree;

    public PreferencesDialog(Shell parentShell, String dialogTitle,
            GUI fetcherGui) {
        super(parentShell);
        this.title = dialogTitle;
        this.fetcherGui = fetcherGui;

        /* Init the PropertyChangeManager */
        PropertyPage.initPropertyChangeManager(fetcherGui);
    }

    // private void createGeneralProps() {
    // renewPropertyPage("General");
    // activePropertyPage = new GeneralProperties(contentHolder, fetcherGui);
    // }

    // private void createCrawlerProps() {
    // renewPropertyPage("Crawler");
    // activePropertyPage = new CrawlerProperties(contentHolder, fetcherGui);
    // }

    // private void createBrowserProps() {
    // renewPropertyPage("Browser");
    // }

    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {
            saveSettings();
        }

        if (activePropertyPage != null) {
            activePropertyPage.dispose();
        }

        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        shell.setLayout(LayoutUtil.createGridLayout(1, 0, 5));

        shell.setText(title);
        shell.setSize(0, 0);
    }

    private void createArxivProperties() {
        renewPropertyPage("ArXiv");
        activePropertyPage = new ArXivProperties(contentHolder);
    }

    private void createBrowserTabProps() {
        renewPropertyPage("Internal Browser");
        activePropertyPage = new BrowserTabProperties(contentHolder);
    }

    // private void createLogTabProps() {
    // renewPropertyPage("Debug Log");
    // activePropertyPage = new LogTabProperties(contentHolder);
    // }

    @Override
    protected Control createButtonBar(Composite parent) {

        buttonHolder = new Composite(parent, SWT.NONE);
        buttonHolder.setLayout(LayoutUtil
                .createGridLayout(2, 0, 0, 5, 5, false));
        buttonHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));

        Composite okCancelHolder = new Composite(buttonHolder, SWT.NONE);
        okCancelHolder.setLayout(LayoutUtil.createGridLayout(2, 0, 5, 5));
        okCancelHolder.setLayoutData(new GridData(SWT.END, SWT.TOP, false,
                false));

        if (GUI.display.getDismissalAlignment() == SWT.RIGHT) {

            createButton(okCancelHolder, IDialogConstants.CANCEL_ID, "Cancel",
                    false).setFont(FontUtil.dialogFont);
            createButton(okCancelHolder, IDialogConstants.OK_ID, "OK", true)
                    .setFont(FontUtil.dialogFont);
        } else {

            createButton(okCancelHolder, IDialogConstants.OK_ID, "OK", true)
                    .setFont(FontUtil.dialogFont);
            createButton(okCancelHolder, IDialogConstants.CANCEL_ID, "Cancel",
                    false).setFont(FontUtil.dialogFont);
        }

        return buttonHolder;
    }

    // private void createStatusTabProps() {
    // renewPropertyPage("Status");
    // activePropertyPage = new StatusTabProperties(contentHolder, fetcherGui);
    // }

    private void createConnectionProps() {
        renewPropertyPage("Connection");
        activePropertyPage = new ConnectionProperties(contentHolder, fetcherGui);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite baseComposite = (Composite) super.createDialogArea(parent);
        baseComposite.setLayout(LayoutUtil.createGridLayout(2, 0, 5, 15, 0,
                false));
        baseComposite.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_BOTH, 1));

        Composite treeHolder = new Composite(baseComposite, SWT.NONE);
        treeHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_VERTICAL, 1, convertHorizontalDLUsToPixels(140)));
        treeHolder.setLayout(LayoutUtil.createGridLayout(1, 5, 0));

        tree = new Tree(treeHolder, SWT.BORDER);
        tree.setFont(FontUtil.dialogFont);
        tree.setFocus();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        tree.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleTreeItemSelect();
            }
        });

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                TreeItem selectedItem = tree.getSelection()[0];
                if ((e.keyCode == SWT.CR) && (selectedItem.getItemCount() > 0)) {
                    selectedItem.setExpanded(!selectedItem.getExpanded());
                }
            }
        });

        tree.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event event) {
                if (tree.getSelectionCount() > 0) {
                    Rectangle clickedRect = event.getBounds();
                    Rectangle selectedRect = tree.getSelection()[0].getBounds();

                    /* Only handle event, if Mouse is over treeitem */
                    if (selectedRect.contains(clickedRect.x, clickedRect.y)) {
                        tree.getSelection()[0]
                                .setExpanded(!tree.getSelection()[0]
                                        .getExpanded());
                    }
                }
            }
        });

        populateTree();

        contentHolder = new Composite(baseComposite, SWT.NONE);
        contentHolder.setLayoutData(new GridData(GridData.FILL_BOTH
                | GridData.VERTICAL_ALIGN_BEGINNING));
        contentHolder.setLayout(LayoutUtil.createGridLayout(1, 5, 0));

        prefTitleHolder = new Composite(contentHolder, SWT.NONE);

        GridLayout prefTitleHolderLayout = new GridLayout(2, false);
        prefTitleHolderLayout.marginWidth = 1;
        prefTitleHolderLayout.marginHeight = 2;
        prefTitleHolderLayout.marginLeft = 4;
        prefTitleHolder.setLayout(prefTitleHolderLayout);

        prefTitleHolder.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        prefTitleHolder.setBackground(GUI.display
                .getSystemColor(SWT.COLOR_WHITE));

        prefTitleHolder.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.setForeground(GUI.display
                        .getSystemColor(SWT.COLOR_DARK_GRAY));
                Rectangle bounds = prefTitleHolder.getClientArea();
                bounds.height -= 2;
                bounds.width -= 1;
                e.gc.drawRectangle(bounds);
            }
        });

        setTreeSelection(lastOpenedPropertyPage);
        handleTreeItemSelect();

        Label seperator = new Label(baseComposite, SWT.HORIZONTAL
                | SWT.SEPARATOR);
        seperator.setLayoutData(LayoutDataUtil.createGridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL,
                2));

        if (WidgetUtil.isset(tree.getHorizontalBar())) {
            tree.getHorizontalBar().setSelection(0);
        }

        return contentHolder;
    }

    private void createDOIProperties() {
        renewPropertyPage("DOI");
        activePropertyPage = new DOIProperties(contentHolder);
    }

    private void createGoogleDesktopSearchProperties() {
        renewPropertyPage("Google Desktop Search");
        activePropertyPage = new GoogleDesktopSearchProperties(contentHolder);
    }

    // private void createCiteSeerProperties() {
    // renewPropertyPage("Cite Seer");
    // activePropertyPage = new CiteSeerProperties(contentHolder);
    // }

    private void createGoogleScholarProperties() {
        renewPropertyPage("Google Scholar");
        activePropertyPage = new GoogleScholarProperties(contentHolder);
    }

    private void createGoogleWebSearchProperties() {
        renewPropertyPage("Google Web Search");
        activePropertyPage = new GoogleWebSearchProperties(contentHolder);
    }

    private void createPlosJournalsProperties() {
        renewPropertyPage("Plos Journals");
        activePropertyPage = new PlosJournalsProperties(contentHolder);
    }

    private void createPubMedCentralProperties() {
        renewPropertyPage("PubMed Central");
        activePropertyPage = new PubMedCentralProperties(contentHolder);
    }

    private void createPubMedProperties() {
        renewPropertyPage("PubMed");
        activePropertyPage = new PubMedProperties(contentHolder);
    }

    private void createQueryTabProps() {
        renewPropertyPage("Queries");
        activePropertyPage = new QueryTabProperties(contentHolder);
    }

    private void createScirusProperties() {
        renewPropertyPage("Scirus");
        activePropertyPage = new ScirusProperties(contentHolder);
    }

    private void createServicesProperties() {
        renewPropertyPage("Default Services");
        activePropertyPage = new ServicesProperties(contentHolder);
    }

    private void createSystemTrayProps() {
        renewPropertyPage("System Tray");
        activePropertyPage = new SystemTrayProperties(contentHolder, fetcherGui);
    }

    private void createTerrierProperties() {
        renewPropertyPage("Terrier");
        activePropertyPage = new TerrierProperties(contentHolder);
    }

    private void createTransferTabProps() {
        renewPropertyPage("Transfer");
        activePropertyPage = new TransferTabProperties(contentHolder);
    }

    private void createViewProps() {
        renewPropertyPage("View");
        activePropertyPage = new ViewProperties(contentHolder, fetcherGui);
    }

    private void createWebOfKnowledgeProperties() {
        renewPropertyPage("WebOfKnowledge");
        activePropertyPage = new WebOfKnowledgeProperties(contentHolder);
    }

    private void createYahooProperties() {
        renewPropertyPage("Yahoo Web Search");
        activePropertyPage = new YahooWebSearchProperties(contentHolder);
    }

    @Override
    protected int getShellStyle() {
        int style = SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.APPLICATION_MODAL
                | getDefaultOrientation();

        return style;
    }

    void handleTreeItemSelect() {

        if (tree.getSelection().length == 0) {
            return;
        }

        if (tree.getSelection()[0].getText().equals(lastSelectedItemText)) {
            return;
        }

        lastSelectedItemText = tree.getSelection()[0].getText();

        // if (tree.getSelection()[0].getText().equals("General")) {
        // createGeneralProps();
        // lastOpenedPropertyPage = 0;
        // }

        // if (tree.getSelection()[0].getText().equals("Crawler")) {
        // createCrawlerProps();
        // lastOpenedPropertyPage = 23;
        // }

        /* Browser */
        // if (tree.getSelection()[0].getText().equals("Browser")) {
        // createBrowserProps();
        // lastOpenedPropertyPage = 1;
        // }
        /* Connection */
        if (tree.getSelection()[0].getText().equals("Connection")) {
            createConnectionProps();
            lastOpenedPropertyPage = 2;
        }

        /* System Tray */
        else if (tree.getSelection()[0].getText().equals("System Tray")) {
            createSystemTrayProps();
            lastOpenedPropertyPage = 3;
        }

        /* View */
        else if (tree.getSelection()[0].getText().equals("View")) {
            createViewProps();
            lastOpenedPropertyPage = 4;
        }

        /* Browser Tab */
        else if (tree.getSelection()[0].getText().equals("Internal Browser")) {
            createBrowserTabProps();
            lastOpenedPropertyPage = 5;
        }

        /* Log Tab */
        // else if (tree.getSelection()[0].getText().equals("Debug Log")) {
        // createLogTabProps();
        // lastOpenedPropertyPage = 6;
        // }
        /* Query Tab */
        else if (tree.getSelection()[0].getText().equals("Queries")) {
            createQueryTabProps();
            lastOpenedPropertyPage = 7;
        }

        /* Status Tab */
        // else if (tree.getSelection()[0].getText().equals("Status")) {
        // createStatusTabProps();
        // lastOpenedPropertyPage = 8;
        // }
        /* Transfer Tab */
        else if (tree.getSelection()[0].getText().equals("Transfer")) {
            createTransferTabProps();
            lastOpenedPropertyPage = 9;
        }

        /* Services */
        else if (tree.getSelection()[0].getText().equals("Services")) {
            createServicesProperties();
            lastOpenedPropertyPage = 10;
        }

        /* ArXiv */
        else if (tree.getSelection()[0].getText().equals("ArXiv")) {
            createArxivProperties();
            lastOpenedPropertyPage = 11;
        }

        /* CiteSeer */
        // else if (tree.getSelection()[0].getText().equals("Cite Seer")) {
        // createCiteSeerProperties();
        // lastOpenedPropertyPage = 12;
        // }
        /* DOI */
        else if (tree.getSelection()[0].getText().equals("DOI")) {
            createDOIProperties();
            lastOpenedPropertyPage = 13;
        }

        /* GoogleDesktopSearch */
        else if (tree.getSelection()[0].getText().equals(
                "Google Desktop Search")) {
            createGoogleDesktopSearchProperties();
            lastOpenedPropertyPage = 24;
        }

        /* Google */
        else if (tree.getSelection()[0].getText().equals("Google Web Search")) {
            createGoogleWebSearchProperties();
            lastOpenedPropertyPage = 14;
        }

        /* GoogleScholar */
        else if (tree.getSelection()[0].getText().equals("Google Scholar")) {
            createGoogleScholarProperties();
            lastOpenedPropertyPage = 15;
        }

        /* PlosJournals */
        else if (tree.getSelection()[0].getText().equals("Plos Journals")) {
            createPlosJournalsProperties();
            lastOpenedPropertyPage = 16;
        }

        /* PubMed */
        else if (tree.getSelection()[0].getText().equals("PubMed")) {
            createPubMedProperties();
            lastOpenedPropertyPage = 17;
        }

        /* PubMedCentral */
        else if (tree.getSelection()[0].getText().equals("PubMed Central")) {
            createPubMedCentralProperties();
            lastOpenedPropertyPage = 18;
        }

        /* Scirus */
        else if (tree.getSelection()[0].getText().equals("Scirus")) {
            createScirusProperties();
            lastOpenedPropertyPage = 19;
        }

        /* Terrier */
        else if (tree.getSelection()[0].getText().equals("Terrier")) {
            createTerrierProperties();
            lastOpenedPropertyPage = 20;
        }

        /* WebOfKnowledge */
        else if (tree.getSelection()[0].getText().equals("Web Of Knowledge")) {
            createWebOfKnowledgeProperties();
            lastOpenedPropertyPage = 21;
        }

        /* Yahoo */
        else if (tree.getSelection()[0].getText().equals("Yahoo Web Search")) {
            createYahooProperties();
            lastOpenedPropertyPage = 22;
        }

        contentHolder.layout();

        initializeBounds(false);
    }

    @Override
    protected void initializeBounds() {
        initializeBounds(true);
    }

    protected void initializeBounds(boolean updateLocation) {

        Point currentSize = getShell().getSize();
        Point bestSize = getShell().computeSize(
                convertHorizontalDLUsToPixels(dialogMinWidth), SWT.DEFAULT);

        Point location = (updateLocation == true) ? getInitialLocation(bestSize)
                : getShell().getLocation();

        if (updateLocation && (bestSize.y > currentSize.y)) {
            getShell()
                    .setBounds(location.x, location.y, bestSize.x, bestSize.y);
        } else if (bestSize.y > currentSize.y) {
            getShell().setSize(bestSize.x, bestSize.y);
        }

        getShell().setMinimumSize(bestSize.x, bestSize.y);
    }

    void populateTree() {

        String selectionText = null;
        if (tree.getSelectionCount() > 0) {
            selectionText = tree.getSelection()[0].getText();
        }

        if (tree.getItemCount() > 0) {
            tree.removeAll();
        }

        /* General properties */
        // general = new TreeItem(tree, SWT.NONE);
        // general.setText("General");
        /* Crawler sub-property */
        // crawler = new TreeItem(tree, SWT.NONE);
        // crawler.setText("Crawler");
        /* Browser sub-property */
        // browser = new TreeItem(tree, SWT.NONE);
        // browser.setText("Browser");
        /* Connection sub-property */
        connection = new TreeItem(tree, SWT.NONE);
        connection.setText("Connection");

        /* System Tray sub-property */
        if (SettingsRegistry.useSystemTray()) {

            // if (!WidgetShop.isset(general)) {
            // general = new TreeItem(tree, SWT.NONE);
            // general.setText("General");
            // }

            systemTray = new TreeItem(tree, SWT.NONE);
            systemTray.setText("System Tray");
        }

        /* View properties */
        view = new TreeItem(tree, SWT.NONE);
        view.setText("View");

        /* Browser tab sub-property */
        browserTab = new TreeItem(view, SWT.NONE);
        browserTab.setText("Internal Browser");

        /* Log tab sub-property */
        // logTab = new TreeItem(view, SWT.NONE);
        // logTab.setText("Debug Log");
        // logTab.setForeground(ColourUtil.gray);
        /* Status tab sub-property */
        // statusTab = new TreeItem(view, SWT.NONE);
        // statusTab.setText("Status");
        // statusTab.setForeground(ColourUtil.gray);
        /* Transfer tab sub-property */
        transferTab = new TreeItem(view, SWT.NONE);
        transferTab.setText("Transfer");

        /* Query tab sub-property */
        queryTab = new TreeItem(view, SWT.NONE);
        queryTab.setText("Queries");

        /* Services sub-property */
        services = new TreeItem(tree, SWT.NONE);
        services.setText("Services");

        /* Arxiv sub-property */
        arxiv = new TreeItem(services, SWT.NONE);
        arxiv.setText("ArXiv");

        /* DOI sub-property */
        doi = new TreeItem(services, SWT.NONE);
        doi.setText("DOI");

        /* Cite Seer sub-property */
        // citeSeer = new TreeItem(services, SWT.NONE);
        // citeSeer.setText("Cite Seer");
        /* Google Scholar sub-property */
        googleDesktop = new TreeItem(services, SWT.NONE);
        googleDesktop.setText("Google Desktop Search");

        /* Google Scholar sub-property */
        google = new TreeItem(services, SWT.NONE);
        google.setText("Google Web Search");

        /* Google Scholar sub-property */
        googleScholar = new TreeItem(services, SWT.NONE);
        googleScholar.setText("Google Scholar");

        /* Plos Journals sub-property */
        plosjournals = new TreeItem(services, SWT.NONE);
        plosjournals.setText("Plos Journals");

        /* PubMed sub-property */
        pubmed = new TreeItem(services, SWT.NONE);
        pubmed.setText("PubMed");

        /* PubMed Central sub-property */
        pubmedCentral = new TreeItem(services, SWT.NONE);
        pubmedCentral.setText("PubMed Central");

        /* Scirus sub-property */
        scirus = new TreeItem(services, SWT.NONE);
        scirus.setText("Scirus");

        /* Terrier sub-property */
        terrier = new TreeItem(services, SWT.NONE);
        terrier.setText("Terrier");

        /* WOK sub-property */
        webofknowledge = new TreeItem(services, SWT.NONE);
        webofknowledge.setText("Web Of Knowledge");

        /* Yahoo sub-property */
        yahoo = new TreeItem(services, SWT.NONE);
        yahoo.setText("Yahoo Web Search");

        // if (WidgetShop.isset(general))
        // general.setExpanded(true);

        if (WidgetUtil.isset(view)) {
            view.setExpanded(true);
        }

        if (WidgetUtil.isset(services)) {
            services.setExpanded(true);
        }

        if (StringUtil.isset(selectionText)) {
            restoreSelection(selectionText, tree.getItems());
        }
    }

    private void renewPropertyPage(String title) {

        if (activePropertyPage != null) {
            activePropertyPage.updatePropertiesChangeManager();
            activePropertyPage.dispose();
        }

        if (labelPrefTitle == null) {
            labelPrefTitle = new Label(prefTitleHolder, SWT.LEFT);
            labelPrefTitle.setBackground(GUI.display
                    .getSystemColor(SWT.COLOR_WHITE));
            labelPrefTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                    true, false));
            labelPrefTitle.setFont(FontUtil.dialogBoldFont);
        }

        if (labelImgHolder == null) {
            labelImgHolder = new Label(prefTitleHolder, SWT.NONE);
            labelImgHolder.setBackground(GUI.display
                    .getSystemColor(SWT.COLOR_WHITE));
            labelImgHolder.setImage(PaintUtil.iconBlueStripes);
            labelImgHolder.setLayoutData(new GridData(SWT.END, SWT.END, false,
                    false));
        }

        labelPrefTitle.setText(title);
        labelPrefTitle.update();
        labelImgHolder.update();
        prefTitleHolder.layout();
        contentHolder.layout();
    }

    private void restoreSelection(String selectionText, TreeItem items[]) {

        for (TreeItem item : items) {
            if (selectionText.equals(item.getText())) {
                tree.setSelection(new TreeItem[] { item });
                break;
            }

            if (item.getItemCount() != 0) {
                restoreSelection(selectionText, item.getItems());
            }
        }
    }

    void saveSettings() {

        activePropertyPage.applyButtonPressed();

        PropertyPage.getPropertyChangeManager().saveProperties();

        new SettingsSaver(fetcherGui).saveCoreSettings();

        new SettingsSaver(fetcherGui).saveGUISettings();

        new SettingsSaver(fetcherGui).saveServiceSettings();
    }

    @Override
    protected void setButtonLayoutData(Button button) {
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
                SWT.DEFAULT, true).x);
        button.setLayoutData(data);
    }

    private void setTreeSelection(int propertyPage) {
        switch (propertyPage) {

        /* Workbench */
        // case 0:
        // tree.setSelection(new TreeItem[] { general });
        // break;
        /* Browser */
        // case 1:
        // tree.setSelection(new TreeItem[] { browser });
        // break;
        /* Connection */
        case 2:
            tree.setSelection(new TreeItem[] { connection });
            break;

        /* System Tray */
        case 3:
            tree.setSelection(new TreeItem[] { systemTray });
            break;

        /* Tabs */
        case 4:
            tree.setSelection(new TreeItem[] { view });
            break;

        /* Browser Tab */
        case 5:
            tree.setSelection(new TreeItem[] { browserTab });
            break;

        /* Log Tab */
        // case 6:
        // tree.setSelection(new TreeItem[] { logTab });
        // break;
        /* Query Tab */
        case 7:
            tree.setSelection(new TreeItem[] { queryTab });
            break;

        /* Status Tab */
        // case 8:
        // tree.setSelection(new TreeItem[] { statusTab });
        // break;
        /* Transfer Tab */
        case 9:
            tree.setSelection(new TreeItem[] { transferTab });
            break;

        /* Services Tab */
        case 10:
            tree.setSelection(new TreeItem[] { services });
            break;

        /* Arxiv Tab */
        case 11:
            tree.setSelection(new TreeItem[] { arxiv });
            break;

        /* Citeseer Tab */
        // case 12:
        // tree.setSelection(new TreeItem[] { citeSeer });
        // break;
        /* DOI Tab */
        case 13:
            tree.setSelection(new TreeItem[] { doi });
            break;

        /* Google Tab */
        case 14:
            tree.setSelection(new TreeItem[] { google });
            break;

        /* Google Desktop Tab */
        case 24:
            tree.setSelection(new TreeItem[] { googleDesktop });
            break;

        /* Google Scholar Tab */
        case 15:
            tree.setSelection(new TreeItem[] { googleScholar });
            break;

        /* PlosJournals Tab */
        case 16:
            tree.setSelection(new TreeItem[] { plosjournals });
            break;

        /* PubMed Tab */
        case 17:
            tree.setSelection(new TreeItem[] { pubmed });
            break;

        /* PubMed Central Tab */
        case 18:
            tree.setSelection(new TreeItem[] { pubmedCentral });
            break;

        /* Scirus Tab */
        case 19:
            tree.setSelection(new TreeItem[] { scirus });
            break;

        /* Terrier Tab */
        case 20:
            tree.setSelection(new TreeItem[] { terrier });
            break;

        /* Web of Knowledge Tab */
        case 21:
            tree.setSelection(new TreeItem[] { webofknowledge });
            break;

        /* Yahoo Tab */
        case 22:
            tree.setSelection(new TreeItem[] { yahoo });
            break;

        /* Yahoo Tab */
        case 23:
            tree.setSelection(new TreeItem[] { crawler });
            break;
        }
    }

}
