#!/bin/bash -
# -*- coding: UTF8 -*-

# shellcheck disable=2009
declare -i pid
pid="$(ps -u "$(users)" -o pid,cmd | grep  "/usr/bin/java -jar /opt/apache-jena-fuseki/fuseki-server.jar" | grep -v grep | awk '{print $1}' )"

if [[ ${pid} -ne 0 ]]
then
    kill "$pid"
else
    exit 10
fi
