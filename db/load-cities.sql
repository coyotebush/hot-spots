CREATE VIRTUAL TABLE cities1000
 USING VirtualText("cities1000.txt", "UTF-8", 0, POINT);
DELETE FROM city;
INSERT INTO city (id, name, latitude, longitude, country, admin1, admin2)
 SELECT COL001, COL002, COL005, COL006, COL009, COL011, COL012
 FROM cities1000;
DROP TABLE cities1000;
UPDATE city SET geom = MakePoint(longitude, latitude, 4326);
