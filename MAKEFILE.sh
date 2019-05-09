#!/bin/bash
# -*- coding: UTF8 -*-

# I've also tried with following without success. inotifywait seems doesn't
# like Maven. Maybe too much operations? In fact no any idea wy.
# while inotifywait -rd -o ${tmpfile} --event modify --event move \
#  --event delete --event create "$dir1"; do

cd ./scholarProjectWebSemantic || exit

while true
do
    mvn -P \!webpack -Dspring-boot.run.arguments="fusekiServerEmbedded"
done

