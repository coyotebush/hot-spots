Hot Spots
=========

Hot Spots is an Android application which continually records the device's position and identifies probable "favorite" locations.

Developed as a course project for [ID2012 Ubiquitous Computing](https://www.kth.se/student/kurser/kurs/ID2012/?l=en) at KTH. Some project documentation is in the [wiki](//github.com/coyotebush/hot-spots/wiki).

Development
-----------
Add <https://github.com/alexbirkett/cwac-locpoll> as an Android library project in your IDE.

An empty version of `seed.sqlite` (which the application uses as a starting point when it has no existing database) is in `db/`. To create a version in `assets/` with GeoNames data included:

 * Install SpatiaLite (tested with the Debian `spatialite-bin` package, versions `3.0.0~beta20110817-3+deb7u1` (wheezy) and `4.1.1-4`)
 * `cd db; ./init.sh`
