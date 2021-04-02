#!/bin/sh
filename=gosoanSensorEvent.fbs

flatc -g -o ./server/go $filename
flatc --kotlin -o ./client/android/app/src/main/java $filename