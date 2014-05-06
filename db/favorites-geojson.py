#!/usr/bin/python

import sys

from pyspatialite import dbapi2 as sqlite3

filename = sys.argv[1]
conn = sqlite3.connect(filename)
cur = conn.cursor()

cur.execute("SELECT AsGeoJSON(geom) FROM favorite ORDER BY weight / GreatCircleLength(ExteriorRing(geom)) DESC LIMIT 10")
print '{"type": "FeatureCollection", "features": ['
first = True
for r in cur:
    if r[0] is None:
        continue
    if not first:
        print ',',
    first = False
    print '{"type": "Feature", "properties": {}, "geometry": '
    print r[0]
    print '}'
print ']}'
