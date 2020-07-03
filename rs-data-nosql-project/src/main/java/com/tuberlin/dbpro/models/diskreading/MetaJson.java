package com.tuberlin.dbpro.models.diskreading;

import java.util.List;
import java.util.Objects;

/**
 * Jackson-parsed class for meta JSON disk file of an image patch.
 */
public class MetaJson {

    /**
     * List of labels included in image patch.
     */
    private List<String> labels;

    /**
     * Coordinates of image patch.
     */
    private Coordinates coordinates;

    /**
     * Projection information of image patch.
     */
    private String projection;

    /**
     * Acquisition data of image patch.
     */
    private String acquisition_date;

    /**
     * Tile source of image patch.
     */
    private String tile_source;

    /**
     * Returns list of labels included in image patch.
     * @return list of labels included in image patch
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Sets labels of image patch.
     * @param labels list of labels
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Returns coordinates included in image patch.
     * @return coordinates included in image patch
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets coordinates of image patch.
     * @param coordinates coordinates of image patch
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Returns projection information included in image patch as string.
     * @return projection information included in image patch
     */
    public String getProjection() {
        return projection;
    }

    /**
     * Sets projection of image patch.
     * @param projection projection  of image patch
     */
    public void setProjection(String projection) {
        this.projection = projection;
    }

    /**
     * Returns acquisition date of image patch as string.
     * @return returns acquisition date of image patch
     */
    public String getAcquisition_date() {
        return acquisition_date;
    }

    /**
     * Sets acquisition date of image patch.
     * @param acquisition_date acquisition date of image patch
     */
    public void setAcquisition_date(String acquisition_date) {
        this.acquisition_date = acquisition_date;
    }

    /**
     * Returns tile source of image patch as string.
     * @return tile source of image patch as string
     */
    public String getTile_source() {
        return tile_source;
    }

    /**
     * Sets tile source of image patch.
     * @param tile_source tile source of image patch
     */
    public void setTile_source(String tile_source) {
        this.tile_source = tile_source;
    }

    /**
     * Returns string representation of MetaJson object.
     * @return string representation of MetaJson object
     */
    @Override
    public String toString() {
        return "MetaJson{" +
                "labels=" + labels +
                ", coordinates=" + coordinates +
                ", projection='" + projection + '\'' +
                ", acquisition_date='" + acquisition_date + '\'' +
                ", tile_source='" + tile_source + '\'' +
                '}';
    }

    /**
     * Hash code method needed for file parsing.
     * @return hash code of object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(labels, coordinates, projection, acquisition_date, tile_source);
    }
}
