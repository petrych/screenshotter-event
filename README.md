### ABOUT
A RESTful webservice that creates screenshots of web pages and stores them in the application folder.

#### Technology stack

- Java 15
- Maven 3
- Spring 5
- Spring Boot 2
- JPA, Hibernate (im-memory DB with some predefined data)
- Selenium
- Chrome Driver executable (*)
- JUnit 5
- Spring WebTestClient

(*) The version of Chrome Driver must correspond to the Chrome browser installed.
chromedriver executable needs to be in the /tools directory.
Download from https://sites.google.com/chromium.org/driver/downloads.

Application is shipped with Chrome Driver for Mac.

### IMPORTANT

The application runs on port *8083*.
To change the port, go to _**.src/main/resources/application.properties**_ and change the value of the ```server.port``` property.


### RUNNING IN LOCAL ENVIRONMENT

#### 1. Build

Build the project and run all unit tests:
```
mvn clean install
```

Build without unit tests:
```
mvn clean install -Dmaven.test.skip=true
```

#### 2. Run

The project can be run as a Spring Boot app by using the main class - _**ScreenshotterApp.java**_.

To run the project **from the command line**, use the Maven command:
```
mvn spring-boot:run
```

#### 3. Use the app in a browser

To access the application open http://localhost:8083/screenshotter/screenshots/. A JSON representation of several screenshots will be shown.
    
To see a screenshot with id 1, call the http://localhost:8083/screenshotter/screenshots/1 URL.

To create a screenshot of the Apple.com webpage, use an application that can send HTTP requests, for example, Postman.
Send a POST request containing "http://apple.com" as plain text in the body. 

#### 4. Use the app via CLI

Get all screenshots (Run the curl command in a new tab)
```
curl --request GET \
--url http://localhost:8083/screenshotter/screenshots/ \
--header 'content-type: application/json'
```

Create a screenshot of the Apple.com webpage (Run the curl command in a new tab)
```
curl --request POST \
--url http://localhost:8083/screenshotter/screenshots/ \
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
docker run -d -p 8083:8083 screenshotter
```

Go in to the container’s file system
```
docker run -ti --entrypoint /bin/sh screenshotter
```

See the logs of the running container
```
docker logs -f [container_name]
```
