DROP TABLE IF EXISTS SCREENSHOT;

CREATE TABLE SCREENSHOT (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  uri VARCHAR(250) NOT NULL,
  date_time_created VARCHAR(250) NOT NULL
);

INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (4, 'screenshot-1-com.png', 'http://localhost:8080/screenshotter/screenshots/4', '2020-05-04');
INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (5, 'screenshot-2-com.png', 'http://localhost:8080/screenshotter/screenshots/5', '2020-05-05');
INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (6, 'drive-google-com.png', 'http://localhost:8080/screenshotter/screenshots/6', '2020-05-06');
