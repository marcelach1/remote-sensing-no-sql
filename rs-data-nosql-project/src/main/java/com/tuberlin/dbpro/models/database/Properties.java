package com.tuberlin.dbpro.models.database;

import org.springframework.data.mongodb.core.index.Indexed;
import java.util.List;

/**
 * Properties class containing both labels and name of single image patch.
 */
public class Properties {

    /**
     * Name of the image patch.
     */
    private String patchName;

    /**
     * List of labels the image patch features, multi-key index on list
     */
    @Indexed(name = "multikey-label-index")
    private List<Character> labels;

    /**
     * Returns name of image patch.
     * @return name of image patch
     */
    public String getPatchName() {
        return patchName;
    }

    /**
     * Sets name of image patch.
     * @param patchName name of image patch
     */
    public void setPatchName(String patchName) {
        this.patchName = patchName;
    }

    /**
     * Returns list of featured image patch labels.
     * @return Labels which are included in image patch with above name
     */
    public List<Character> getLabels() {
        return labels;
    }

    /**
     * Sets label list of image patch.
     * @param labels label list of image patch
     */
    public void setLabels(List<Character> labels) {
        this.labels = labels;
    }

    /**
     * Returns string representation of Properties object.
     * @return string representation of Properties object.
     */
    @Override
    public String toString() {
        return "Properties{" +
                "patchName='" + patchName + '\'' +
                ", labels=" + labels +
                '}';
    }
}
