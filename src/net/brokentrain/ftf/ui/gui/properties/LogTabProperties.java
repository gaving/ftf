package net.brokentrain.ftf.ui.gui.properties;

import org.eclipse.swt.widgets.Composite;

public class LogTabProperties extends PropertyPage {

    public LogTabProperties(Composite parent) {
        super(parent);
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
