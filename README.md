General application setup:
__________________

1. Make sure you have retain our project folder hierarchy (with the BigEarthNet folders in the data folder)
2. Download MongoDB 4.2.6
3. Install using custom settings with MongoDB as service
4. Give MongoDB folder write permissions
5. Navigate into data folder in MongoDB directory
6. Create folder named "db"
7. Copy path of db folder -> <DB_PATH>
8. Navigate to advanced system settings and add MongoDB's bin folder as global path variables
9. Open terminal
10. Set database path: mongod --dbpath "<DB_PATH>"
11. Leave current terminal opened and open second terminal
12. Execute "mongo" to enter Mongo shell
13. Navigate into our database which automatically creates it: "use db_test"
14. Copy to clipboard: 
"db.createUser( { user: "root",
    pwd: "root",
    roles: [ { role: "readWrite", db: "db_test" }],
      } );" 
15. Insert copied content into opened terminal (right-click in terminal) and execute (this is the user Spring will use to access the database)
16. Execute "show users" to show created user
17. If you have already loaded the databse with data, comment out @PostConstruct above saveBigEarthDatabse method in DiskReadingService.java not to populate your database with the same data twice
18. Run our Java server in background using the IDE of your choice
19. Wait for a while, until you see in Spring console, that all of the files have been successfully loaded (just wait until Spring has initialized the application correctly if you commented out @PostConstruct)
20. Execute "db.geojsons.find()" or "db.patch_images.find()" in the Mongo shell to print our collections in the database
21. Open 127.0.0.1:8080 and start exploring (note: we recommend Chrome or Edge as Firefox suffers from native drawing issues with Leaflet)

Performance measurement: 
________________________

For benchmarking the performance, we recommend the following procedure:
0. Make sure MongoDB is configured as described above and running
1. Run the application with @PostConstruct initially to fill the database with the data and create indices
2. Find a consistent example query request x and explain the query, e.g.: 

"db.geojsons.explain().find({ location: { $geoWithin: { $geometry: { type: "Polygon" , coordinates: [[[21.763916,63.364445],[21.763916,63.741201],[22.247314,63.741201],[22.247314,63.364445],[21.763916,63.364445]]] } } }, "properties.labels": {$all: ["Mixed forest","Sea and ocean"]} })"

3. Open the Mongo shell
4. Verify the available indices in shell: "db.geojsons.getIndexes()"
5. Execute x in the Mongo shell
6. See that MongoDB uses the custom-created indices and that the queries are fast
7. Play around with the frontend and watch the browser's and server's console windows
8. Delete all custom indices by executing in shell: "db.geojsons.dropIndex("<INDEX_NAME>")" -> NOTE: Before this step, make sure you have explored the application enough with indices enabled (you need to reload the entire data set into your database to create the indexes again after this step)
9. Execute x again in the Mongo shell
10. Redo step 7
11. See that MongoDB does not use the custom-creates indices anymore and that the queries are significantly slower

Tests:
______
As a best practice, we restricted the unit tests so that we do not access any real resources (BigEarthNet folders or even the database) during the tests.

Documentation:
______________
The entire implementation is commented with Javadoc comments. Additionally, we provide a Javadoc webpage documentation in the documentation folder (entry point: index.html).

Note:
_____
For convenience and quick prototyping, we decided to include 35 image patch folders of the BigEarthNet in the data folder of this repository. Replace its content with the original BigEarthNet data set folders when testing the application.
