DROP TABLE IF EXISTS SCREENSHOT;

CREATE TABLE SCREENSHOT (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  uri VARCHAR(250) NOT NULL,
  date_time_created VARCHAR(250) NOT NULL
);

INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (1, 'google-com.png', 'http://localhost:8083/screenshotter/screenshots/1', '2020-05-01');
INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (2, 'stackoverflow-com.png', 'http://localhost:8083/screenshotter/screenshots/2', '2020-05-02');
INSERT INTO SCREENSHOT (id, name, uri, date_time_created) VALUES (3, 'google-2-com.png', 'http://localhost:8083/screenshotter/screenshots/3', '2020-05-14');
