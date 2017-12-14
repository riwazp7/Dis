#!/usr/bin/env bash
nohup java -cp chaos-1.0.jar impl.<class> </dev/null 2>&1 | tee chaos.log &