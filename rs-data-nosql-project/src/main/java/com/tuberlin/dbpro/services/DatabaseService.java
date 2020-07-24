package com.tuberlin.dbpro.services;

import com.tuberlin.dbpro.models.database.GeoJson;
import com.tuberlin.dbpro.models.database.PatchImages;
import com.tuberlin.dbpro.models.frontend.InputRequest;
import com.tuberlin.dbpro.repositories.GeoJsonRepository;
import com.tuberlin.dbpro.repositories.PatchImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class connecting directly to database for querying.
 */
@Service
public class DatabaseService {

    /**
     * GeoJson repository instance for GeoJson-related queries.
     */
    @Autowired
    private GeoJsonRepository geoJsonRepository;

    /**
     * Patch image repository for merely storing the image files.
     */
    @Autowired
    private PatchImageRepository patchImageRepository;

    /**
     * Label service instance for label mappings.
     */
    @Autowired
    private LabelService labelService;

    /**
     * Returns a list of matching image patch names based on the passed polygon location and user parameters
     * like labels and query type.
     * @param inputRequest input request object from frontend
     * @param polygon polygon location
     * @return list of matching image patch names
     */
    List<String> getPatchImageNamesFromDatabase(InputRequest inputRequest, GeoJsonPolygon polygon) {

        // labels are first mapped to character to accelerate queries, e.g. "Sea and ocean" -> 'f'
        List<Character> targetLabels = this.labelService.getPassedLabelsEncoded(inputRequest.getLabels());

        // no label restriction requested: no need to filter labels
        if (targetLabels.size() == 44) {
            return this.getPatchImageNamesFullLabelSelection(inputRequest, polygon);
        } else {
            // label subset has been selected: need for label filtering / differentiation
            if (inputRequest.getQueryType().equals("CONTAINS_ALL")) {
                return this.getPatchImageNamesLabelFilterContainsAll(inputRequest, polygon, targetLabels);
            } else if (inputRequest.getQueryType().equals("CONTAINS_SOME")) {
                return this.getPatchImageNamesLabelFilterContainsSome(inputRequest, polygon, targetLabels);
            } else {
                return this.getPatchImageNamesLabelFilterContainsOnly(inputRequest, polygon, targetLabels);
            }
        }
    }

    /**
     * Returns a list of all the image patch names matching the search criteria with all labels selected.
     * @param inputRequest input request object from frontend
     * @param polygon polygon location
     * @return list of matching image patch names
     */
    private List<String> getPatchImageNamesFullLabelSelection(InputRequest inputRequest, GeoJsonPolygon polygon) {
        String queryText = "";
        if (inputRequest.getQueryType().equals("CONTAINS_SOME")) {
            if (polygon == null) {
                // no location provided (pure label query), but every image patch contains
                // at least one label out of the 44 available ones
                queryText = "ALL LABELS REQUESTED - CONTAINS_SOME QUERY - NO COORDINATES";
                System.out.println(queryText);

                return returnPatchNames(this.geoJsonRepository.findAll());
            } else {
                queryText = "ALL LABELS REQUESTED - CONTAINS_SOME QUERY - WITH COORDINATES - " +
                        inputRequest.getGeospatialQueryOperator();
                System.out.println(queryText);

                if (inputRequest.getGeospatialQueryOperator().equals("GEO_INTERSECTS")) {
                    // return all names intersecting a given location as every image patch contains
                    // at least one label out of the 44 available ones
                    return returnPatchNames(this.geoJsonRepository.findByLocationIntersects(polygon));
                } else {
                    // return all names within given location as every image patch contains
                    // at least one label out of the 44 available ones
                    return returnPatchNames(this.geoJsonRepository.findByLocationWithin(polygon));
                }
            }
        } else {
            // no single point has at least all 44 labels (for CONTAINS_ONLY) or even more (for CONTAINS_ALL)
            // therefore: return empty result set
            queryText = "ALL LABELS REQUESTED - CONTAINS_ALL || CONTAINS_ONLY QUERY";
            System.out.println(queryText);
            return new ArrayList<>();
        }
    }

    /**
     * Returns a list of all the image patch names matching the search criteria with the label filter on and
     * for the CONTAINS_ALL query type.
     * @param inputRequest input request object from frontend
     * @param polygon polygon location
     * @param targetLabels target labels
     * @return list of matching image patch names
     */
    private List<String> getPatchImageNamesLabelFilterContainsAll(InputRequest inputRequest, GeoJsonPolygon polygon,
                                                                  List<Character> targetLabels) {
        String queryText = "";
        // all image patch names carrying at least the provided labels requested
        if (polygon == null) {
            // no coordinates provided: pure label query
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_ALL QUERY - NO COORDINATES";
            System.out.println(queryText);
            return returnPatchNames(this.geoJsonRepository.findByContainsAllPropertiesLabels(targetLabels));
        } else {
            // hard case: frontend requests all image patch names with all (or more than the) requested labels
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_ALL QUERY - WITH COORDINATES - " +
                    inputRequest.getGeospatialQueryOperator();
            System.out.println(queryText);

            if (inputRequest.getGeospatialQueryOperator().equals("GEO_INTERSECTS")) {
                // intersecting the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsAllPropertiesLabelsAndLocationIntersects(targetLabels, polygon));
            } else {
                // within the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsAllPropertiesLabelsAndLocationWithin(targetLabels, polygon));
            }
        }
    }

    /**
     * Returns a list of all the image patch names matching the search criteria with the label filter on and
     * for the CONTAINS_SOME query type.
     * @param inputRequest input request object from frontend
     * @param polygon polygon location
     * @param targetLabels target labels
     * @return list of matching image patch names
     */
    private List<String> getPatchImageNamesLabelFilterContainsSome(InputRequest inputRequest, GeoJsonPolygon polygon,
                                                                   List<Character> targetLabels) {
        String queryText = "";
        // all image patch names carrying at least one of the provided labels required
        if (polygon == null) {
            // no location provided: pure label query
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_SOME QUERY - NO COORDINATES";
            System.out.println(queryText);
            return returnPatchNames(this.geoJsonRepository.findByContainsSomePropertiesLabels(targetLabels));
        } else {
            // hard case: frontend requests all image patch names with at least one of the requested labels
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_SOME QUERY - WITH COORDINATES - " +
                    inputRequest.getGeospatialQueryOperator();
            System.out.println(queryText);

            if (inputRequest.getGeospatialQueryOperator().equals("GEO_INTERSECTS")) {
                // intersecting the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsSomePropertiesLabelsAndLocationIntersects(targetLabels, polygon));
            } else {
                // within the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsSomePropertiesLabelsAndLocationWithin(targetLabels, polygon));
            }
        }
    }

    /**
     * Returns a list of all the image patch names matching the search criteria with the label filter on and
     * for the CONTAINS_ONLY query type.
     * @param inputRequest input request object from frontend
     * @param polygon polygon location
     * @param targetLabels target labels
     * @return list of matching image patch names
     */
    private List<String> getPatchImageNamesLabelFilterContainsOnly(InputRequest inputRequest, GeoJsonPolygon polygon,
                                                                   List<Character> targetLabels) {
        String queryText = "";
        // CONTAINS_ONLY query: all image patch names carrying exactly and only the provided labels requested
        if (polygon == null) {
            // no location provided: pure label query
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_ONLY QUERY - NO COORDINATES";
            System.out.println(queryText);
            return returnPatchNames(this.geoJsonRepository.findByContainsOnlyPropertiesLabels(targetLabels));
        } else {
            // hard case: frontend requests all image patch names with exactly and only the requested labels
            queryText = "LABELS SUBSET REQUESTED - CONTAINS_ONLY QUERY - WITH COORDINATES - " +
                    inputRequest.getGeospatialQueryOperator();
            System.out.println(queryText);

            if (inputRequest.getGeospatialQueryOperator().equals("GEO_INTERSECTS")) {
                // intersecting the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsOnlyPropertiesLabelsAndLocationIntersects(targetLabels, polygon));
            } else {
                // within the passed location
                return returnPatchNames(this.geoJsonRepository
                        .findByContainsOnlyPropertiesLabelsAndLocationWithin(targetLabels, polygon));
            }
        }
    }

    /**
     * Persists a given GeoJson object in the database.
     * @param geoJson GeoJson object to persist
     */
    void persistGeoJson(GeoJson geoJson) {
        this.geoJsonRepository.save(geoJson);
    }

    /**
     * Persists a given PatchImages object in the database.
     * @param patchImages patch images object to persist
     */
    void persistPatchImages(PatchImages patchImages) {
        this.patchImageRepository.save(patchImages);
    }

    /**
     * Returns list of patch names of the passed GeoJson objects.
     * @param geojsons list of GeoJson objects
     * @return list of corresponding image patches' names
     */
    static List<String> returnPatchNamesINI(List<GeoJson> geojsons) {
        if (geojsons == null || geojsons.size() == 0) {
            return new ArrayList<>();
        }

        // streams are intended here instead of classic database projections
        // Why? => Because mongoDB projections always deliver an _id field that we cannot exclude from our result set,
        // therefore we would have to use streams anyway to drop these
        // (we don't want to pass unnecessary information to the frontend, just the requested image patch names)
        // hence, we do not use projections + streams but only streams to save performance

        return geojsons
                .parallelStream()
                .map(geoJson -> geoJson
                        .getProperties()
                        .getPatchName())
                .collect(Collectors.toList());
    }

    /**
     * Returns list of patch names of the passed GeoJson objects.
     * @param geojsons list of GeoJson objects
     * @return list of corresponding image patches' names
     */
    //TODO: ask what happen if I do not make this function static?
    List<String> returnPatchNames(List<GeoJson> geojsons) {
        if (geojsons == null || geojsons.size() == 0) {
            return new ArrayList<>();
        }

        // streams are intended here instead of classic database projections
        // Why? => Because mongoDB projections always deliver an _id field that we cannot exclude from our result set,
        // therefore we would have to use streams anyway to drop these
        // (we don't want to pass unnecessary information to the frontend, just the requested image patch names)
        // hence, we do not use projections + streams but only streams to save performance

        List<Object[]> labels = geojsons
                .parallelStream()
                .map(geoJson -> geoJson.getProperties().getLabels().toArray())
                .collect(Collectors.toList());
        System.out.println("TOTAL No. PATCHES in this selection=" + labels.size());

        // Get the labels of each patch, decoded
        List<String> labelsString = new ArrayList<String>();
        // Get a summary of all the labels contained in this List
        Map<Character, Integer> labelsCharSummary = new TreeMap<>();
        for(Object [] lab: labels) {
            String this_patch_labels = "";
            for(int i=0; i<lab.length; i++){
                //System.out.println("Char: " + lab[i]);
                if(labelsCharSummary.containsKey(lab[i])){
                    int val = labelsCharSummary.get(lab[i]);
                    val++;
                    labelsCharSummary.replace((Character)lab[i],val);
                } else {
                    labelsCharSummary.put((Character)lab[i],1);
                }
                this_patch_labels += this.labelService.getDecodedCharacter((Character)lab[i]) + ", ";
            }
            labelsString.add(this_patch_labels);
        }
        // label characters are decoded back to normal strings 'f' ->  "Sea and ocean"
        Map<String, Integer> labelsStringSummary = new TreeMap<>();
        for (Map.Entry<Character, Integer> entry : labelsCharSummary.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();
            labelsStringSummary.put(this.labelService.getDecodedCharacter(key),value);
        }

        System.out.println("LABELS CHAR SUMMARY:" + EntriesSortedByValues(labelsCharSummary).toString());
        System.out.println("LABELS STRING SUMMARY:" + EntriesSortedByValues(labelsStringSummary).toString());

        List<Map.Entry<String,Integer>> returnList = EntriesSortedByValues(labelsStringSummary);
        List<String> patchesLabelsNamesCoor = new ArrayList<String>();

        patchesLabelsNamesCoor.add("TOTAL No. PATCHES in this selection=" + labels.size());
        patchesLabelsNamesCoor.add("LABELS SUMMARY: ");
        for (Map.Entry<String, Integer> entry : returnList) {
            patchesLabelsNamesCoor.add(entry.getKey() + "=" + entry.getValue());
        }

        // get the patches names
        //patchesLabelsNamesCoor.add("PATCHES_NAMES AND COOR:");
        List<String> patches_names = geojsons
                .parallelStream()
                .map(geoJson -> geoJson.getProperties().getPatchName())
                .collect(Collectors.toList());
        //for(String str : patches_names) {
        //    patchesLabelsNamesCoor.add(str);
        //}

        // get the coordinates of the patches in the selection
        List<List<GeoJsonLineString>> locations = geojsons
                .parallelStream()
                .map(geoJson -> geoJson.getLocation().getCoordinates())
                .collect(Collectors.toList());

        //System.out.println("No. LOCATIONS: " + locations.size());
        int n=0;
        for(List<GeoJsonLineString> loc: locations) {
           for(GeoJsonLineString coor : loc) {
               // TODO: calculate the middle point for the moment returning (ul_long,ul_lat)
               //System.out.println("  LineString: " + coor.getCoordinates());
               //System.out.println("  point[0]: " + coor.getCoordinates().get(0));
               patchesLabelsNamesCoor.add("patch: " + patches_names.get(n) + ";" +
                       coor.getCoordinates().get(0).getY()+ ";" +
                       coor.getCoordinates().get(0).getX() + ";" +
                       labelsString.get(n));
               n++;
           }
        }



        return patchesLabelsNamesCoor;
    }


    /**
     * from:
     *   https://stackoverflow.com/questions/11647889/sorting-the-mapkey-value-in-descending-order-based-on-the-value
     */
    static <K,V extends Comparable<? super V>> List<Map.Entry<K, V>> EntriesSortedByValues(Map<K,V> map) {
        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );
        return sortedEntries;
    }


}
