# Course Work
## hospital-health-API
### Task description:
**Create an API that allows you to receive and transmit data on the health of light-sick patients in the hospital, 
which are taken from trackers (pressure, temperature, heart rate). It should be noted that patients are in the wards 
and buildings of the hospital, and are not displayed in a large tabular list.** \

Those key points should be followed:
1. Draw a diagram of the classes that will be used to complete the course work and indicate the relationships between them
2. Write the appropriate classes using the lombok library to reduce the amount of code
3. Implement REST services for all entities using Spring Boot. It is necessary to implement operations: GET/POST/PUT/DELETE. Operation GET/2 returns the entity with id equal to 2. Operation /GET - returns all entities that are present in the system
4. Divide the code into controllers, services and data access level
5. Linking controllers, services, and data access levels should be done using dependency inversion
6. Implement data storage and reading from the .csv file. Important: Each entity is stored in a separate file.
7. If the file does not exist for the entity, it must be created. The file name must contain the date of creation, for example: fish-2022-05-27.csv
8. Each file should contain headers(matching the names of the attributes of the designed classes) only in the first line.
9. When you start the application, all entities should be read from the file and saved in the hash map. When reading data, you should search for all files for the entity that were created in the current month(for example, all files created in June if the program runs in June)
10. The course work code should be available as a Pull Request on GitHub
11. The project must contain README.md with a description of the task and step-by-step instructions for starting the program(you can see them below)
12. The project must use maven to build the project
13. The code should be checked using SpotBugs and CheckStyle
14. The code should contain unit tests to check the logic of writing and searching for files on the file system
15. Create a set (collection) of REST queries that test the performance of developed services


### Instructions for starting the program:
1. Clone the project from GitHub
2. Open Command Line and proceed to the project's path where it was cloned
3. Run "mvn spring-boot:run" command and wait till the application is fully loaded and started. Make sure that port 
   8888 is not occupied by another process
4. Use the API following Postman collection!
5. To stop the application, open the Command Line that runs the application, press "Ctrl + C", enter "Y" and press 
   "Enter". The application should be shutdown normally, saving all the changes to appropriate files