package net.brokentrain.ftf.ui.gui.tabs.components;

import java.util.ArrayList;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.tabs.SearchTab;

public class ServiceTracker {

    private SearchTab searchTab;

    private ArrayList<ServiceEntry> serviceRegistry;

    private boolean hasResults;

    public ServiceTracker(SearchTab searchTab) {
        this.searchTab = searchTab;
    }

    public void handleService(Dispatcher dispatcher) {

        /* Ensure that we are waiting for dispatchers to return */
        if (isWaiting()) {

            /* Confirm the arrival of the dispatcher with the UI */
            searchTab.confirmArrival(dispatcher.getName());

            /* Check if the dispatcher has any results */
            if (dispatcher.hasResults()) {

                GUI.log.debug(dispatcher.getName()
                        + " coming back with results");

                /* Show the results in the UI */
                searchTab.showResults(dispatcher);

                /* Indicate we have received results before */
                hasResults = true;
            } else {

                /*
                 * Show no results message if last dispatcher and never had
                 * results
                 */
                if ((isLast(dispatcher.getServiceEntry())) && (!hasResults)) {
                    searchTab.showEmptyResults(dispatcher);
                    GUI.log.debug("No services returned any results!");
                } else {
                    GUI.log.debug(dispatcher.getName()
                            + " returning with no results");
                }
            }

            /* Reset the interface if it is the last one */
            if (isLast(dispatcher.getServiceEntry())) {
                searchTab.setBusy(false);
            }

            serviceRegistry.remove(dispatcher.getServiceEntry());
        } else {
            GUI.log.debug("Results returned that are not expected.");
        }
    }

    public boolean isLast(ServiceEntry lastService) {

        if (serviceRegistry.size() == 1) {

            String serviceController = (serviceRegistry.get(0)).getController();
            String lastServiceController = lastService.getController();

            return serviceController.equals(lastServiceController);
        } else {
            return false;
        }
    }

    public boolean isWaiting() {
        return (serviceRegistry.size() > 0);
    }

    public void setSourceRegistry(ArrayList<ServiceEntry> serviceRegistry) {
        this.serviceRegistry = serviceRegistry;

        hasResults = false;
    }

}
