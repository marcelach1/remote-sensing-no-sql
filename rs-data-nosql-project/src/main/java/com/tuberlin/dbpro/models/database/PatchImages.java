package com.tuberlin.dbpro.models.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Arrays;

/**
 * PatchImages class / collection persisted in database: contains photos of image patch and corresponding id.
 */
@Document(collection = "patch_images")
public class PatchImages {

    /**
     * Id of image patch
     */
    @Id
    private String patchName;

    /**
     * Images of image patch
     */
    private Photo[] photos;

    /**
     * Returns name of image patch.
     * @return name of image patch
     */
    public String getPatchName() { return patchName; }

    /**
     * Sets image patch name.
     * @param patchName name of image patch
     */
    public void setPatchName(String patchName) { this.patchName = patchName; }

    /**
     * Returns 12 photos belonging to the image patch.
     * @return array of image patch photos
     */
    public Photo[] getPhotos() { return photos; }

    /**
     * Sets photos belonging to image patch.
     * @param photos photos to set
     */
    public void setPhotos(Photo[] photos) { this.photos = photos; }

    /**
     * Returns string representation of patch images.
     * @return string representation of patch images
     */
    @Override
    public String toString() {
        return "Images{" +
                "patchName='" + patchName + '\'' +
                ", photos=" + Arrays.toString(photos) +
                '}';
    }
}
