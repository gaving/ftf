package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class QueryTabProperties extends PropertyPage {

    private Button showMetadataPane;

    private Button highlightTerms;

    private Button showTooltip;

    private Button glimpse;

    private Button investigate;

    public QueryTabProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {
        SettingsRegistry.showMetadataPane = showMetadataPane.getSelection();
        SettingsRegistry.highlightTerms = highlightTerms.getSelection();
        SettingsRegistry.showTooltip = showTooltip.getSelection();
        SettingsRegistry.glimpse = glimpse.getSelection();
        SettingsRegistry.investigate = investigate.getSelection();

        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        Group tabGeneralGroup = new Group(composite, SWT.NONE);
        tabGeneralGroup.setText("Metadata Pane");
        tabGeneralGroup.setLayout(new GridLayout(2, false));
        tabGeneralGroup.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        tabGeneralGroup.setFont(FontUtil.dialogFont);

        showMetadataPane = new Button(tabGeneralGroup, SWT.CHECK);
        showMetadataPane.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        showMetadataPane.setText("Show the metadata pane");
        showMetadataPane.setSelection(propertyChangeManager
                .isShowMetadataPane());
        showMetadataPane.setFont(dialogFont);

        highlightTerms = new Button(tabGeneralGroup, SWT.CHECK);
        highlightTerms.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        highlightTerms.setText("Highlight search terms");
        highlightTerms
                .setSelection(propertyChangeManager.isHighlightingTerms());
        highlightTerms.setFont(dialogFont);

        showTooltip = new Button(tabGeneralGroup, SWT.CHECK);
        showTooltip.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        showTooltip.setText("Show information tooltip");
        showTooltip.setSelection(propertyChangeManager.isShowTooltip());
        showTooltip.setFont(dialogFont);

        Group miscGroup = new Group(composite, SWT.NONE);
        miscGroup.setText("Results");
        miscGroup.setLayout(new GridLayout(2, false));
        miscGroup.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        miscGroup.setFont(FontUtil.dialogFont);

        glimpse = new Button(miscGroup, SWT.CHECK);
        glimpse.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        glimpse.setText("Read information about each result (slow)");
        glimpse.setSelection(propertyChangeManager.isGlimpsing());
        glimpse.setFont(dialogFont);

        investigate = new Button(miscGroup, SWT.CHECK);
        investigate.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 2));
        investigate.setText("Always attempt to find full-text");
        investigate.setSelection(propertyChangeManager.isInvestigating());
        investigate.setFont(dialogFont);
    }

    @Override
    protected void restoreButtonPressed() {
        showMetadataPane.setSelection(true);
        highlightTerms.setSelection(true);
        showTooltip.setSelection(true);
        glimpse.setSelection(false);
        investigate.setSelection(false);
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setShowMetadataPane(showMetadataPane
                .getSelection());
        propertyChangeManager.setHighlightTerms(highlightTerms.getSelection());
        propertyChangeManager.setShowTooltip(showTooltip.getSelection());
        propertyChangeManager.setGlimpse(glimpse.getSelection());
    }

}
