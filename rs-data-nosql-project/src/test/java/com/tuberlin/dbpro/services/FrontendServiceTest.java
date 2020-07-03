package com.tuberlin.dbpro.services;

import com.tuberlin.dbpro.models.frontend.FrontendPolygon;
import com.tuberlin.dbpro.models.frontend.InputRequest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class FrontendServiceTest {

    private FrontendService frontendService = new FrontendService();

    @Test
    void testValidRequestOnlyLabels() {
        InputRequest inputRequest = new InputRequest();
        List<String> labelList = new ArrayList<>();
        labelList.add("e");
        inputRequest.setLabels(labelList);

        assertTrue("Pure label request not accepted due to \"coordinates\"!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testEmptyRequest() {
        InputRequest inputRequest = new InputRequest();
        List<String> labelList = new ArrayList<>();
        inputRequest.setLabels(labelList);

        assertFalse("Empty request supported!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testWrongBoundingBoxCoordinates() {
        InputRequest inputRequest = new InputRequest();
        FrontendPolygon geometry = new FrontendPolygon();
        List<Double> p1 = new ArrayList<>();
        List<Double> p2 = new ArrayList<>();
        List<List<Double>> pointsList = new ArrayList<>();
        pointsList.add(p1);
        pointsList.add(p2);
        List<List<List<Double>>> wrapperList = new ArrayList<>();
        wrapperList.add(pointsList);

        geometry.setCoordinates(wrapperList);
        inputRequest.setGeometry(geometry);


        assertFalse("Invalid bounding box point assumed supported!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testRequestLargerThanHemisphere() {
        InputRequest inputRequest = new InputRequest();
        FrontendPolygon geometry = new FrontendPolygon();

        List<Double> p1 = new ArrayList<>();
        p1.add(-180.0);
        p1.add(0.0);
        List<Double> p2 = new ArrayList<>();
        p2.add(-180.0);
        p2.add(0.0);

        List<Double> p3 = new ArrayList<>();
        p3.add(180.0);
        p3.add(0.0);
        List<Double> p4 = new ArrayList<>();
        p4.add(180.0);
        p4.add(0.0);

        List<Double> p5 = new ArrayList<>();
        p1.add(-180.0);
        p1.add(0.0);

        List<List<Double>> pointsList = new ArrayList<>();
        pointsList.add(p1);
        pointsList.add(p2);
        pointsList.add(p3);
        pointsList.add(p4);
        pointsList.add(p5);
        List<List<List<Double>>> wrapperList = new ArrayList<>();
        wrapperList.add(pointsList);

        geometry.setCoordinates(wrapperList);
        inputRequest.setGeometry(geometry);


        assertFalse("Queries larger than a hemisphere supported!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testValidRequestSupported() {
        InputRequest inputRequest = new InputRequest();
        FrontendPolygon geometry = new FrontendPolygon();

        List<Double> p1 = new ArrayList<>();
        p1.add(-180.0);
        p1.add(10.0);
        List<Double> p2 = new ArrayList<>();
        p2.add(-90.0);
        p2.add(10.0);

        List<Double> p3 = new ArrayList<>();
        p3.add(-90.0);
        p3.add(0.0);
        List<Double> p4 = new ArrayList<>();
        p4.add(-180.0);
        p4.add(0.0);

        List<Double> p5 = new ArrayList<>();
        p5.add(-180.0);
        p5.add(10.0);

        List<List<Double>> pointsList = new ArrayList<>();
        pointsList.add(p1);
        pointsList.add(p2);
        pointsList.add(p3);
        pointsList.add(p4);
        pointsList.add(p5);
        List<List<List<Double>>> wrapperList = new ArrayList<>();
        wrapperList.add(pointsList);

        geometry.setCoordinates(wrapperList);
        inputRequest.setGeometry(geometry);


        assertTrue("Valid coordinates not supported!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testInValidRequestSupported() {
        InputRequest inputRequest = new InputRequest();
        FrontendPolygon geometry = new FrontendPolygon();

        List<Double> p1 = new ArrayList<>();
        p1.add(-180.0);
        p1.add(10.0);
        List<Double> p2 = new ArrayList<>();
        p2.add(-90.0);
        p2.add(10.0);

        List<Double> p3 = new ArrayList<>();
        p3.add(-90.0);
        p3.add(0.0);
        List<Double> p4 = new ArrayList<>();
        p4.add(-180.0);
        p4.add(0.01);

        List<Double> p5 = new ArrayList<>();
        p5.add(-180.0);
        p5.add(0.0);

        List<List<Double>> pointsList = new ArrayList<>();
        pointsList.add(p1);
        pointsList.add(p2);
        pointsList.add(p3);
        pointsList.add(p4);
        pointsList.add(p5);
        List<List<List<Double>>> wrapperList = new ArrayList<>();
        wrapperList.add(pointsList);

        geometry.setCoordinates(wrapperList);
        inputRequest.setGeometry(geometry);


        assertFalse("Valid coordinates not supported!",
                frontendService.isValidAnalogueInputRequest(inputRequest));
    }

    @Test
    void testGetGeoJsonPolygon() {

        InputRequest inputRequest = new InputRequest();
        FrontendPolygon geometry = new FrontendPolygon();

        List<Double> p1 = new ArrayList<>();
        p1.add(-180.0);
        p1.add(10.0);
        List<Double> p2 = new ArrayList<>();
        p2.add(-90.0);
        p2.add(10.0);

        List<Double> p3 = new ArrayList<>();
        p3.add(-90.0);
        p3.add(0.0);
        List<Double> p4 = new ArrayList<>();
        p4.add(-180.0);
        p4.add(0.0);

        List<Double> p5 = new ArrayList<>();
        p5.add(-180.0);
        p5.add(10.0);

        List<List<Double>> pointsList = new ArrayList<>();
        pointsList.add(p1);
        pointsList.add(p2);
        pointsList.add(p3);
        pointsList.add(p4);
        pointsList.add(p5);
        List<List<List<Double>>> wrapperList = new ArrayList<>();
        wrapperList.add(pointsList);

        geometry.setCoordinates(wrapperList);
        inputRequest.setGeometry(geometry);

        assertTrue("GeoJsonPolygon mapping not working!",
                    frontendService.getGeoJsonPolygon(inputRequest) != null);
    }
}
