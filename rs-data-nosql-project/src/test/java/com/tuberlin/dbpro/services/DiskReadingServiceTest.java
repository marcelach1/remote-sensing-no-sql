package com.tuberlin.dbpro.services;

import com.tuberlin.dbpro.models.database.GeoJson;
import com.tuberlin.dbpro.models.diskreading.Coordinates;
import com.tuberlin.dbpro.models.diskreading.MetaJson;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class DiskReadingServiceTest {

    private DiskReadingService diskReadingService = new DiskReadingService();

    Coordinates generateCoordinates(){
        Coordinates c = new Coordinates();

        c.setUlx(7.646484);
        c.setUly(51.206238);

        c.setLrx(8.0);
        c.setLry(45.0);

        return  c;
    }


    @Test
    void testGetGeoJsonAndProjection(){
        LabelService labelService = new LabelService();
        diskReadingService.setLabelService(labelService);
        labelService.initializeLabelMap();

        List<String> labelList = new ArrayList<>();
        labelList.add("Airports");

        MetaJson metaJson = new MetaJson();
        metaJson.setLabels(labelList);
        Coordinates c = generateCoordinates();
        metaJson.setCoordinates(c);
        metaJson.setProjection(".............EPSG:32632...");
        metaJson.setAcquisition_date("01-01-1999");

        String patchName = "S2A_MSIL2A_20170613T101031_0_57";


        GeoJson geoJson = diskReadingService.getGeoJson(metaJson,patchName);
        assertTrue("Wrong patch name passed!",
                geoJson.getProperties().getPatchName().equals("S2A_MSIL2A_20170613T101031_0_57"));

        assertTrue("Wrong labels mapped!", geoJson.getProperties().getLabels().get(0).equals('B') &&
                geoJson.getProperties().getLabels().size() == 1);

        assertTrue("Projection did not work!", geoJson.getLocation() != null);
    }
}
