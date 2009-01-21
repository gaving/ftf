package net.brokentrain.ftf.core.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Represents a configuration file that allows manipulation of a Services
 * details. It provides constructors that take a {@link #Config(String)}.
 * Specific sections can then be extracted from it using methods such as {@link
 * #get get()} or all sections can be obtained using {@link #sections
 * sections()}
 */
public class Config extends ConfigLoader {

    /**
     * Construct a new config by first passing it up to the ConfigLoader
     * 
     * @param file
     *            The filename from which to construct a new config.
     * @exception IOException
     *                if the file does not exist.
     */
    public Config(String file) throws IOException {
        super(file);
        load();
    }

    /**
     * Adds a section to a configuration file.
     * 
     * @param sectionName
     *            The name to add.
     */
    public void addSection(String sectionName) {
        if (config.containsKey(sectionName)) {
            return;
        }
        config.put(sectionName, new Hashtable<String, String>());
    }

    public void clear() {
        config = new Hashtable<String, Hashtable<String, String>>();
    }

    /**
     * Raw outputs a representation of the configuration file.
     * 
     * @return A dump of the current configuration.
     */
    public String dump() {

        String configOutput = "";
        String[] sectionKeys = sections();

        for (String element : sectionKeys) {
            configOutput += "[" + element + "]\n";
            String[] keys = keys(element);

            for (String element0 : keys) {
                configOutput += element0 + ": " + get(element, element0) + "\n";
            }
        }
        return configOutput;
    }

    /**
     * Returns the value for a specific section and key.
     * 
     * @param section
     *            The section to modify.
     * @param key
     *            The key to set.
     * @return The value for this particular section and key.
     */
    public String get(String section, String key) {
        Hashtable<String, String> sectionTable = config.get(section);
        if (sectionTable == null) {
            return null;
        }
        return sectionTable.get(key);
    }

    /**
     * Returns the entire config data structure.
     * 
     * @return The entire data structure for the config file
     */
    public Hashtable<String, Hashtable<String, String>> getTable() {
        return config;
    }

    public boolean hasSection(String section) {
        Hashtable<String, String> sectionTable = config.get(section);
        return (sectionTable == null) ? false : true;
    }

    /**
     * Returns a list of keys present in the configuration file for a specific
     * section.
     * 
     * @param section
     *            The section to read.
     * @return A list of keys.
     */
    public String[] keys(String section) {
        Hashtable<String, String> h = config.get(section);
        return (h == null) ? null : sortKeys(h.keys());
    }

    /**
     * Removes a key value pair from a configuration file.
     * 
     * @param section
     *            The section to remove from.
     * @param key
     *            The key to delete from.
     */
    public void remove(String section, String key) {
        Hashtable<String, String> hashTable = config.get(section);
        if (hashTable != null) {
            hashTable.remove(key);
        }
    }

    /**
     * Removes a section from a configuration file.
     * 
     * @param sectionName
     *            The name to remove.
     */
    public void removeSection(String sectionName) {
        config.remove(sectionName);
    }

    /**
     * Returns a list of sections present in the configuration file.
     * 
     * @return A list of sections.
     */
    public String[] sections() {
        return sortKeys(config.keys());
    }

    /**
     * Set a new key value pair in a config, if no such section exists a new
     * section is created with the new pair and is otherwise modified.
     * 
     * @param section
     *            The section to modify.
     * @param key
     *            The key to set.
     * @param value
     *            The value to set.
     */
    public void set(String section, String key, String value) {
        if (!config.containsKey(section)) {
            config.put(section, new Hashtable<String, String>());
        }
        (config.get(section)).put(key, value);
    }

    /**
     * Sort the keys in a configuration file alphabetically.
     * 
     * @param e
     *            An enumeration object.
     * @return A list of sorted keys.
     */
    public String[] sortKeys(Enumeration<String> e) {
        ArrayList<String> v = new ArrayList<String>();
        while (e.hasMoreElements()) {
            String nextKey = e.nextElement();
            if (v.isEmpty()) {
                v.add(nextKey);
            } else {
                int i;
                for (i = 0; (i < v.size()) && (nextKey.compareTo(v.get(i)) > 0); i++) {
                    ;
                }
                v.add(i, nextKey);
            }
        }
        String[] result = new String[v.size()];
        v.toArray(result);

        return result;
    }

}
