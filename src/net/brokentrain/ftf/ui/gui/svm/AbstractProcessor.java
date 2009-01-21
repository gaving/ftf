package net.brokentrain.ftf.ui.gui.svm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.io.Serializable;

import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

public class AbstractProcessor implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    /* Default stop word */
    public static final Set<String> DEFAULT_STOP_WORDS = null;

    /* Default minimum word length */
    public static final int DEFAULT_MIN_WORD_LENGTH = 0;

    /* Default maximum word length */
    public static final int DEFAULT_MAX_WORD_LENGTH = 0;

    private Set<String> stopWords = DEFAULT_STOP_WORDS;

    private int minWordLength = DEFAULT_MIN_WORD_LENGTH;

    private int maxWordLength = DEFAULT_MAX_WORD_LENGTH;

    private List<String> features;

    public AbstractProcessor() {

        features = new ArrayList<String>();

        /* Create a new set for the stop features */
        TreeSet<String> stopWords = new TreeSet<String>();

        /* Get the stop features file content */
        InputStream inputStream = FileUtil
                .getResourceAsStream("/misc/stopwords.txt");
        String stopWordsContent = FileUtil.getContent(inputStream);

        /* Add these words (separated by line) to the set */
        for (String word : stopWordsContent.split("\n")) {
            stopWords.add(word);
        }

        this.stopWords = stopWords;
    }

    public void addAbstract(String abstractText) {
        if (StringUtil.isset(abstractText)) {

            /* Words are normally split by blank spaces! */
            for (String word : abstractText.split(" ")) {

                /*
                 * Ensure that the word meets our minimum and maximum length and
                 * is not a stop word
                 */
                if (!isNoiseWord(word)) {

                    /* Add word (strip any standard punctuation) */
                    addFeature(word.replaceAll("[^a-zA-Z0-9]", ""));
                }
            }
        }
    }

    public void addFeature(String word) {

        /* Check if the features currently has this word */
        if (!features.contains(word)) {
            features.add(word);
        }
    }

    public int getFeatureCount() {
        return features.size();
    }

    public List<String> getFeatures() {
        return features;
    }

    public int getMaxWordLength() {
        return maxWordLength;
    }

    public int getMinWordLength() {
        return minWordLength;
    }

    public Set<String> getStopWords() {
        return stopWords;
    }

    private boolean isNoiseWord(String word) {
        int len = word.length();

        if (minWordLength > 0
                && ((len < minWordLength) || (len > maxWordLength))) {
            return true;
        }

        if ((stopWords != null) && (stopWords.contains(word))) {
            return true;
        }

        return false;
    }

    public void setMaxWordLength(int maxWordLength) {
        this.maxWordLength = maxWordLength;
    }

    public void setMinWordLength(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    public void setStopWords(Set<String> stopWords) {
        this.stopWords = stopWords;
    }

}
