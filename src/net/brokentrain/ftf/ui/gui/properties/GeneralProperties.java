package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.swt.widgets.Composite;

public class GeneralProperties extends PropertyPage {

    public GeneralProperties(Composite parent, GUI fetcherGui) {
        super(parent, fetcherGui);
    }

    @Override
    public void applyButtonPressed() {
        updatePropertiesChangeManager();
    }

    @Override
    protected void initComponents() {
    }

    @Override
    protected void restoreButtonPressed() {
    }

    @Override
    public void updatePropertiesChangeManager() {
    }
}
