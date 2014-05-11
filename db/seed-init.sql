-- $ spatialite seed.sqlite < seed-init.sql

CREATE TABLE city (id INTEGER PRIMARY KEY, name TEXT, latitude REAL, longitude REAL, country TEXT, admin1 TEXT, admin2 TEXT);
SELECT AddGeometryColumn("city", "geom", 4326, "POINT", "XY");
SELECT CreateSpatialIndex("city", "geom");

CREATE TABLE location (id INTEGER PRIMARY KEY, provider TEXT, accuracy REAL, time INTEGER);
SELECT AddGeometryColumn("location", "geom", 4326, "POINT", "XY");
SELECT CreateSpatialIndex("location", "geom");

CREATE TABLE favorite (id INTEGER PRIMARY KEY, name TEXT, weight REAL, last_location INTEGER);
SELECT AddGeometryColumn("favorite", "geom", 4326, "POLYGON", "XY");
SELECT CreateSpatialIndex("favorite", "geom");

