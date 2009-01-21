package net.brokentrain.ftf.ui.gui.properties.services;

import java.util.ArrayList;

import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.DisposeListenerImpl;
import net.brokentrain.ftf.ui.gui.properties.PropertyPage;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ServicesProperties extends PropertyPage {

    private ArrayList<ServiceEntry> availableServices;

    public ServicesProperties(Composite parent) {
        super(parent);
    }

    @Override
    public void applyButtonPressed() {

        /* Work out what has been changed */
        SettingsRegistry.availableServices = availableServices;
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {

        availableServices = new ArrayList<ServiceEntry>();
        availableServices.addAll(SettingsRegistry.availableServices);

        Composite serviceHolder = new Composite(composite, SWT.NONE);
        serviceHolder.setLayout(LayoutUtil.createGridLayout(4, 10, 0, 15, 5,
                false));
        serviceHolder.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
                false, 2, 1));

        /* Create a button for every available service */
        for (Object service : availableServices) {

            final ServiceEntry serviceEntry = (ServiceEntry) service;

            final Button selectServiceButton = new Button(serviceHolder,
                    SWT.CHECK);
            selectServiceButton.setFont(dialogFont);
            selectServiceButton.addDisposeListener(DisposeListenerImpl
                    .getInstance());
            selectServiceButton.setImage(PaintUtil.getServiceIcon(serviceEntry
                    .getSection()));
            selectServiceButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    serviceEntry.toggleSelected();
                }
            });

            Label selectServiceLabel = new Label(serviceHolder, SWT.NONE);
            selectServiceLabel.setText(serviceEntry.getDescription());
            selectServiceLabel.setFont(dialogFont);

            /* Enable this service as it has been specified as a default */
            selectServiceButton.setSelection(serviceEntry.isSelected());
        }

    }

    @Override
    protected void restoreButtonPressed() {
        updatePropertiesChangeManager();
    }

    @Override
    public void updatePropertiesChangeManager() {
        propertyChangeManager.setDefaultServices(availableServices);
    }

}
