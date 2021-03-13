#!/usr/bin/env sh

./mvnw clean package

mkdir -p debug/addons/jpplag
mkdir -p debug/workspace

cp jpplag-addon/target/jpplag-addon-*.jar debug/addons/jpplag.jar
cp svn-datacollector/target/svn-datacollector-*.jar debug/addons/svn-datacollector.jar
cp core/target/core-*.jar debug/jacat.jar

curl -o debug/addons/jpplag/jplag-2.12.1.jar https://github.com/jplag/jplag/releases/download/v2.12.1-SNAPSHOT/jplag-2.12.1-SNAPSHOT-jar-with-dependencies.jar