package net.brokentrain.ftf.ui.gui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.brokentrain.ftf.ui.gui.GUI;

public class URLUtil {

    public static String FTF_CHANGELOG_URL;

    public static String FTF_CONTACT;

    public static String FTF_DOWNLOAD;

    public static String FTF_FAQ;

    public static String FTF_NEW_TICKET;

    public static String FTF_START;

    public static String FTF_TUTORIAL;

    public static String FTF_WEBPAGE;

    public static String PUBMED_HOME;

    static {
        FTF_CONTACT = "mailto:gavin@brokentrain.net";
        FTF_DOWNLOAD = "http://ftf.brokentrain.net/download";
        FTF_FAQ = "http://ftf.brokentrain.net/faq";
        FTF_NEW_TICKET = "http://ftf.brokentrain.net/newticket";
        FTF_START = "http://ftf.brokentrain.net/start";
        FTF_TUTORIAL = "http://ftf.brokentrain.net/tutorial";
        FTF_WEBPAGE = "http://ftf.brokentrain.net/";

        PUBMED_HOME = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=PubMed";
    }

    public static String createNewTicket(Throwable e) {

        /* Use base ticket url */
        String ticketURL = URLUtil.FTF_NEW_TICKET;

        /* Append username of current user */
        ticketURL += "?reporter="
                + URLUtil.urlEncode(System.getProperty("user.name"));

        /* Convert the exception to a query string friendly format */
        StringWriter sWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(sWriter));
        ticketURL += "&description="
                + URLUtil.urlEncode(sWriter.getBuffer().toString());

        /* TODO: Add system/version information to the query string */

        return ticketURL;
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "ISO-8859-1");
        } catch (UnsupportedEncodingException uee) {
            GUI.log.error("Encoding error!", uee);
            return str;
        }
    }

    private URLUtil() {
    }
}
