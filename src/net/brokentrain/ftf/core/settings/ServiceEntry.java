package net.brokentrain.ftf.core.settings;

import java.util.HashMap;
import java.io.Serializable;

public class ServiceEntry implements Comparable<ServiceEntry>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean enabled;

    private boolean selected;

    private HashMap<String, String> properties;

    private String controller;

    private String description;

    private String maxResults;

    private String section;

    public ServiceEntry(ServiceEntry serviceEntry) {
        controller = serviceEntry.getController();
        description = serviceEntry.getDescription();
        maxResults = serviceEntry.getMaxResults();
        section = serviceEntry.getSection();
        selected = serviceEntry.isSelected();
        enabled = serviceEntry.isEnabled();
    }

    /**
     * Construct a new service entry.
     * 
     * @param section
     *            The section header of this service.
     */
    public ServiceEntry(String section) {
        this.section = section;
    }

    /**
     * Compare services by name only.
     * 
     * @param ServiceEntry
     *            The service to compare.
     */
    public int compareTo(ServiceEntry serviceEntry) {
        return section.compareTo(serviceEntry.getSection());
    }

    /**
     * Return the controlling classname for this service.
     * 
     * @return The classname.
     */
    public String getController() {
        return controller;
    }

    /**
     * Return the description for this service.
     * 
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the desired maximum number of results to return from this service.
     * 
     * @return The maximum results to return.
     */
    public String getMaxResults() {
        return maxResults;
    }

    /**
     * Return any additional properties for this service in the configuration
     * file.
     * 
     * @return Any additional properties.
     */
    public HashMap<String, String> getProperties() {
        return properties;
    }

    /**
     * Return the section header for this service.
     * 
     * @return The section header.
     */
    public String getSection() {
        return section;
    }

    /**
     * Indicate if this service entry has any additional properties set.
     * 
     * @return True or false depending if any additional properties are set.
     */
    public boolean hasProperties() {
        return (properties.size() > 0);
    }

    /**
     * Indicate if this service entry has been enabled.
     * 
     * @return True or false depending if this service entry is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Indicate if this service entry has been enabled.
     * 
     * @return True or false depending if this service entry is enabled.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the controller name for this service.
     * 
     * @param controller
     *            The controller name.
     */
    public void setController(String controller) {
        this.controller = controller;
    }

    /**
     * Set the description of this service.
     * 
     * @param description
     *            The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Set the maximum results to be returned from this service.
     * 
     * @param maxResults
     *            The maximum results.
     */
    public void setMaxResults(String maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Set any additional properties that this service has.
     * 
     * @param properties
     *            Any additional properties.
     */
    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Set if this service is enabled or not.
     * 
     * @param enabled
     *            True or false depending on if this service is enabled.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Toggle this services enabled status.
     */
    public void toggleSelected() {
        this.selected = !this.selected;
    }

    @Override
    public String toString() {
        return section;
    }
}
