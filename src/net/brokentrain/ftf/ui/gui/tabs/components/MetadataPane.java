package net.brokentrain.ftf.ui.gui.tabs.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.tabs.SearchTab;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

public class MetadataPane {

    private Composite metadataArea;

    private Composite parent;

    private CTabItem tabItem;

    private Button closeButton;

    private Label imageLabel;

    private StyledText metadataLabel;

    private ArrayList<String> words;

    private ArrayList<Integer> highlightColours;

    private SearchTab queryTab;

    public MetadataPane(Composite parent, CTabItem tabItem, SearchTab searchTab) {
        this.parent = parent;
        this.tabItem = tabItem;
        this.queryTab = searchTab;

        /* Store the words we want to highlight later */
        words = new ArrayList<String>();

        highlightColours = new ArrayList<Integer>();
        highlightColours.add(SWT.COLOR_YELLOW);
        highlightColours.add(SWT.COLOR_GREEN);
        highlightColours.add(SWT.COLOR_CYAN);
        highlightColours.add(SWT.COLOR_GRAY);
        highlightColours.add(SWT.COLOR_MAGENTA);
        highlightColours.add(SWT.COLOR_RED);

        initComponents();
    }

    private void doLabelStyle(boolean highlightTerms) {

        /* Create a new list of style ranges */
        ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();

        /* Calculate the style ranges for the keys */
        styleRanges
                .addAll(WidgetUtil.calculateStyleRanges(metadataLabel, words,
                        GUI.display.getSystemColor(SWT.COLOR_BLACK),
                        GUI.display.getSystemColor(SWT.COLOR_WHITE), SWT.BOLD,
                        true, false));

        if (highlightTerms) {

            String query = queryTab.getQuery();

            int colourIndex = 0;
            for (String word : query.split(" ")) {

                if (colourIndex >= highlightColours.size()) {
                    colourIndex = 0;
                }

                /* Create a one item list */
                ArrayList<String> highlightWord = new ArrayList<String>();
                highlightWord.add(word);

                Color colour = GUI.display.getSystemColor(highlightColours
                        .get(colourIndex));
                styleRanges.addAll(WidgetUtil.calculateStyleRanges(
                        metadataLabel, highlightWord, GUI.display
                                .getSystemColor(SWT.COLOR_BLACK), colour,
                        SWT.BOLD, true, false));
                colourIndex++;
            }
        }

        /* Highlight these on the label */
        WidgetUtil.highlightText(metadataLabel, styleRanges);
    }

    public void hidePane() {
        metadataArea.setVisible(false);
        metadataArea.getParent().layout();
    }

    public void initComponents() {

        /* Core metadata area */
        metadataArea = new Composite(parent, SWT.BORDER);
        metadataArea.setLayout(LayoutUtil.createGridLayout(3, 7, 11, 4, 8,
                false));
        metadataArea.setBackground(ColourUtil.white);
        metadataArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        metadataArea.setVisible(false);

        /* Create a new label for the image */
        imageLabel = new Label(metadataArea, SWT.NULL);
        imageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_BEGINNING));
        imageLabel.setBackground(ColourUtil.white);

        /* Create a label for the metadata */
        metadataLabel = new StyledText(metadataArea, SWT.READ_ONLY | SWT.WRAP
                | SWT.V_SCROLL);
        metadataLabel.setBackground(ColourUtil.white);

        GridData data = new GridData(GridData.FILL_BOTH
                | GridData.GRAB_HORIZONTAL);
        data.widthHint = 0;
        metadataLabel.setLayoutData(data);

        metadataArea.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {

                /* Redraw the labels if we detect a resize event */
                metadataArea.layout();
            }
        });

        closeButton = new Button(metadataArea, SWT.FLAT);
        closeButton.setImage(PaintUtil.iconMetadataClose);
        closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_BEGINNING));
        closeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                hidePane();
            }
        });
    }

    public boolean isVisible() {
        return metadataArea.isVisible();
    }

    public void setPane(TreeItem selectedItem) {

        /* Read the data from the selected item */
        TreeItemData itemData = (TreeItemData) selectedItem.getData();

        if ((itemData == null) || (!itemData.isResult())) {

            /* Immediately exit and hide if there is no data for this item */
            hidePane();
            return;
        }

        /* Extract the result from the data */
        Resource resource = itemData.getResult().getResource();

        DataStore data = resource.getData();

        /* Create a list of key value strings */
        LinkedHashMap<String, String> extendedData = null;

        Article article = resource.getArticle();

        if (article != null) {

            /* An Article has key value pairs already */
            extendedData = article.getValues();
        }

        if (extendedData == null) {

            /* Automatically hide and return if no data */
            hidePane();
            return;
        }

        /* Figure out the content type */
        DataStore.ContentType contentType = data.getContentType();

        /* Set the background of the image the same background as the label */
        Image image = PaintUtil.getBigIcon(contentType);
        image.setBackground(imageLabel.getBackground());
        imageLabel.setImage(image);

        StringBuffer text = new StringBuffer("");

        /* Rough hack for 0.2rc2 */
        text.append("URI: " + itemData.getResult().getURI() + "\n");
        words.add("URI:");

        /* Build up a label for all the data available */
        for (String key : extendedData.keySet()) {

            /* Get the value for this particular key */
            String value = extendedData.get(key);
            if (StringUtil.isset(value)) {

                /* Create a key: value entry in the text label */
                String dataEntry = String.format("%s: %s\n", key, value);

                /* Add it to the rest of the text */
                text.append(dataEntry);

                /* Add key to the highlighted words list */
                words.add(key + ":");
            }
        }

        if (!StringUtil.isset(text.toString())) {

            /* Somehow we've just a bunch of blank metadata */
            hidePane();
            return;
        }

        /* Clear any previous style ranges */
        metadataLabel.setStyleRange(null);

        /* Set the panes contents */
        metadataLabel.setText(text.toString());

        doLabelStyle(SettingsRegistry.highlightTerms);

        /* Reset our sashform */
        ((SashForm) tabItem.getControl()).setMaximizedControl(null);

        /* Hide a pane that is currently opened */
        if (isVisible()) {
            hidePane();
        }

        /* Show the pane */
        showPane();
    }

    public void showPane() {
        metadataArea.setVisible(true);
        metadataArea.getParent().layout();
    }

    public void toggle() {
        metadataArea.setVisible(!metadataArea.isVisible());
        metadataArea.getParent().layout();
    }

    public void toggleHighlight() {
        doLabelStyle(SettingsRegistry.highlightTerms);
    }

}
