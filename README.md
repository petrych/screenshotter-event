### ABOUT
A RESTful webservice that creates screenshots of web pages and stores them in the application folder.

#### Technology stack

- Java 15
- Maven 3
- Spring 5
- Spring Boot 2
- JPA, Hibernate
- MySQL 8
- Selenium
- Chrome Driver executable (*)
- JUnit 5
- Spring WebTestClient

(*) The version of Chrome Driver must correspond to the Chrome browser installed.
chromedriver executable needs to be in the /tools directory.
Download from https://sites.google.com/chromium.org/driver/downloads.

Application is shipped with Chrome Driver for Mac.

### IMPORTANT

The application runs on port *8080*.
To change the port, go to _**.src/main/resources/application.properties**_ and change the value of the ```server.port``` property.


### RUNNING IN LOCAL ENVIRONMENT

#### 1. Set up MySQL

### Install MySQL components

- MySQL Server (locally):
  https://dev.mysql.com/doc/refman/8.0/en/installing.html

- MySQL Workbench (or another MySQL client application)
  https://dev.mysql.com/doc/workbench/en/

Run both applications.

### Create the database

The database is called "screenshotter" and runs on port *3306*.
To change these settings, go to _**.src/main/resources/application.properties**_.

Open a MySQL client and establish the connection to the database.

Open a terminal (command prompt in Microsoft Windows) and open the MySQL client as a user who can create new users.
For example, on a Linux system, use the following command:
```
sudo mysql --password
```

To create a new database, run the following commands at the mysql prompt:
- to create the new database:
```mysql> create database screenshotter;```
- to create the user:
```mysql> create user 'user'@'%' identified by '123'; ```
- to give all privileges to the new user on the newly created database
```mysql> grant all on screenshotter.* to 'user'@'%'; ```

### Initial schema and data available

Hibernate automatic schema creation is disabled:
```spring.jpa.hibernate.ddl-auto=none```

Initial schema and data is populated via _**src/main/resources/data.sql**_ script
```spring.sql.init.mode=always```

To change these settings, go to _**.src/main/resources/application.properties**_.

#### 2. Build

Build the project and run all unit tests:
```
mvn clean install
```

Build without unit tests:
```
mvn clean install -Dmaven.test.skip=true
```

#### 3. Run

The project can be run as a Spring Boot app by using the main class - _**ScreenshotterApp.java**_.

To run the project **from the command line**, use the Maven command:
```
mvn spring-boot:run
```

#### 4. Use the app in a browser

To access the application open http://localhost:8080/screenshotter/screenshots/. A JSON representation of several screenshots will be shown.
For the first run after installation, the list will be empty.

To create a screenshot of the Apple.com webpage, use an application that can send HTTP requests, for example, Postman.
Send a POST request containing "http://apple.com" as plain text in the body. 

To see a screenshot with id 1, call the http://localhost:8080/screenshotter/screenshots/1 URL.

#### 5. Use the app via CLI

Get all screenshots (Run the curl command in a new tab)
```
curl --request GET \
--url http://localhost:8080/screenshotter/screenshots/ \
--header 'content-type: application/json'
```

Create a screenshot of the Apple.com webpage (Run the curl command in a new tab)
```
curl --request POST \
--url http://localhost:8080/screenshotter/screenshots/ \
--header 'content-type: text/plain' \
--data "http://apple.com"
```

### RUNNING IN A DOCKER CONTAINER

#### 1. Build the image named screenshotter
```
docker build --tag screenshotter .
```

#### 2. Run the image in the container
```
docker run -d -p 8080:8080 screenshotter
```

Go in to the container’s file system
```
docker run -ti --entrypoint /bin/sh screenshotter
```

See the logs of the running container
```
docker logs -f [container_name]
```
