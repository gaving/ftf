package net.brokentrain.ftf.core.settings;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ServiceManager {

    private static final Logger log = Logger.getLogger(ServiceManager.class);

    private static ServiceManager instance;

    /**
     * Gets an existing Service Manager.
     * 
     * @return The existing service managers instance.
     */
    public static ServiceManager getServiceManager() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    private HashMap<String, ServiceEntry> serviceRegistry;

    private Config servicesConfig;

    /**
     * Constructs a new Service Manager.
     */
    public ServiceManager() {

        try {

            /* Load in configuration files */
            servicesConfig = new Config(Const.SERVICES_FILE);
        } catch (IOException ioe) {
            log.error("Services configuration file does not exist!", ioe);
        }

        /* No services preferences are found?! */
        if (servicesConfig == null) {
            log
                    .warn("Could not find services configuration file, none available!");
            return;
        }

        serviceRegistry = new HashMap<String, ServiceEntry>();

        /* Load the services in from the configuration file */
        readServices();
    }

    public ServiceManager(HashMap<String, ServiceEntry> serviceRegistry,
            Config servicesConfig) {
        this.servicesConfig = servicesConfig;

        this.serviceRegistry = new HashMap<String, ServiceEntry>(
                serviceRegistry);
    }

    public ServiceManager(ServiceManager serviceManager) {
        this(serviceManager.getServiceRegistry(), serviceManager
                .getServiceConfig());
    }

    /**
     * Disables all services.
     */
    public void deselectAll() {
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if (serviceEntry.isSelected()) {
                serviceEntry.setSelected(false);
            }
        }
    }

    /**
     * Disables all services.
     */
    public void disableAll() {
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if (serviceEntry.isEnabled()) {
                serviceEntry.setEnabled(false);
            }
        }
    }

    /**
     * Enables all services.
     */
    public void enableAll() {
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if (!serviceEntry.isEnabled()) {
                serviceEntry.setEnabled(true);
            }
        }
    }

    /**
     * Retrieves a list of currently available services.
     * 
     * @return A list of currently available services.
     */
    public ArrayList<ServiceEntry> getAvailableServices() {
        ArrayList<ServiceEntry> services = new ArrayList<ServiceEntry>();
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            services.add(serviceEntry);
        }
        Collections.sort(services);
        return services;
    }

    /**
     * Retrieves a list of currently enabled services.
     * 
     * @return A list of currently enabled services.
     */
    public ArrayList<ServiceEntry> getSelectedServices() {
        ArrayList<ServiceEntry> services = new ArrayList<ServiceEntry>();
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if (serviceEntry.isSelected()) {
                services.add(serviceEntry);
            }
        }
        return services;
    }

    public Config getServiceConfig() {
        return servicesConfig;
    }

    /**
     * Return a particular service entry for a given section name.
     * 
     * @param section
     *            The service section name
     * @return The service entry for this section header.
     */
    public HashMap<String, ServiceEntry> getServiceEntries() {
        return serviceRegistry;
    }

    /**
     * Return a particular service entry for a given section name.
     * 
     * @param section
     *            The service section name
     * @return The service entry for this section header.
     */
    public ServiceEntry getServiceEntry(String section) {
        return serviceRegistry.get(section);
    }

    public HashMap<String, ServiceEntry> getServiceRegistry() {
        return serviceRegistry;
    }

    /**
     * Determines if a service has any enabled services.
     * 
     * @return True or false depending on if this service has any enabled
     *         services.
     */
    public boolean hasSelectedServices() {
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if (serviceEntry.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void readServices() {

        /* Iterate over the services contained in the file */
        for (String section : servicesConfig.sections()) {

            ServiceEntry serviceEntry = new ServiceEntry(section);

            HashMap<String, String> extendedProperties = new HashMap<String, String>();

            ArrayList<String> keys = new ArrayList<String>(Arrays
                    .asList(servicesConfig.keys(section)));

            /* Make sure the service hasn't been purposely hidden */
            if (!keys.contains("hide")) {

                /* Iterate over the keys contained in this service */
                for (String key : keys) {

                    /* Get the value for this key */
                    String value = servicesConfig.get(section, key);

                    /* Set the essential data in a service entry */
                    if (key.equals("controller")) {
                        serviceEntry.setController(value);
                    } else if (key.equals("description")) {
                        serviceEntry.setDescription(value);
                    } else if (key.equals("max_results")) {
                        serviceEntry.setMaxResults(value);
                    } else if (key.equals("enabled")) {
                        serviceEntry.setSelected(Boolean.valueOf(value));
                    } else {
                        extendedProperties.put(key, value);
                    }

                }

                /* Add any extended information to the entry */
                serviceEntry.setProperties(extendedProperties);

                /* Associate this entry with the section name */
                serviceRegistry.put(section, serviceEntry);
            }
        }
    }

    /**
     * Enables all services.
     */
    public void selectAll() {
        for (ServiceEntry serviceEntry : serviceRegistry.values()) {
            if ((serviceEntry.isEnabled()) && (!serviceEntry.isSelected())) {
                serviceEntry.setSelected(true);
            }
        }
    }
}
