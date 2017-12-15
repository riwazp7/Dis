#!/usr/bin/env bash
pwd
cd ../../../_dis_jar
cp ../Distributed/target/chaos-1.0-jar-with-dependencies.jar .
git add .
git commit -m "auto.script"
git push