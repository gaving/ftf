package net.brokentrain.ftf.core;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultSet implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private Result parent;

    private ArrayList<Result> data;

    /**
     * Construct a new result set.
     */
    public ResultSet() {
    }

    /**
     * Add a result to the collection.
     * 
     * @param result
     *            The result to add to the set.
     */
    public void add(Result result) {
        data.add(result);
    }

    /**
     * Return the results that have been collected.
     * 
     * @return The results that have been collected.
     */
    public ArrayList<Result> getData() {
        return data;
    }

    /**
     * Return the parent result that the rest of these results stem from.
     * 
     * @return The parent result.
     */
    public Result getOriginalLink() {
        return parent;
    }

    /**
     * Set a list of child results for this ResultSet.
     * 
     * @param data
     *            The list of child results.
     */
    public void setData(ArrayList<Result> data) {
        this.data = data;
    }

    /**
     * Set the parent result of this ResultSet.
     * 
     * @param parent
     *            The parent result.
     */
    public void setOriginalLink(Result parent) {
        this.parent = parent;
    }
}
