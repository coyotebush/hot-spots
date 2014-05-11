#!/usr/bin/python

import sys

from pyspatialite import dbapi2 as sqlite3

beta = 2 ** (1.0/(86400*7)) # double in a week
alpha = float("Inf")
meters_per_degree = 110000 # approximate, varies with latitude
start_time = 1262304000    # 2010-01-01; makes math more manageable

filename = sys.argv[1]
conn = sqlite3.connect(filename)
cur = conn.cursor()

locs = cur.execute("SELECT id, time FROM location " +
                   "WHERE id > Coalesce((SELECT Max(last_location) FROM favorite), 0) ORDER BY time ASC").fetchall()
for i, loc in enumerate(locs):
    # Find the previous point
    prev = cur.execute("SELECT id, time FROM location WHERE time < ? " +
                       "ORDER BY time DESC LIMIT 1", (loc[1],)).fetchone()

    if prev is None:
        continue

    print "Processing location id {}".format(loc[0])

    # Create a new favorite from these two points
    # weight = (loc[1] - prev[1]) * beta ** (loc[1] + prev[1])
    params = { "first": prev[0],
               "second": loc[0],
               # "weight": weight,
               "beta": beta,
               "start": start_time,
               "factor": meters_per_degree,
               "city": "Anywhere" }
    cur.execute("""INSERT INTO favorite (name, weight, geom, last_location)
                   SELECT :city name,
                     (b.time - a.time) * Pow(:beta, (a.time + b.time - 2 * :start)/2) weight,
                     ConvexHull(GUnion(Buffer(a.geom, Coalesce(a.accuracy, 10.0) / :factor),
                       Buffer(b.geom, Coalesce(b.accuracy, 10.0) / :factor))) geom,
                     b.id last_location
                   FROM location a, location b
                   WHERE a.id = :first AND b.id = :second""", params)
    newid = cur.lastrowid
    # print "Inserted row {}".format(newid)

    # If the new favorite overlaps with existing one(s):
    params = {"newid": newid, "locid": loc[0]}

    #   If the merge is better than each individually, merge destructively
    #   into the current best one
    cur.execute("""UPDATE favorite
                   SET weight =
                       (SELECT Sum(weight) FROM favorite
                        WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid))),
                     geom =
                       (SELECT ConvexHull(GUnion(geom)) FROM favorite
                        WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid))),
                     last_location = :locid
                   WHERE id <> :newid AND Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid))
                     AND (weight / GreatCircleLength(ExteriorRing(geom))) <
                       (SELECT Sum(weight) / GreatCircleLength(ExteriorRing(ConvexHull(GUnion(geom)))) FROM favorite
                        WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid)))
                     AND (weight / GreatCircleLength(ExteriorRing(geom))) >=
                       (SELECT Max(weight / GreatCircleLength(ExteriorRing(geom))) FROM favorite
                        WHERE id <> :newid AND Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid)))""",
                     params)

    merged = cur.rowcount > 0
    if merged:
        # print "Merged!"
        cur.execute("""DELETE FROM favorite
                       WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid))
                         AND last_location <> :locid OR id = :newid""", params)
        print "Merged with {} others".format(cur.rowcount)
    #   Otherwise, add some weight and delete the new one
    else:
        cur.execute("""UPDATE favorite
                       SET weight = weight +
                        (SELECT weight / Area(geom) FROM favorite WHERE id = :newid) *
                        Area(Intersection(geom, (SELECT geom FROM favorite WHERE id = :newid))),
                        last_location = :locid
                       WHERE id <> :newid AND Intersects(geom, (SELECT geom FROM favorite WHERE id = :newid))""",
                       params)

        intersections = cur.rowcount
        if intersections > 0:
            print "Added weight to {} others, deleting".format(intersections)
            cur.execute("""DELETE FROM favorite
                           WHERE id = :newid""", params)

    # conn.commit()
    # sys.exit(0)
    if i % 50 == 0:
        conn.commit()
        print "Committed"

conn.commit()

