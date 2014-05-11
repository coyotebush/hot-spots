#!/usr/bin/python

import sys
import calendar

from pyspatialite import dbapi2 as sqlite3
from lxml import etree
import iso8601

if len(sys.argv) < 3:
    print "Usage: {} db.sqlite track.kml".format(sys.argv[0])
    sys.exit(1)

conn = sqlite3.connect(sys.argv[1])
cur = conn.cursor()

tree = etree.parse(sys.argv[2])
tracks = tree.xpath(".//gx:Track",
                    namespaces={'gx': 'http://www.google.com/kml/ext/2.2'})

for track in tracks:
    count = 0
    when = 0
    for node in track.iterchildren():
        if node.tag == "{http://www.opengis.net/kml/2.2}when":
            # convert to UNIX time
            when = iso8601.parse_date(node.text)
            when = calendar.timegm(when.utctimetuple())
        elif node.tag == "{http://www.google.com/kml/ext/2.2}coord":
            lon, lat, alt = node.text.split()
            cur.execute("insert into location (provider, time, geom) " +
                        "values (?, ?, MakePoint(?, ?, 4326))",
                        ("kml", when, float(lon), float(lat)))
            count += 1

    print "Imported track with {} points".format(count)
    conn.commit()

conn.close()
