package net.brokentrain.ftf.core.services.lookup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;

/**
 * Responsible for looking up an Article's metadata based on ArXiv ID. Using the
 * services provided by the citebase.org web service and official arxiv.org site
 * we can obtain a direct link to an articles full-text based on it's ArXiv ID.
 */
public class ArXivLookup {

    private static final Logger log = Logger.getLogger(ArXivLookup.class);

    private URL infoString;

    private URL urlString;

    private String query;

    private ArXivArticle article;

    /**
     * Construct a new ArXiv Lookup
     */
    public ArXivLookup() {
        try {
            infoString = new URL("http://www.citebase.org/openurl?url_ver="
                    + "Z39.88-2004&svc_id=bibtex&rft_id=oai%3AarXiv.org%3A%s");
            urlString = new URL("http://arxiv.org/pdf/%s");
        } catch (MalformedURLException mue) {
            log.error(mue.getMessage(), mue);
        }
    }

    /**
     * Return the ArXiv IDs full-text link.
     * 
     * @return The full-text link for this ArXiv ID.
     */
    public String extractLink() {
        try {

            String resolveURL = urlString.toString().replace("%s", query);
            org.htmlparser.Parser.getConnectionManager()
                    .setRedirectionProcessingEnabled(false);
            org.htmlparser.Parser parser = new org.htmlparser.Parser(resolveURL);
            return parser.getURL();
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Return an associated Article structure for this result.
     * 
     * @return An article with associated metadata.
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Indicate if this lookup has yielded a result.
     * 
     * @return True or false depending if this lookup has a result.
     */
    public boolean hasResult() {
        return (article != null);
    }

    /**
     * Lookup the ArXiv ID.
     */
    @SuppressWarnings("unchecked")
    public void lookup() {

        log.info("Trying to resolve " + query);

        try {
            URL url = new URL(infoString.toString().replace("%s", query));

            BibtexFile bibtexFile = new BibtexFile();
            BibtexParser parser = new BibtexParser(false);

            article = new ArXivArticle();
            article.setId(query);
            article.setURL(extractLink());

            try {

                /* Parse the bibtex stream as it comes in */
                parser.parse(bibtexFile,
                        new InputStreamReader(url.openStream()));
                List<?> entries = bibtexFile.getEntries();
                for (Object rawEntry : entries) {

                    BibtexEntry entry = (BibtexEntry) rawEntry;

                    Map<String, BibtexAbstractValue> bibtexData = entry
                            .getFields();

                    /* Populate our article object as we read the data */
                    for (String key : bibtexData.keySet()) {
                        String bibtexValue = bibtexData.get(key).toString();
                        if (key.equals("author")) {
                            article.setAuthor(bibtexValue);
                        } else if (key.equals("title")) {
                            article.setArticleTitle(bibtexValue);
                        } else if (key.equals("journal")) {
                            article.setJournalTitle(bibtexValue);
                        } else if (key.equals("volume")) {
                            article.setVolume(bibtexValue);
                        } else if (key.equals("year")) {
                            article.setYear(bibtexValue);
                        }
                    }
                }
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            } catch (ParseException pe) {
                log.error(pe.getMessage(), pe);
            }
        } catch (MalformedURLException mue) {
            log.error(mue.getMessage(), mue);
        }
    }

    /**
     * Set a new ArXiv ID to lookup.
     * 
     * @param query
     *            The new ArXiv ID to lookup.
     */
    public void setQuery(String query) {
        try {
            if (query.toLowerCase().startsWith("arxiv:")) {
                query = query.substring(6, query.length());
            }
            this.query = URLEncoder.encode(query, "ISO-8859-1");
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }
    }

}
