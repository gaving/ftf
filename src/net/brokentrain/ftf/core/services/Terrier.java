package net.brokentrain.ftf.core.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.FileData;
import net.brokentrain.ftf.core.services.lookup.FileArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;

import uk.ac.gla.terrier.matching.ResultSet;
import uk.ac.gla.terrier.querying.Manager;
import uk.ac.gla.terrier.querying.SearchRequest;
import uk.ac.gla.terrier.querying.parser.TerrierFloatLexer;
import uk.ac.gla.terrier.querying.parser.TerrierLexer;
import uk.ac.gla.terrier.querying.parser.TerrierQueryParser;
import uk.ac.gla.terrier.structures.Index;
import uk.ac.gla.terrier.utility.ApplicationSetup;
import antlr.TokenStreamSelector;

/**
 * Implements the Terrier file service. A search can be created with no
 * parameters {@link #Terrier()} which can be set later or created immediately
 * {@link #Terrier(String, int)} and started.
 */
public class Terrier implements SearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected SearchService create() {
            return new Terrier();
        }
    }

    private static final Logger log = Logger.getLogger(Terrier.class);

    static {
        ServiceFactory.addFactory("Terrier", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    private Index index;

    private Manager manager;

    private final String mModel = "Matching";

    private String terrierHome;

    private String wModel;

    /**
     * Construct a new Terrier Search.
     */
    public Terrier() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        wModel = "PL2";
    }

    private boolean createIndex() {
        index = Index.createIndex();
        if (index != null) {
            manager = new Manager(index);
            return true;
        }
        return false;
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        File terrierDirectory = new File(terrierHome);

        if ((terrierHome == null) || (!terrierDirectory.exists())) {
            log.error("The terrier service is not properly configured!");
            return;
        }

        if (!createIndex()) {
            log
                    .error("Couldn't locate or create indexes, service misconfigured?");
            return;
        }

        SearchRequest srq = manager.newSearchRequest();

        try {
            TerrierLexer lexer = new TerrierLexer(new StringReader(query));
            TerrierFloatLexer flexer = new TerrierFloatLexer(lexer
                    .getInputState());

            TokenStreamSelector selector = new TokenStreamSelector();
            selector.addInputStream(lexer, "main");
            selector.addInputStream(flexer, "numbers");
            selector.select("main");

            TerrierQueryParser parser = new TerrierQueryParser(selector);
            parser.setSelector(selector);
            srq.setQuery(parser.query());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

        srq.addMatchingModel(mModel, wModel);
        srq.setControl("c", "1.0d");

        manager.runPreProcessing(srq);
        manager.runMatching(srq);
        manager.runPostProcessing(srq);
        manager.runPostFilters(srq);

        ResultSet rs = srq.getResultSet();

        ArrayList<String> fileList = getFileList(new File(ApplicationSetup
                .makeAbsolute(ApplicationSetup.getProperty(
                        "desktop.directories.filelist", "data.filelist"),
                        ApplicationSetup.TERRIER_INDEX_PATH)));

        int ResultsSize = rs.getResultSize();
        int[] docids = rs.getDocids();
        int resultCount = 0;

        for (int i = 0; i < ResultsSize; i++) {

            File file = new File(fileList.get(docids[i]));

            if (file == null) {
                continue;
            }

            if (resultCount >= maxResults) {
                log.info("Limiting matches..");
                break;
            }

            log.info("Local result found: " + file);

            Resource resource = new Resource();
            resource.setArticle(new FileArticle());
            resource.setData(new FileData(file));

            /* Add to total results with extended data */
            results.put(file.toURI(), resource);
            resultCount++;
        }

        index.close();

        /* Stop timing */
        timer.stop();
    }

    private ArrayList<String> getFileList(File file) {
        if ((file == null) || !file.exists()) {
            return new ArrayList<String>();
        }
        ArrayList<String> out = new ArrayList<String>();
        try {
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = buf.readLine()) != null) {
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                out.add(line.trim());
            }
            buf.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return out;
    }

    /**
     * Return all search metadata.
     * 
     * @return A collection of metadata.
     */
    public HashMap<URI, Resource> getResults() {
        return results;
    }

    /**
     * Returns how long the search took in milliseconds.
     * 
     * @return The time in milliseconds.
     */
    public long getSearchTime() {
        return timer.toValue();
    }

    /**
     * Returns the total number of results obtained by the search.
     * 
     * @return The total number of results.
     */
    public Integer getTotalResults() {
        return results.size();
    }

    /**
     * Returns if the current net.brokentrain.ftf contains any results.
     * 
     * @return True if the search returned any results, false otherwise.
     */
    public boolean hasResults() {
        return results.size() > 0;
    }

    public void setData(ServiceEntry serviceEntry) {

        /* Set terrier home */
        log.debug("Setting terrier home");

        terrierHome = serviceEntry.getProperties().get("terrier_home");

        Properties sysProps = System.getProperties();
        sysProps.put("terrier.home", terrierHome);
        System.setProperties(sysProps);

        wModel = serviceEntry.getProperties().get("weight_model");
    }

    /**
     * Set if this service should glimpse at a URL for data.
     * 
     * @param glimpse
     *            True or False depending on if it should glimpse.
     */
    public void setGlimpse(boolean glimpse) {
    }

    /**
     * Set a maximum number of results to return.
     * 
     * @param maxResults
     *            The maximum results to return.
     */
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        this.query = query;
    }

}
