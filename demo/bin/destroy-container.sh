#!/usr/bin/env bash

[ -z "$1" ] && echo "Usage: $0 <container-name>" && exit 1

echo -n "Destroying ${DB_CONTAINER_NAME}"
docker kill "$1" 2> /dev/null
docker rm "$1" 2> /dev/null
