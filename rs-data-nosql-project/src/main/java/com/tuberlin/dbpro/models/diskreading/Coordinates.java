package com.tuberlin.dbpro.models.diskreading;

/**
 * Class containing the Jackson-parsed box coordinates from the BigEarthNet disk files.
 */
public class Coordinates {

    /**
     * upper left x coordinate
     */
    private double ulx;

    /**
     * upper left y coordinate
     */
    private double uly;

    /**
     * lower right x coordinate
     */
    private double lrx;

    /**
     * lower right y coordinate
     */
    private double lry;

    /**
     * Returns upper left x coordinate.
     * @return upper left x coordinate
     */
    public double getUlx() {
        return ulx;
    }

    /**
     * Sets upper left x coordinate.
     * @param ulx upper left x coordinate
     */
    public void setUlx(double ulx) {
        this.ulx = ulx;
    }


    /**
     * Returns upper left y coordinate.
     * @return upper left y coordinate
     */
    public double getUly() {
        return uly;
    }

    /**
     * Sets upper left y coordinate.
     * @param uly upper left y coordinate
     */
    public void setUly(double uly) {
        this.uly = uly;
    }

    /**
     * Returns lower right x coordinate.
     * @return lower right x coordinate
     */
    public double getLrx() {
        return lrx;
    }

    /**
     * Sets lower right x coordinate.
     * @param lrx lower right x coordinate
     */
    public void setLrx(double lrx) {
        this.lrx = lrx;
    }

    /**
     * Returns lower right y coordinate.
     * @return lower right y coordinate
     */
    public double getLry() {
        return lry;
    }

    /**
     * Sets lower right y coordinate.
     * @param lry lower right y coordinate
     */
    public void setLry(double lry) {
        this.lry = lry;
    }

    /**
     * Returns string representation of parsed coordinates from disk.
     * @return string representation of parsed coordinates from disk
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "ulx=" + ulx +
                ", uly=" + uly +
                ", lrx=" + lrx +
                ", lry=" + lry +
                '}';
    }
}
