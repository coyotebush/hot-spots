#!/bin/sh
set -e
cd $(dirname $0)
wget http://download.geonames.org/export/dump/cities1000.zip
unzip cities1000.zip
mkdir -p ../assets/
cp seed.sqlite ../assets/
spatialite ../assets/seed.sqlite < load-cities.sql
rm cities1000.zip cities1000.txt
