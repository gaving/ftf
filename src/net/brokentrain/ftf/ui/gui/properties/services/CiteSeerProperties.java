package net.brokentrain.ftf.ui.gui.properties.services;

import java.util.HashMap;

import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.properties.PropertyPage;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class CiteSeerProperties extends PropertyPage {

    private static final int MIN_RESULTS = 1;

    private static final int MAX_RESULTS = 10;

    private static final String SERVICE_ENTRY = "CiteSeer";

    private HashMap<String, ServiceEntry> services;

    private ServiceEntry serviceEntry;

    private HashMap<String, Text> extendedFields;

    private Text textController;

    private Text textDescription;

    private Spinner spinnerMaxResults;

    public CiteSeerProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {

        /* Set the description */
        serviceEntry.setDescription(textDescription.getText());

        /* Set the max results */
        serviceEntry.setMaxResults(String.valueOf(spinnerMaxResults
                .getSelection()));

        /* Set the extended properties */
        HashMap<String, String> extendedProperties = serviceEntry
                .getProperties();

        /* Iterate over the fields */
        for (String extendedField : extendedFields.keySet()) {

            /* Get the field from this particular key */
            Text field = extendedFields.get(extendedField);

            if (WidgetUtil.isset(field)) {

                String value = field.getText();

                /* Check there is actually something in the field */
                if (StringUtil.isset(value)) {

                    /* Add to properties */
                    extendedProperties.put(extendedField, value);
                }
            }

        }

        /* Add any extended information to the entry */
        serviceEntry.setProperties(extendedProperties);

        /* Register with this service entry */
        services.put(CiteSeerProperties.SERVICE_ENTRY, serviceEntry);

        /* Update the properties finally */
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        extendedFields = new HashMap<String, Text>();

        services = new HashMap<String, ServiceEntry>();
        services.putAll(SettingsRegistry.services);
        serviceEntry = services.get(CiteSeerProperties.SERVICE_ENTRY);

        Group generalGroup = new Group(composite, SWT.NONE);
        generalGroup.setLayoutData(LayoutDataUtil
                .createGridData(GridData.VERTICAL_ALIGN_BEGINNING
                        | GridData.FILL_HORIZONTAL, 2));
        generalGroup.setText("General");
        generalGroup.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
        generalGroup.setFont(FontUtil.dialogFont);

        Label labelController = new Label(generalGroup, SWT.NONE);
        labelController.setText("Controller: ");
        labelController.setFont(dialogFont);
        labelController.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_CENTER
                        | GridData.HORIZONTAL_ALIGN_BEGINNING));

        textController = new Text(generalGroup, SWT.BORDER | SWT.READ_ONLY);
        textController.setFont(dialogFont);
        textController.setText(serviceEntry.getController());
        textController.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(textController);

        Label labelDescription = new Label(generalGroup, SWT.NONE);
        labelDescription.setText("Description: ");
        labelDescription.setFont(dialogFont);
        labelDescription.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_CENTER
                        | GridData.HORIZONTAL_ALIGN_BEGINNING));

        textDescription = new Text(generalGroup, SWT.BORDER);
        textDescription.setFont(dialogFont);
        textDescription.setText(serviceEntry.getDescription());
        textDescription.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(textDescription);

        Label labelMaxResults = new Label(generalGroup, SWT.NONE);
        labelMaxResults.setText("Maximum Results: ");
        labelMaxResults.setFont(dialogFont);
        labelMaxResults.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_CENTER
                        | GridData.HORIZONTAL_ALIGN_BEGINNING));

        spinnerMaxResults = new Spinner(generalGroup, SWT.BORDER);
        spinnerMaxResults.setMinimum(MIN_RESULTS);
        spinnerMaxResults.setMaximum(MAX_RESULTS);
        spinnerMaxResults.setSelection(Integer.valueOf(serviceEntry
                .getMaxResults()));
        spinnerMaxResults.setFont(FontUtil.dialogFont);
        spinnerMaxResults.setLayoutData(LayoutDataUtil.createGridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING, 1));

        if ((serviceEntry.hasProperties())
                && (serviceEntry.getProperties() != null)) {

            HashMap<String, String> extendedProperties = serviceEntry
                    .getProperties();

            Group extendedGroup = new Group(composite, SWT.NONE);
            extendedGroup.setLayoutData(LayoutDataUtil.createGridData(
                    GridData.VERTICAL_ALIGN_BEGINNING
                            | GridData.FILL_HORIZONTAL, 2));
            extendedGroup.setText("Extended");
            extendedGroup.setLayout(LayoutUtil.createGridLayout(2, 5, 5, 10));
            extendedGroup.setFont(FontUtil.dialogFont);

            /* Iterate over the keys contained in this service */
            for (String key : extendedProperties.keySet()) {

                /* Get the value for this key */
                String value = extendedProperties.get(key);

                Label labelExtendedItem = new Label(extendedGroup, SWT.NONE);
                labelExtendedItem.setText(StringUtil.createPropertyName(key));
                labelExtendedItem.setFont(dialogFont);
                labelExtendedItem.setLayoutData(new GridData(
                        GridData.VERTICAL_ALIGN_CENTER
                                | GridData.HORIZONTAL_ALIGN_BEGINNING));

                Text textExtendedItem = new Text(extendedGroup, SWT.BORDER);
                textExtendedItem.setFont(dialogFont);
                textExtendedItem.setText(value);
                textExtendedItem.setLayoutData(new GridData(
                        GridData.VERTICAL_ALIGN_BEGINNING
                                | GridData.FILL_HORIZONTAL));

                WidgetUtil.tweakTextWidget(textExtendedItem);

                extendedFields.put(key, textExtendedItem);
            }
        }
    }

    @Override
    protected void restoreButtonPressed() {
        // textDescription.setText(origServiceEntry.getDescription());
        // spinnerMaxResults.setSelection(origServiceEntry.getMaxResults());
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setServices(services);
    }

}
