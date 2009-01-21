package net.brokentrain.ftf;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.services.ExecutionTimer;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.thread.ExtendedThread;

import org.apache.log4j.Logger;

/**
 * Responsible for providing a link between the model and the view of the
 * software. A Fetcher object is responsible for providing a consistent
 * interface for any application wishing to use the core functionality of the
 * system. A Fetcher should be given a query {@link #Fetcher(String)} or set
 * manually {@link #Fetcher()} with {@link #setQueries setQuery(String)}. Any
 * application wishing to use the Fetcher object must do so by subscribing to it
 * via an Observer interface to receive events as they are issued.
 * 
 * @see Dispatcher
 * @see ServiceManager
 * @see java.util.Observer
 */
public class Fetcher extends Observable implements Observer {

    private static final Logger log = Logger.getLogger(Fetcher.class);

    private ExecutionTimer timer;

    private ServiceManager serviceManager;

    private String query;

    private boolean executing;

    private boolean isProcessing;

    private HashMap<Dispatcher, ExtendedThread> runningDispatchers;

    /**
     * Construct a Fetcher.
     */
    public Fetcher() {

        /* Initialize the dispatchers */
        log.info("Initialising dispatchers");

        timer = new ExecutionTimer();
        serviceManager = SettingsManager.getServiceManager();
    }

    /**
     * Construct a Fetcher using provided query.
     * 
     * @param query
     *            The search terms
     */
    public Fetcher(String query) {

        /* Initialize the dispatchers */
        log.info("Initialising dispatchers");

        this.query = query;
        executing = false;
        isProcessing = false;

        timer = new ExecutionTimer();
        serviceManager = SettingsManager.getServiceManager();

        runningDispatchers = new HashMap<Dispatcher, ExtendedThread>();
    }

    /**
     * Start the fetch process.
     */
    public void fetch() {

        /* Start timing */
        timer.start();

        runningDispatchers = new HashMap<Dispatcher, ExtendedThread>();

        /* Create a new thread for each dispatcher available from our config */
        for (ServiceEntry service : serviceManager.getSelectedServices()) {

            log.info("Starting " + service + " crawler.");

            Dispatcher dispatcher = new Dispatcher(service, query);

            dispatcher.setProcessingAbstract(isProcessing);

            /* Observe events from this dispatcher */
            dispatcher.addObserver(this);

            /* Create new thread for the dispatcher */
            ExtendedThread dispatcherThread = new ExtendedThread(dispatcher);

            /* Daemonize the thread */
            dispatcherThread.setDaemon(true);

            /* Store the thread */
            runningDispatchers.put(dispatcher, dispatcherThread);

            /* Start fetching immediately */
            dispatcherThread.startThread();
        }

        this.executing = true;

        /* Stop timing */
        timer.stop();
    }

    public void fetch(List<Dispatcher> dispatchers) {

        /* Start timing */
        timer.start();

        runningDispatchers = new HashMap<Dispatcher, ExtendedThread>();

        /* Create a new thread for each dispatcher available from our config */
        for (Dispatcher dispatcher : dispatchers) {

            log.info("Restarting " + dispatcher);

            dispatcher.incrementSearch();

            /* Observe events from this dispatcher */
            dispatcher.addObserver(this);

            /* Create new thread for the dispatcher */
            ExtendedThread dispatcherThread = new ExtendedThread(dispatcher);

            /* Daemonize the thread */
            dispatcherThread.setDaemon(true);

            /* Store the thread */
            runningDispatchers.put(dispatcher, dispatcherThread);

            /* Start fetching immediately */
            dispatcherThread.startThread();
        }

        this.executing = true;

        /* Stop timing */
        timer.stop();
    }

    /**
     * Return the total fetch time.
     * 
     * @return A string representation in milliseconds.
     */
    public String getTotalFetchTime() {
        return timer.toValue() + "ms";
    }

    /**
     * Determines if the fetcher is currently fetching.
     * 
     * @return True or false depending if it is active.
     */
    public boolean isFetching() {
        return executing;
    }

    public void setProcessingAbstract(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }

    /**
     * Sets a query to fetch.
     * 
     * @param query
     *            The query to search for.
     */
    public void setQuery(String query) {
        this.query = query;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Stop and ongoing fetcher.
     */
    public void stop() {

        for (Dispatcher dispatcher : runningDispatchers.keySet()) {

            ExtendedThread runningDispatcher = runningDispatchers
                    .get(dispatcher);

            /* Check the dispatcher is actually alive */
            if (runningDispatcher.isAlive()) {
                log.info("Killing " + dispatcher.getName());

                /* Stop the thread */
                runningDispatcher.stopThread();
            }
        }

        this.executing = false;

    }

    /**
     * Receives Dispatcher updates and notifies any observers.
     * 
     * @param o
     *            The observed Dispatcher object.
     * @param arg
     *            Any associated data returned from the Dispatcher.
     */
    public void update(Observable o, Object arg) {

        /* Indicate that the fetcher object as changed */
        setChanged();

        /* Notify people watching for results */
        notifyObservers(o);
    }

}
