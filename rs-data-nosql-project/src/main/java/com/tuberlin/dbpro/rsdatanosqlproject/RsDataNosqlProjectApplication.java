package com.tuberlin.dbpro.rsdatanosqlproject;

import com.tuberlin.dbpro.controllers.ApiController;
import com.tuberlin.dbpro.repositories.GeoJsonRepository;
import com.tuberlin.dbpro.repositories.PatchImageRepository;
import com.tuberlin.dbpro.services.DatabaseService;
import com.tuberlin.dbpro.services.DiskReadingService;
import com.tuberlin.dbpro.services.FrontendService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Spring application class: runs application.
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {ApiController.class, DiskReadingService.class,
        FrontendService.class, DatabaseService.class, GeoJsonRepository.class, PatchImageRepository.class})
@EnableMongoRepositories(basePackages = {"com.tuberlin.dbpro.repositories"})
public class RsDataNosqlProjectApplication {

    /**
     * Main method of the application - runs the entire application.
     * @param args additional arguments for run configuration
     */
    public static void main(String[] args) {
        SpringApplication.run(RsDataNosqlProjectApplication.class, args);
    }
}
