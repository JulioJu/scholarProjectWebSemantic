#!/bin/bash -
# -*- coding: UTF8 -*-

cd "$( dirname "${BASH_SOURCE[0]}")" || exit

cd ./scholarProjectWebSemanticFusekiDatabase || exit

fuseki-server
