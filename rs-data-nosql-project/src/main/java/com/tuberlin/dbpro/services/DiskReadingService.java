package com.tuberlin.dbpro.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuberlin.dbpro.models.database.GeoJson;
import com.tuberlin.dbpro.models.database.PatchImages;
import com.tuberlin.dbpro.models.database.Photo;
import com.tuberlin.dbpro.models.database.Properties;
import com.tuberlin.dbpro.models.diskreading.Coordinates;
import com.tuberlin.dbpro.models.diskreading.MetaJson;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.*;

/**
 * Service class for reading BigEarthNet files from disk into database.
 */
@Service
public class DiskReadingService {

    /**
     * DatabaseService instance as persistence interface.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Label mapping service instance.
     */
    @Autowired
    private LabelService labelService;

    /**
     * Number of currently read patches.
     */
    private int numberOfReadPatches;

    /**
     * Map containing the photo title and corresponding binary photo of the current image patch.
     */
    private Map<String, Binary> currentPatchPhotos;

    /**
     * Object mapper for manual Jackson parsing.
     */
    private ObjectMapper objectMapper;

    /**
     * Coordinate reference system factory instance needed for coordinate system transformation.
     */
    private CRSFactory factory = new CRSFactory();

    /**
     * Coordinate reference system instance containing target coordinate system EPSG:4326.
     */
    private CoordinateReferenceSystem dstCrs = factory.createFromName("EPSG:4326");

    /**
     * Reads entire BigEarthNet dataset from disk into database.
     */
 //Comment after loading the BigEarthNet files
/*
    @PostConstruct
    public void saveBigEarthNetToDatabase () {
        this.numberOfReadPatches = 0;
        this.objectMapper = new ObjectMapper();
        this.currentPatchPhotos = new TreeMap<>();

        // change this according to the location of your BigEarthNet dataset
        //File[] allObjects = new File ("../data").listFiles();
        //File[] allObjects = new File ("/projects/data/BigEarthNet-10000Examples").listFiles();
        //File[] allObjects = new File ("/media/charfuelan/Backup-2019/projects/data/BigEarthNet-v1.0").listFiles();
        File[] allObjects = new File ("/data/sentinel2/BigEarthNet-v1.0").listFiles();

        if (allObjects != null && allObjects.length > 0) {
            this.readFiles(allObjects);
        } else {
            System.out.println("NO DATA SET FOUND!");
        }
        System.out.println("Total image patches collected: " + String.valueOf(numberOfReadPatches));
    }
*/

    /**
     * Reads in given image patch folder from disk.
     * @param files file array containing folder files
     */
    private void readFiles(File[] files) {
        BufferedReader reader = null;
        StringBuilder stringBuilder = null;

        for(File file: files) {
            if(file.isDirectory()) {
                File[] directoryFiles = file.listFiles();
                if(directoryFiles != null) {
                    // more efficient persisting process as json files are always read last after all 12 photos are read:
                    // two instead of 13 insertions
                    // source inspiration for sorting mechanism:
                    // https://stackoverflow.com/questions/203030/best-way-to-list-files-in-java-sorted-by-date-modified
                    // last access: 02 July 2020, around 1PM UTC+1
                    Arrays.sort(directoryFiles, Comparator.comparing(File::getName));

/* Commented just to load the json metadata and not the tiff files
                    for(int i = 0; i < 12; i++) { // read images first into memory
                        this.saveImageIntoImageBuffer(directoryFiles[i]);
                    }
*/

                    // then save objects from memory into database
                    this.numberOfReadPatches++;
                    this.readAndSaveGeoJsonToDatabase(directoryFiles[12], reader, stringBuilder);
/* Commented just to load the json metadata and not the tiff files
                    this.saveBufferedPatchImagesToDatabase();
 */
                }
            }
        }
    }

    /**
     * Adds given image file as to internal image buffer (in-memory).
     * @param file File object containing an image
     */
    private void saveImageIntoImageBuffer(File file) {
        try {
            Binary tif = new Binary(BsonBinarySubType.BINARY, Files.readAllBytes(file.toPath()));
            String title = file.getName().substring(0, file.getName().length() - 4);
            this.currentPatchPhotos.put(title, tif);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves all image patch photos contained in the internal image buffer into database.
     */
    private void saveBufferedPatchImagesToDatabase() {
        Photo[] photos = new Photo[12]; // 12 bands to save
        int currentPhotoIndex = 0;

        for(Map.Entry<String,Binary> entry: this.currentPatchPhotos.entrySet()) {
            Photo photo = new Photo();
            photo.setTitle(entry.getKey());
            photo.setPhoto(entry.getValue());
            photos[currentPhotoIndex] = photo;
            currentPhotoIndex++;
        }

        String patchName = photos[0].getTitle().substring(0, photos[0].getTitle().length() - 4);
        PatchImages patchImages = new PatchImages();
        patchImages.setPatchName(patchName);
        patchImages.setPhotos(photos);
        this.databaseService.persistPatchImages(patchImages);

        this.currentPatchPhotos.clear();
    }

    /**
     * Maps read meta JSON file to internal GeoJson database representation and saves it the database.
     * @param file file object containing meta JSON
     * @param reader BufferedReader instance for reusability
     * @param stringBuilder StringBuilder instance for reusability
     */
    private void readAndSaveGeoJsonToDatabase(File file, BufferedReader reader, StringBuilder stringBuilder) {
        String patchName = file.getName().substring(0, file.getName().indexOf("_labels_metadata.json"));
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            MetaJson metaJson = this.objectMapper.readValue(stringBuilder.toString(), MetaJson.class);
            GeoJson geoJson = this.getGeoJson(metaJson, patchName);
            this.databaseService.persistGeoJson(geoJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a GeoJson object from a given read meta JSON object.
     * @param metaJson read meta JSON from disk
     * @param patchName name of the image patch
     * @return parsed GeoJson file to save in database
     */
    GeoJson getGeoJson(MetaJson metaJson, String patchName) {
        Properties properties = new Properties();
        properties.setPatchName(patchName);
        properties.setLabels(this.labelService.getPassedLabelsEncoded(metaJson.getLabels()));

        GeoJson geoJson = new GeoJson();
        geoJson.setType("Feature");
        String proj = metaJson.getProjection();
        String srcCrs = proj.substring(proj.length() - 8, proj.length() - 3);
        geoJson.setLocation(this.getGeoJsonPolygonFromReadCoordinates(metaJson.getCoordinates(), srcCrs));
        geoJson.setProperties(properties);

        return geoJson;
    }

    /**
     * Transforms a given read coordinate object from EPSG:32xyz to EPSG:4326.
     * @param coordinates coordinate object in source CRS
     * @param srcCrs EPSG code of source CRS
     * @return GeoJsonPolygon object in EPSG:4236
     */
    private GeoJsonPolygon getGeoJsonPolygonFromReadCoordinates(Coordinates coordinates, String srcCrs) {
        // Proj4j example: https://stackoverflow.com/questions/38906402/proj-4-java-convert-coordinates-from-wgs84-to-epsg4141
        // double rounding: https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
        // last access: 02 July 2020, around 1PM UTC+1
        ProjCoordinate ul_src = new ProjCoordinate(coordinates.getUlx(), coordinates.getUly());
        CoordinateReferenceSystem sourceCrs = factory.createFromName("EPSG:" + srcCrs);
        BasicCoordinateTransform coordinateTransformer = new BasicCoordinateTransform(sourceCrs, dstCrs);

        ProjCoordinate upper_left = coordinateTransformer.transform(ul_src, new ProjCoordinate());
        double upper_left_x = Math.round(upper_left.x * 10000000000000d) / 10000000000000d;
        double upper_left_y = Math.round(upper_left.y * 10000000000000d) / 10000000000000d;

        ProjCoordinate lr_src = new ProjCoordinate(coordinates.getLrx(), coordinates.getLry());
        ProjCoordinate lower_right = coordinateTransformer.transform(lr_src, new ProjCoordinate());
        double lower_right_x = Math.round(lower_right.x * 10000000000000d) / 10000000000000d;
        double lower_right_y = Math.round(lower_right.y * 10000000000000d) / 10000000000000d;

        Point upperLeft = new Point(upper_left_x, upper_left_y);
        Point upperRight = new Point(lower_right_x, upper_left_y);
        Point lowerRight = new Point(lower_right_x, lower_right_y);
        Point lowerLeft = new Point(upper_left_x, lower_right_y);

        // fifth point needed even for rectangles to close GeoJsonPolygon ring
        return new GeoJsonPolygon(upperLeft, upperRight, lowerRight, lowerLeft, upperLeft);
    }

    /**
     * Sets label service for test compatibility (injected in production).
     * @param labelService Label service for test compatibility
     */
    void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }
}
