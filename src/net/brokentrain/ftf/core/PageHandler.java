package net.brokentrain.ftf.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.URLArticle;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

public class PageHandler {

    private static final Logger log = Logger.getLogger(PageHandler.class);

    private Parser parser;

    private String name;

    private URL url;

    private ArrayList<Result> results;

    private SettingsManager settingsManager;

    public PageHandler() {
        this.results = new ArrayList<Result>();

        settingsManager = SettingsManager.getSettingsManager();
    }

    /**
     * Add a result to the result collection.
     * 
     * @param url
     *            A url to add.
     */
    public void addResult(String url) {
        try {
            URI resultURI = new URI(url);

            Result result = new Result();
            result.setURI(resultURI);

            Resource resource = new Resource();
            resource.setArticle(new URLArticle());
            resource.setData(extractDataFromLink(resultURI.toURL()));

            result.setResource(resource);
            results.add(result);
        } catch (MalformedURLException mue) {
            log.error(getName() + ": " + mue.getMessage(), mue);
        } catch (URISyntaxException use) {
            log.error(getName() + ": " + use.getMessage(), use);
        }
    }

    private boolean checkBlackList(String match) {

        /* Check the match against our defined blacklist */
        for (Pattern pattern : settingsManager.getBlackList()) {

            /* Apply the expression of the current page */
            Matcher matcher = pattern.matcher(match);

            /* Check for any matches */
            return (matcher.find());
        }

        return false;
    }

    /**
     * Start investigating the given resource for potential links
     */
    public void doInvestigate() {

        if ((url != null) && (parser != null)) {

            /* Reset the parser so that we begin from the top */
            parser.reset();

            /* Fetch any potential links */
            fetch();
        }
    }

    private URLData extractDataFromLink(URL target) {
        return new URLData(target, true);
    }

    private void extractLinksFromMeta(ArrayList<String> matches) {
        try {

            /* Reset the parser after searching above */
            parser.reset();

            NodeFilter metaFilter = new TagNameFilter("META");
            NodeFilter metaAndNameFilter = new AndFilter(metaFilter,
                    new HasAttributeFilter("name"));
            NodeList nodeList = parser
                    .extractAllNodesThatMatch(metaAndNameFilter);

            String url = null;
            boolean foundMetaLink = false;
            URLArticle article = new URLArticle();

            /* Loop through nodes and add matching links to list */
            for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {

                TagNode currentNode = (TagNode) e.nextNode();
                String tagName = currentNode.getAttribute("name");

                if (StringUtil.isset(tagName)) {

                    String tagContent = currentNode.getAttribute("content");

                    if (tagName.equals("citation_pdf_url")) {
                        log.debug(getName()
                                + ": Hit for content metatag, adding: "
                                + tagContent);
                        url = Translate.decode(tagContent);
                        foundMetaLink = true;
                    } else if (tagName.equals("citation_authors")) {
                        article.setAuthor(tagContent);
                    } else if (tagName.equals("citation_title")) {
                        article.setArticleTitle(tagContent);
                    } else if (tagName.equals("citation_journal_title")) {
                        article.setJournalTitle(tagContent);
                    } else if (tagName.equals("citation_date")) {
                        article.setYear(tagContent);
                    } else if (tagName.equals("citation_volume")) {
                        article.setVolume(tagContent);
                    } else if (tagName.equals("citation_issue")) {
                        article.setIssue(tagContent);
                    }
                }
            }

            /*
             * The metalink data that exists at the top of some sites is
             * generally very useful, and we can assume that this is decent
             * metadata to use.
             */
            if (foundMetaLink) {

                if (StringUtil.isset(url)) {

                    /* Add the match if it is well formed */
                    matches.add(url);
                }

            }
        } catch (ParserException pe) {
            log.error(getName() + ": " + pe.getMessage(), pe);
        }
    }

    private void extractLinksFromPatterns(ArrayList<String> matches) {
        try {

            /* Reset the parser after searching above */
            parser.reset();

            /* Use no filter */
            NodeList nodematches = parser.parse(null);
            String body = nodematches.toHtml();

            /* Fall back to trying the compiled patterns */
            for (Pattern pattern : settingsManager.getPatterns()) {

                /* Apply the expression of the current page */
                Matcher matcher = pattern.matcher(body);

                /* Check for any matches */
                if (matcher.find()) {

                    /* Loop over matches and add them to the matches */
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        log.debug(getName() + ": Hit for a regexp, adding: "
                                + matcher.group(i));
                        matches.add(matcher.group(i));
                    }
                }
            }
        } catch (ParserException pe) {
            log.error(getName() + ": " + pe.getMessage(), pe);
        }
    }

    private void extractLinksFromTokens(ArrayList<String> matches, int number) {

        /* Reset the parser after searching above */
        parser.reset();

        ArrayList<NodeFilter> tokenList = settingsManager.getTokens();

        NodeFilter orFilter = new OrFilter(tokenList
                .toArray(new NodeFilter[tokenList.size()]));

        try {
            NodeList fallbackNodeList = parser
                    .extractAllNodesThatMatch(orFilter);

            int resultCount = 0;

            /* Loop through nodes and add matching links to list */
            for (NodeIterator e = fallbackNodeList.elements(); e.hasMoreNodes();) {

                if (resultCount >= number) {
                    log.info("Limiting matches..");
                    break;
                }

                TagNode currentNode = (TagNode) e.nextNode();

                /* Strip out the URL of the link */
                String link = currentNode.getAttribute("href");

                if (StringUtil.isset(link)) {
                    log.debug(getName() + ": Hit for string token, adding: "
                            + link);
                    matches.add(Translate.decode(link));
                    resultCount++;
                }
            }
        } catch (ParserException pe) {
            log.error(getName() + ": " + url + " " + pe.getMessage(), pe);
        }
    }

    private void fetch() {

        /* Keep a record of any possible matches */
        ArrayList<String> matches = new ArrayList<String>();
        String contentType = parser.getConnection().getContentType();

        /* Automatically add PDFs to the results */
        if (contentType.equals("application/pdf")) {
            log.info(getName() + " : Detected PDF! Ignoring..");
        } else {

            log.info(getName() + ": Checking the meta tag information.");
            extractLinksFromMeta(matches);

            if (matches.isEmpty()) {
                log.info(getName() + ": Attempting custom string token search");
                extractLinksFromTokens(matches, 1);

                if (matches.isEmpty()) {
                    log.info(getName() + ": Trying regular expressions");
                    extractLinksFromPatterns(matches);
                }
            }
        }

        if (!matches.isEmpty()) {

            filter(matches);

            for (String url : matches) {
                log.info(getName() + ": Adding as result: " + url);
                addResult(Translate.decode(url));
            }
        } else {
            log.debug(getName() + ": Nothing found!");
        }
    }

    private void filter(ArrayList<String> matches) {
        log.debug(getName() + ": Filtering matches with " + matches.size()
                + " matches.");

        /* Filter out duplicate items */
        HashSet<String> hashList = new HashSet<String>(matches);

        /* Clear the target matches */
        matches.clear();

        for (String match : hashList) {

            if (checkBlackList(match)) {
                log.warn(getName() + ": Hit for a blacklist entry, ignoring: "
                        + match);

                /* Don't process this link at all */
                continue;
            }

            if (!match.startsWith("http://")) {

                if (match.startsWith("/")) {

                    /* Uh, link is referring relatively! */
                    match = getCurrentHostname().concat(match);
                } else if (match.startsWith("..")) {

                    /* Urgh, work out the path from this */
                    log
                            .warn(getName()
                                    + ": Strange path for match returned, reconstructing it");

                    /* Hope that the browser can figure it out */
                    match = getCurrentHostname().concat(
                            getCurrentPath().concat(match));
                } else {
                    match = getCurrentHostname().concat(
                            getCurrentPath().concat(match));
                }
            }
            matches.add(match);
        }
    }

    /**
     * Return the hostname for the current URL.
     * 
     * @return The hostname for the current URL.
     */
    public String getCurrentHostname() {
        return url.getProtocol() + "://" + url.getHost();
    }

    /**
     * Return the current path of the URL that is being looked at.
     * 
     * @return The current path of the URL.
     */
    public String getCurrentPath() {
        int lastSlash = url.getPath().lastIndexOf('/');
        String path = url.getPath().substring(0, lastSlash);
        return path + "/";
    }

    /**
     * Return the current URL this PageHandler is actively processing.
     * 
     * @return The current URL of the page being processed.
     */
    public URL getCurrentURL() {
        return url;
    }

    /**
     * Return the name of the service this PageHandler is handling.
     * 
     * @return The name of the service.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the results found by this PageHandler.
     * 
     * @return A list of results found by this PageHandler.
     */
    public ArrayList<Result> getResults() {
        return results;
    }

    /**
     * Set the name of the service from which this URL is from.
     * 
     * @param name
     *            The name of the service.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the URL and associated Parser of the resource to process.
     * 
     * @param url
     *            The resource to process.
     */
    public void setURL(URL url) {
        this.url = url;
        try {
            DefaultParserFeedback defaultFeedback = new DefaultParserFeedback(
                    DefaultParserFeedback.QUIET);
            parser = new Parser(url.toString(), defaultFeedback);
        } catch (ParserException pe) {
            log.error(getName() + ": " + url + " " + pe.getMessage(), pe);
        }
    }
}
