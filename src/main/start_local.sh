# push deps to git repo for proxy machines to fetch.
#!/usr/bin/env bash

cd ../../../_dis_jar
pwd
cp ../Distributed/target/chaos-1.0-jar-with-dependencies.jar .
git add .
git commit -m "auto.script"
git push