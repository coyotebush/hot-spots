ATTACH 'hotspots.sqlite' AS old;
INSERT INTO location (id, provider, accuracy, time, geom) SELECT id, provider, accuracy, time/1000, geom FROM old.location;
INSERT INTO favorite (name, geom) SELECT name, Buffer(geom, 0.01) FROM city WHERE name='Kista';
