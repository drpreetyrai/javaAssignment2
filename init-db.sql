CREATE DATABASE IF NOT EXISTS whisky_db CHARSET utf8;

DROP TABLE IF EXISTS smartcamera ;

CREATE TABLE smartcamera  (
    deviceid VARCHAR(255),
    domain VARCHAR(255),
    city VARCHAR(255),
    location VARCHAR(255),
    deviceType VARCHAR(255),
    PRIMARY KEY (deviceid)
);

INSERT INTO smartcamera (deviceid, domain, state, city, location, deviceType) VALUES ("123-asdasd-123", "smart-transport","MH","Pune",[34.56,76.34],"smart-camera");
INSERT INTO smartcamera (deviceid, domain, state, city, location, deviceType) VALUES ("123-asdasd-256", "smart-transport","Maharastra","Mumbai",[39.56,74.43],"smart-camera");
INSERT INTO smartcamera (deviceid, domain, state, city, location, deviceType) VALUES ("123-asdasd-546", "smart-transport",""West Bengal","Kolkata",[55.56,88.34],"smart-camera");