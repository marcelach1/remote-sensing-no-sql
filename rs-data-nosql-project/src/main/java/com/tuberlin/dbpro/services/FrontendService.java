package com.tuberlin.dbpro.services;

import com.tuberlin.dbpro.models.frontend.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Service class for frontend-related processing.
 */
@Service
public class FrontendService {

    /**
     * Instance of database service as querying interface.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Returns whether the passed input request contains valid coordinates for querying.
     * @param inputRequest input request
     * @return Whether the input request contains valid coordinates
     */
    public boolean isValidAnalogueInputRequest(InputRequest inputRequest) {
        if ((inputRequest.getGeometry() == null || inputRequest.getGeometry().getCoordinates().size() == 0)) {
            if (inputRequest.getLabels().size() > 0) {
                // if no coordinates given: must be a pure label query
                return true;
            }
            return false;
        } else if (inputRequest.getGeometry().getCoordinates().get(0).size() < 5){
            // need at least five points for GeoJson rectangles
            return false;
        }

        List<List<Double>> coordinates = inputRequest.getGeometry().getCoordinates().get(0);
        double ulx = coordinates.get(0).get(0);
        double uly = coordinates.get(0).get(1);
        double urx = coordinates.get(1).get(0);
        double ury = coordinates.get(1).get(1);
        double lrx = coordinates.get(2).get(0);
        double lry = coordinates.get(2).get(1);
        double llx = coordinates.get(3).get(0);
        double lly = coordinates.get(3).get(1);

        if (lrx - ulx >= 180.0) {
            // Spring's MongoDB API does not yet support bigger *latitude ranges* for geospatial queries (only natively)
            // as the needed GeoJsonPolygon class does not contain a CRS attribute that is necessary for this
            // see source: https://docs.mongodb.com/manual/reference/operator/query/geoWithin/
            // last access: 02 July 2020, around 1PM UTC+1
            // luckily, this should not be an issue for the BigEarthNet data set we operate on
            return false;
        }

        if (llx == ulx && lly == lry) { // lower left okay
            if (urx == lrx && ury == uly) { // upper right okay
                // as both points okay and == transitive: all four points fine
                if (llx >= -180.0 && urx <= 180.0 && lly >= -90.0 && ury <= 90.0) {
                    // coordinates within allowed bounds, but polygons require a closed ring!
                    return ulx == coordinates.get(4).get(0) && uly == coordinates.get(4).get(1);
                }
            }
        }

        return false;
    }

    /**
     * Returns the according output response of the input request.
     * @param inputRequest input request
     * @return output response
     */
    public OutputResponse getOutputResponse(InputRequest inputRequest) {
        List<String> labels = inputRequest.getLabels();
        GeoJsonPolygon geoJsonPolygon = null;

        if (inputRequest.getGeometry() != null && inputRequest.getGeometry().getCoordinates().size() > 0) {
            geoJsonPolygon = this.getGeoJsonPolygon(inputRequest);
        }

        // print what the server received
        System.out.println("Received polygon: " + geoJsonPolygon);
        System.out.println("Received labels: " + Arrays.toString(labels.toArray()));

        // initialize response object, query the database and insert matching patch names in response object
        OutputResponse response = new OutputResponse();
        List<String> resultNames = this.databaseService.getPatchImageNamesFromDatabase(inputRequest, geoJsonPolygon);
        response.setPatchNames(resultNames);

        // print and return results
        //System.out.println("Returning " + String.valueOf(response.getPatchNames().size()) + " image names: ");

        return response;
    }

    /**
     * Returns a GeoJsonPolygon object for querying given the input request.
     * @param inputRequest input request containing coordinates and requested labels
     * @return GeoJsonPolygon object for querying
     */
    GeoJsonPolygon getGeoJsonPolygon(InputRequest inputRequest) {

        List<List<Double>> coordinates = inputRequest.getGeometry().getCoordinates().get(0);
        List<Point> pointList = new ArrayList<>();

        for(int i = 0; i < coordinates.size(); i++) {
            pointList.add(new Point(coordinates.get(i).get(0), coordinates.get(i).get(1)));
        }

        return new GeoJsonPolygon(pointList);
    }
}
