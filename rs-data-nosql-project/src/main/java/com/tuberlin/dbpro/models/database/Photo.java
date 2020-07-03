package com.tuberlin.dbpro.models.database;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

/**
 * Database model class containing single photo, its title and corresponding photo id.
 */
public class Photo {

    /**
     * Id of photo.
     */
    @Id
    private String id;

    /**
     * Title of photo.
     */
    private String title;

    /**
     * Binary representation of photo.
     */
    private Binary photo;

    /**
     * Returns the title of the photo.
     * @return title of the photo
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of the photo.
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns binary representation of the photo.
     * @return binary representation of the photo
     */
    public Binary getPhoto() {
        return photo;
    }

    /**
     * Sets binary photo representation.
     * @param photo Photo to set
     */
    public void setPhoto(Binary photo) {
        this.photo = photo;
    }

    /**
     * Returns string representation of the photo.
     * @return string representation of the photo
     */
    @Override
    public String toString() {
        return "Photo{" +
                "title='" + title + '\'' +
                ", image=" + photo +
                '}';
    }
}
