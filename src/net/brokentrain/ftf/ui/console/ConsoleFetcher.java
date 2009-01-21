package net.brokentrain.ftf.ui.console;

import java.util.Observable;
import java.util.Observer;

import net.brokentrain.ftf.Fetcher;
import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.core.settings.SettingsManager;

/**
 * Basic console interface to the net.brokentrain.ftf. This can be created with
 * a list of parameters to locate {@link #ConsoleFetcher(String[])}.
 */
public class ConsoleFetcher implements Observer {

    /**
     * The main program, which can be executed from the command line.
     * 
     * @param argv
     *            A list of ids to locate.
     */
    public static void main(String argv[]) {

        /* Check for required parameters */
        if (argv.length < 1) {
            System.out.println("error: too few parameters");
            System.exit(1);
        }

        /*
         * Pass the arguments to the new console net.brokentrain.ftf and start
         * it
         */
        ConsoleFetcher cl = new ConsoleFetcher(argv);
        cl.start();
    }

    private String[] arguments;

    /**
     * Construct a console client with given terms.
     * 
     * @param argv
     *            The arguments to locate.
     */
    public ConsoleFetcher(String[] argv) {
        this.arguments = argv;
    }

    public void displayMatches(Dispatcher dispatcher) {

        // HashMap<String, ArrayList<Result>> resultData = resultList.getData();

        // for (String key : resultData.keySet()) {
        // for (Result result : resultData.get(key)) {
        // DataStore resultInfo = result.getData();

        // if (resultInfo instanceof URLStore) {

        // URLStore dataStore = (URLStore) resultInfo;

        // /* External (web-based) result */
        // Article foundArticle = ((Article) (dataStore.getArticle()));

        // if (foundArticle != null) {
        // System.out.println(foundArticle.toXML());
        // } else {
        // System.out.println("No metadata available: " + dataStore.getURL());
        // }

        // } else if (resultInfo instanceof FileStore) {

        // /* Local (terrier) result */
        // System.out.println(((FileStore)resultInfo));
        // } else {

        // /* Unknown result type! */
        // System.out.println(resultInfo);
        // }
        // }
        // }

    }

    /**
     * Starts the console net.brokentrain.ftf.
     */
    public void start() {

        /* Select the dispatchers we want to be searched */
        ServiceManager serviceManager = SettingsManager.getServiceManager();
        serviceManager.selectAll();

        /* Create a new 'master' dispatcher net.brokentrain.ftf */
        Fetcher ftf = new Fetcher();

        /* List of queries (strings of ids or text) */
        ftf.setQuery(this.arguments[0]);

        /*
         * Required for setRealtime(true) otherwise
         * net.brokentrain.ftf.getResultList() used
         */
        ftf.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                Dispatcher dispatcher = (Dispatcher) arg;

                /* Make sure the dispatcher is fully dead */
                if ((dispatcher.isDead()) && (dispatcher.hasResults())) {
                    displayMatches(dispatcher);
                }
            }
        });

        /* Fetch results */
        ftf.fetch();
    }

    public void update(Observable o, Object arg) {

        Dispatcher dispatcher = (Dispatcher) arg;
        String dispatcherName = dispatcher.getName();
        Dispatcher.EventType dispatcherStatus = dispatcher.getStatus();

        switch (dispatcherStatus) {
        case INIT:
            System.out.println("* " + dispatcherName + " is initialising");
            break;
        case DEAD:
            System.out.println("* " + dispatcherName + " is dead");
            break;
        case SLEEP:
            System.out.println("* " + dispatcherName + " is sleeping");
            break;
        case WORKING:
            System.out.println("* " + dispatcherName + " is working!");
            break;
        case ERROR:
            System.out
                    .println("* " + dispatcherName + " encountered an error!");
            break;
        }
    }
}
