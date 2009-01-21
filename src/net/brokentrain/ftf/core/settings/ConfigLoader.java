package net.brokentrain.ftf.core.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Responsible for loading a configuration file into memory and allowing certain
 * functionality such as saving changes. The ConfigLoader provides a constructor
 * that takes a {@link #ConfigLoader(String)}. A configuration file can both be
 * saved with the {@link #save save()} method and similarly loaded with
 * {@link #load load()}
 */
public class ConfigLoader {

    /**
     * The filename of the loaded configuration.
     */
    protected String confFile;

    /**
     * The in-memory representation of a configuration file.
     */
    protected Hashtable<String, Hashtable<String, String>> config;

    /**
     * Construct a new loader with a specific filename.
     * 
     * @param file
     *            The filename from which to construct a new loader.
     */
    public ConfigLoader(String file) {
        confFile = file;
    }

    /**
     * Loads a configuration file into memory.
     * 
     * @return A hash table representing the key value pairs.
     * @exception IOException
     *                if no file is specified for loading.
     */
    public Hashtable<String, Hashtable<String, String>> load()
            throws IOException {
        if ((confFile == null) || (!new File(confFile).exists())) {
            throw new IOException("Problems opening file!");
        }
        readFile();
        return config;
    }

    /**
     * Loads a configuration file into memory.
     * 
     * @param file
     *            Filename to load in.
     * @return A hash table representing the key value pairs.
     * @exception IOException
     *                if the file does not exist.
     */
    public Hashtable<String, Hashtable<String, String>> load(String file)
            throws IOException {
        confFile = file;
        return load();
    }

    private void readFile() throws IOException {
        config = new Hashtable<String, Hashtable<String, String>>();
        BufferedReader reader = new BufferedReader(new FileReader(confFile));
        String line = null;
        String currSection = null;

        while ((line = reader.readLine()) != null) {
            if (line.indexOf("#") >= 0) {
                line = line.substring(0, line.indexOf("#"));
            }
            line = line.trim();
            if (!line.equals("")) {
                if (line.startsWith("[") && line.endsWith("]")) {
                    currSection = line.substring(1);
                    currSection = currSection.substring(0, currSection
                            .indexOf(']'));
                    if (!config.containsKey(currSection)) {
                        config
                                .put(currSection,
                                        new Hashtable<String, String>());
                    }
                } else {
                    if (currSection == null) {
                        throw new IOException("Non empty line (" + line
                                + ") occurred outside of section delimiter");
                    }
                    if (line.indexOf('=') <= 0) {
                        throw new IOException("Malformed key=val pair (" + line
                                + ")");
                    }
                    String key = line.substring(0, line.indexOf('='));
                    String val = line.substring(line.indexOf('=') + 1);
                    (config.get(currSection)).put(key, val);
                }
            }
        }
    }

    /**
     * Save the current configuration file.
     * 
     * @exception IOException
     *                if errors occur during saving.
     */
    public void save() throws IOException {
        if ((confFile == null) || (!new File(confFile).exists())) {
            throw new IOException("Problems opening file!");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(confFile));
        Enumeration<String> sections = config.keys();
        while (sections.hasMoreElements()) {
            String sectionName = sections.nextElement();
            writer.write("[" + sectionName + "]\n");
            Hashtable<String, String> section = config.get(sectionName);
            Enumeration<String> keys = section.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String val = section.get(key);
                writer.write(key + "=" + val + "\n");
            }
        }
        writer.close();
    }

    /**
     * Save the current configuration file.
     * 
     * @param file
     *            The filename to save the configuration file as.
     * @exception IOException
     *                if errors occur during saving.
     */
    public void save(String file) throws IOException {
        confFile = file;
        save();
    }

}
