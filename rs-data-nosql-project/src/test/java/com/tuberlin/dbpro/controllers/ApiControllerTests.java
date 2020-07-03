package com.tuberlin.dbpro.controllers;

import com.tuberlin.dbpro.models.frontend.InputRequest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class ApiControllerTests {

    @Test
    void testIfQueryTypeSupported(){

        ApiController controller = new ApiController();

        InputRequest inputRequest1 = new InputRequest();
        InputRequest inputRequest2 = new InputRequest();
        InputRequest inputRequest3 = new InputRequest();
        InputRequest inputRequest4 = new InputRequest();
        InputRequest inputRequest5 = new InputRequest();
        inputRequest1.setQueryType("CONTAINS_ONLY");
        inputRequest2.setQueryType("CONTAINS_ALL");
        inputRequest3.setQueryType("CONTAINS_SOME");
        inputRequest4.setQueryType("");
        inputRequest5.setQueryType("X");
        inputRequest1.setGeospatialQueryOperator("GEO_WITHIN");
        inputRequest2.setGeospatialQueryOperator("GEO_INTERSECTS");
        inputRequest3.setGeospatialQueryOperator("42");

        assertTrue("Supported query type considered unsupported!",
                controller.isQueryTypeSupported(inputRequest1));
        assertTrue("Supported query type considered unsupported!",
                controller.isQueryTypeSupported(inputRequest2));
        assertTrue("Supported query type considered unsupported!",
                controller.isQueryTypeSupported(inputRequest3));
        assertFalse("Unsupported query type considered supported!",
                controller.isQueryTypeSupported(inputRequest4));
        assertFalse("Unsupported query type considered supported!",
                controller.isQueryTypeSupported(inputRequest5));
        assertTrue("Supported geospatial operator considered unsupported!",
                controller.isGeospatialOperatorSupported(inputRequest1));
        assertTrue("Supported geospatial operator considered unsupported!",
                controller.isGeospatialOperatorSupported(inputRequest2));
        assertFalse("Supported geospatial operator considered unsupported!",
                controller.isGeospatialOperatorSupported(inputRequest3));
    }
}
