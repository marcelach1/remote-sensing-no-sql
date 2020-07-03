package com.tuberlin.dbpro.repositories;

import com.tuberlin.dbpro.models.database.PatchImages;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Image patch repository for MongoDB interaction - can be used to query images, but not relevant for our project.
 */
public interface PatchImageRepository extends MongoRepository<PatchImages, String> {
}
