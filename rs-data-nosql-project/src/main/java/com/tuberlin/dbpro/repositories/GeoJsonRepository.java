package com.tuberlin.dbpro.repositories;

import com.tuberlin.dbpro.models.database.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

/**
 * Repository class for interaction with MongoDB: contains needed database query types related to GeoJson type
 */
public interface GeoJsonRepository extends MongoRepository<GeoJson, String> {

    /**
     * Query returning all GeoJson entities including (at least) all passed labels.
     * @param labels labels to match
     * @return GeoJson objects including (at least) all passed labels
     */
    @Query(value = "{'properties.labels': {$all: [?0] }}")
    List<GeoJson> findByContainsAllPropertiesLabels(List<Character> labels);

    /**
     * Query returning all GeoJson entities including at least one of the passed labels.
     * @param labels labels to match
     * @return GeoJson objects including at least one of the passed labels
     */
    @Query(value = "{'properties.labels': {$elemMatch: {$in: [?0]} }}")
    List<GeoJson> findByContainsSomePropertiesLabels(List<Character> labels);

    /**
     * Query returning all GeoJson entities including exactly the passed labels - not more or less.
     * @param labels labels to match
     * @return GeoJson objects including exactly and only the passed labels
     */
    @Query(value = "{'properties.labels': {$eq: [?0] }}")
    List<GeoJson> findByContainsOnlyPropertiesLabels(List<Character> labels);

    /**
     * Returns all GeoJson objects located - within - the specified polygon (not overlapping but within).
     * @param polygon polygon location
     * @return GeoJson objects located - within - the specified polygon
     */
    @Query(value = "{'location' : {$geoWithin: {$geometry: ?0}}}")
    List<GeoJson> findByLocationWithin(GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying all of the passed labels (or more) and located within the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying all of the passed labels (or more) and located within the specified polygon
     */
    @Query(value = "{'properties.labels': {$all: [?0] }, 'location' : {$geoWithin: {$geometry: ?1}}}")
    List<GeoJson> findByContainsAllPropertiesLabelsAndLocationWithin(List<Character> labels, GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying at least one of the passed labels (or more) and
     * located within the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying at least one of the passed labels (or more) and
     * located within the specified polygon
     */
    @Query(value = "{'properties.labels': {$elemMatch: {$in: [?0]} }, 'location' : {$geoWithin: {$geometry: ?1}}}")
    List<GeoJson> findByContainsSomePropertiesLabelsAndLocationWithin(List<Character> labels, GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying exactly the passed labels (not more or less) and
     * located within the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying exactly the passed labels (not more or less) and
     * located within the specified polygon
     */
    @Query(value = "{'properties.labels': {$eq: [?0] }, 'location' : {$geoWithin: {$geometry: ?1}}}")
    List<GeoJson> findByContainsOnlyPropertiesLabelsAndLocationWithin(List<Character> labels, GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects that - intersect - the specified polygon (therefore, either overlapping
     * or fully included).
     * @param polygon polygon location
     * @return GeoJson objects - intersecting - the specified polygon
     */
    @Query(value = "{'location' : {$geoIntersects: {$geometry: ?0}}}")
    List<GeoJson> findByLocationIntersects(GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying all of the passed labels (or more) and intersecting the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying all of the passed labels (or more) and intersecting the specified polygon
     */
    @Query(value = "{'properties.labels': {$all: [?0] }, 'location' : {$geoIntersects: {$geometry: ?1}}}")
    List<GeoJson> findByContainsAllPropertiesLabelsAndLocationIntersects(List<Character> labels, GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying at least one of the passed labels (or more) and
     * intersecting the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying at least one of the passed labels (or more) and
     * intersecting the specified polygon
     */
    @Query(value = "{'properties.labels': {$elemMatch: {$in: [?0]} }, 'location' : {$geoIntersects: {$geometry: ?1}}}")
    List<GeoJson> findByContainsSomePropertiesLabelsAndLocationIntersects(List<Character> labels, GeoJsonPolygon polygon);

    /**
     * Returns all GeoJson objects carrying exactly the passed labels (not more or less) and
     * intersecting the specified polygon.
     * @param labels labels to match
     * @param polygon polygon location
     * @return GeoJson objects carrying exactly the passed labels (not more or less) and
     * intersecting the specified polygon
     */
    @Query(value = "{'properties.labels': {$eq: [?0] }, 'location' : {$geoIntersects: {$geometry: ?1}}}")
    List<GeoJson> findByContainsOnlyPropertiesLabelsAndLocationIntersects(List<Character> labels, GeoJsonPolygon polygon);
}
