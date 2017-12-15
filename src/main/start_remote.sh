#!/usr/bin/env bash
rm -rf _dis_jar
git clone https://github.com/riwazp7/_dis_jar.git
nohup java -cp chaos-1.0.jar impl.StartProxy </dev/null 2>&1 | tee chaos.log &