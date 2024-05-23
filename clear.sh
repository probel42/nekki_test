#!/bin/sh

docker rmi nekki-backend
#docker rmi nekki-backend_build

docker volume rm nekki_backend_build_volume

#docker volume rm $(docker volume ls -q)