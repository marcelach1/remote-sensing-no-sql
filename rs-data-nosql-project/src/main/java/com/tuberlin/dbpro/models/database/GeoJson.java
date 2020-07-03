package com.tuberlin.dbpro.models.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * GeoJson class / collection persisted in database: contains coordinates and labels in hierarchy.
 */
@CompoundIndex(name = "compound-index", def = "{'properties.labels' : 1, 'location' : '2dsphere'}")
@Document(collection = "geojsons")
public class GeoJson {

    /**
     * Id of entity
     */
    @Id
    private String id;

    /**
     * Type of GeoJson (here: "Polygon")
     */
    private String type;

    /**
     * GeoJsonPolygon geometry containing coordinates, geo-spatial 2dsphere index on field
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, name = "geospatial-coordinate-index")
    private GeoJsonPolygon location;

    /**
     * GeoJSON-conform Properties objects containing labels
     */
    private Properties properties;

    /**
     * Sets id.
     * @param id id of entity
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets entity id.
     * @return id id of the GeoJson object
     */
    public String getId() {
        return id;
    }

    /**
     * Gets GeoJSON type (default: "Feature").
     * @return type type of the GeoJson object
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     * @param type Type of GeoJson
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets image patch location.
     * @return location location of the GeoJson object
     */
    public GeoJsonPolygon getLocation() {
        return location;
    }

    /**
     * Sets location.
     * @param location polygon location of entity
     */
    public void setLocation(GeoJsonPolygon location) {
        this.location = location;
    }

    /**
     * Gets image patch properties.
     * @return properties properties of the GeoJson object containing labels and patch name
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets properties.
     * @param properties properties of entity
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns string representation of GeoJson object.
     * @return string representation of GeoJson object
     */
    @Override
    public String toString() {
        return "GeoJson{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", geometry=" + location +
                ", properties=" + properties +
                '}';
    }
}
