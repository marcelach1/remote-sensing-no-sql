package com.tuberlin.dbpro.models.frontend;

import java.util.List;

/**
 * Class for mapping visual queries for polygons from frontend (via map component) to POJO.
 */
public class InputRequest {

    /**
     * GeoJSON-conform geometry object.
     */
    private FrontendPolygon geometry;

    /**
     * List of requested labels.
     */
    private List<String> labels;

    /**
     * Requested query type.
     */
    private String queryType;

    /**
     * Requested geo-spatial query operator (either GEO_INTERSECTS or GEO_WITHIN).
     */
    private String geospatialQueryOperator;

    /**
     * Returns geometry of requested polygon.
     * @return geometry of requested polygon
     */
    public FrontendPolygon getGeometry() {
        return geometry;
    }

    /**
     * Sets geometry of polygon.
     * @param geometry geometry of polygon
     */
    public void setGeometry(FrontendPolygon geometry) {
        this.geometry = geometry;
    }

    /**
     * Returns list of requested labels.
     * @return list of requested labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Sets requested labels.
     * @param labels requested labels
     */
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Returns query type.
     * @return query type
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * Sets query type.
     * @param queryType query type
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    /**
     * Returns geospatial query operator.
     * @return geospatialQueryOperator geospatial query operator
     */
    public String getGeospatialQueryOperator() {
        return geospatialQueryOperator;
    }

    /**
     * Sets geospatial query operator.
     * @param geospatialQueryOperator geospatial query operator
     */
    public void setGeospatialQueryOperator(String geospatialQueryOperator) {
        this.geospatialQueryOperator = geospatialQueryOperator;
    }

    /**
     * Returns string representation of input request.
     * @return string representation of input request
     */
    @Override
    public String toString() {
        return "InputRequest{" +
                "geometry=" + geometry +
                ", labels=" + labels +
                ", queryType=" + queryType +
                '}';
    }
}
