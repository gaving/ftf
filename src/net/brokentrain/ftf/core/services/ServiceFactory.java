package net.brokentrain.ftf.core.services;

import java.util.HashMap;

/**
 * The main service manager. This is responsible for 'registering' and managing
 * different services which provided full-text matches.
 */
public abstract class ServiceFactory {

    private static final HashMap<String, ServiceFactory> factories = new HashMap<String, ServiceFactory>();

    /**
     * Add a new factory to the currently maintained pool.
     * 
     * @param id
     *            The service name (specific class name).
     * @param s
     *            A given factory instance.
     */
    public static void addFactory(String id, ServiceFactory s) {
        factories.put(id, s);
    }

    /**
     * Accepts a new service and returns the object that matches for a given
     * identifier.
     * 
     * @param id
     *            The service name (specific class name).
     * @return The related service.
     */
    public static final SearchService createService(String id) {

        if (!factories.containsKey(id)) {

            try {
                Class.forName("net.brokentrain.ftf.core.services." + id);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                        "Couldn't find controller for service: " + id);
            }
        }

        return (factories.get(id)).create();

    }

    /**
     * Create a new Service factory.
     */
    protected abstract SearchService create();

}
