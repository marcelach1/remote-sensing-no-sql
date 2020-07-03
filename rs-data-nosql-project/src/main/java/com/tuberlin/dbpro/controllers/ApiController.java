package com.tuberlin.dbpro.controllers;

import com.tuberlin.dbpro.models.frontend.*;
import com.tuberlin.dbpro.services.FrontendService;
import com.tuberlin.dbpro.services.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * This class is the controller class the client interacts with -
 * it orchestrates the application flow in the backend and ultimately returns a response object
 * to the JavaScript frontend.
 */
@RestController
@CrossOrigin(origins = "http://127.0.0.1:8080") // setting REST permission for execution port
public class ApiController {

    /**
     * Service instance for frontend-related processing.
     */
    @Autowired
    FrontendService frontendService;

    /**
     * Service instance for mapping passed labels - separate from FrontendService for reusability.
     */
    @Autowired
    LabelService labelService;

    /**
     * Supported query types related to labels:
     * contains ALL (and maybe more), contains ONLY, contains SOME (maybe even only one)
     */
    private static final String[] supportedLabelQueryTypes = {"CONTAINS_ALL", "CONTAINS_ONLY", "CONTAINS_SOME"};

    /**
     * Supported geospatial query operators (either area-intersecting or patches fully included in area).
     */
    private static final String[] supportedGeospatialQueryOperators = {"GEO_INTERSECTS", "GEO_WITHIN"};

    /**
     * Thymeleaf method: returns and renders index.html template.
     * @return Rendered HTML index view
     */
    @RequestMapping(path = "/index")
    public String getIndex(){
        return "index";
    }

    /**
     * Returns all available BigEarthNet labels.
     * @return List of available BigEarthNet labels
     */
    @GetMapping(path = "/labels")
    public List<String> getLabels() {
        return labelService.getLabels();
    }

    /**
     * POST REST interface consuming JSON requests from frontend for analogue queries.
     * @param inputRequest Input request from frontend including requested coordinates or labels
     * @return Output response object containing image patch names
     */
    @PostMapping(path = "/query-analogue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public OutputResponse getImagePatchNamesAnalogue(@RequestBody InputRequest inputRequest) {
        System.out.println("QUERY TYPE: " + inputRequest.getQueryType() +
                ", GEOSPATIAL OPERATOR: " + inputRequest.getGeospatialQueryOperator());
        double time = System.currentTimeMillis();

        if(frontendService.isValidAnalogueInputRequest(inputRequest) && this.isQueryTypeSupported(inputRequest)
                && this.isGeospatialOperatorSupported(inputRequest)) {
            OutputResponse response = this.frontendService.getOutputResponse(inputRequest);
            System.out.println("Response time in ms: " + String.valueOf((System.currentTimeMillis() - time)));
            System.out.println("--------------------------------------------------------------------------------");

            return response;
        }

        return new OutputResponse();
    }

    /**
     * POST REST interface consuming JSON requests from frontend for visual queries.
     * @param inputRequest Input request from frontend including requested coordinates or labels
     * @return Output response object containing image patch names
     */
    @PostMapping(path = "/query-visual", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    public OutputResponse getImagePatchNamesVisual(@RequestBody InputRequest inputRequest) {
        System.out.println("QUERY TYPE: " + inputRequest.getQueryType() +
                ", GEOSPATIAL OPERATOR: " + inputRequest.getGeospatialQueryOperator());
        double time = System.currentTimeMillis();

        if(this.isQueryTypeSupported(inputRequest) && this.isGeospatialOperatorSupported(inputRequest)) {
            OutputResponse response = this.frontendService.getOutputResponse(inputRequest);
            System.out.println("Response time in ms: " + String.valueOf((System.currentTimeMillis() - time)));
            System.out.println("--------------------------------------------------------------------------------");

            return response;
        }

        return new OutputResponse();
    }

    /**
     * Returns whether the query type included in the input request is supported by the application.
     * @param inputRequest Input request from frontend including requested coordinates or labels
     * @return Whether the query type is supported
     */
    boolean isQueryTypeSupported(InputRequest inputRequest) {
        String requestedQueryType = inputRequest.getQueryType();

        return ApiController.supportedLabelQueryTypes[0].equals(requestedQueryType) ||
                ApiController.supportedLabelQueryTypes[1].equals(requestedQueryType) ||
                ApiController.supportedLabelQueryTypes[2].equals(requestedQueryType);
    }

    /**
     * Returns whether the geospatial query operator included in the input request is supported by the application.
     * @param inputRequest Input request from frontend including requested coordinates or labels
     * @return Whether the geospatial query operator is supported
     */
    boolean isGeospatialOperatorSupported(InputRequest inputRequest) {
        String requestedGeospatialQueryOperator = inputRequest.getGeospatialQueryOperator();

        return ApiController.supportedGeospatialQueryOperators[0].equals(requestedGeospatialQueryOperator) ||
                ApiController.supportedGeospatialQueryOperators[1].equals(requestedGeospatialQueryOperator);
    }
}
