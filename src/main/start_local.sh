#!/usr/bin/env bash
cd ../../_dis_jar
cp ../Distributed/target/cchaos-1.0-jar-with-dependencies.jar .
git add .
git commit -m "auto.script"
git push