#!/bin/sh

docker rmi ibelan_nekki_backend
#docker rmi ibelan_nekki_backend_build

docker volume rm ibelan_nekki_backend_build_volume

#docker volume rm $(docker volume ls -q)