package com.tuberlin.dbpro.services;

import com.tuberlin.dbpro.models.database.GeoJson;
import com.tuberlin.dbpro.models.database.Properties;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static com.tuberlin.dbpro.services.DatabaseService.returnPatchNamesINI;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class DatabaseServiceTest {

    @Test
    void testReturnPatchNamesValid(){

        List<GeoJson> geojsons = new ArrayList<>();
        GeoJson g1 = new GeoJson();
        GeoJson g2 = new GeoJson();
        Properties p1 = new Properties();
        Properties p2 = new Properties();
        p1.setPatchName("S2A_MSIL2A_20170613T101031_0_57");
        p2.setPatchName("S2A_MSIL2A_20170613T101031_0_71");
        g1.setId("0");
        g2.setId("1");
        g1.setType("Type_1");
        g2.setType("Type_2");
        g1.setProperties(p1);
        g2.setProperties(p2);
        geojsons.add(g1);
        geojsons.add(g2);
        List<String> list = returnPatchNamesINI(geojsons);

        assertTrue("Image patch name mapping is faulty.",
                list.get(0).equals("S2A_MSIL2A_20170613T101031_0_57"));
        assertTrue("Image patch name mapping is faulty.",
                list.get(1).equals("S2A_MSIL2A_20170613T101031_0_71"));
    }

    @Test
    void testReturnPatchNamesInvalid(){

        List<GeoJson> geojsons = new ArrayList<>();
        List<String> returnedPatchNames = returnPatchNamesINI(geojsons);
        assertTrue("Empty result list mapping false!", returnedPatchNames.size() == 0);

        geojsons = null;
        returnedPatchNames = returnPatchNamesINI(geojsons);
        assertTrue("Empty result list mapping false!", returnedPatchNames.size() == 0);
    }
}
