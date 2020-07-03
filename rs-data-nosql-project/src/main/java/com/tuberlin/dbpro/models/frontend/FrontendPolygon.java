package com.tuberlin.dbpro.models.frontend;

import java.util.List;

/**
 * Intermediate class for parsing frontend request into POJO as internally used
 * spring.data.os.geo.GeoJsonPolygon is not compatible with Jackson REST parsing.
 */
public class FrontendPolygon {

    /**
     * Type of GeoJson (default: "Polygon")
     */
    private String type;

    /**
     * Polygon coordinates according to GeoJSON standard.
     */
    private List<List<List<Double>>> coordinates;

    /**
     * Returns type.
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns polygon coordinates according to GeoJSON standard.
     * @return polygon coordinates according to GeoJSON standard
     */
    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }

    /**
     * Sets polygon coordinates according to official GeoJSON standard.
     * @param coordinates coordinates according to GeoJSON standard
     */
    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Returns string representation.
     * @return string representation
     */
    @Override
    public String toString() {
        return "GeoJsonPolygonGeometry{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
