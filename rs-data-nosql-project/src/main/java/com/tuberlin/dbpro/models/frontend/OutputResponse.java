package com.tuberlin.dbpro.models.frontend;

import java.util.List;

/**
 * Response class sent to frontend after querying - containing names of matching image patches.
 */
public class OutputResponse {

    /**
     * List of matching image names.
     */
    private List<String> patchNames;

    /**
     * Returns list of matching image names.
     * @return list of matching image names
     */
    public List<String> getPatchNames() {
        return patchNames;
    }

    /**
     * Sets the list of matching image names.
     * @param patchNames list of matching image names
     */
    public void setPatchNames(List<String> patchNames) {
        this.patchNames = patchNames;
    }

    /**
     * Returns string representation of output response.
     * @return string representation of output response
     */
    @Override
    public String toString() {
        return "OutputResponse{" +
                "patchNames=" + patchNames +
                '}';
    }
}
