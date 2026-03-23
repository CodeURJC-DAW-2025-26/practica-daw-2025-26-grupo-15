#!/bin/bash

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
	echo "Uso: $0 <usuario_dockerhub> [tag]"
	echo "Ejemplo: $0 isabel 1.0.0"
	exit 1
fi

USERNAME="$1"
TAG="${2:-latest}"

if [ -z "$USERNAME" ] || [ -z "$TAG" ]; then
	echo "Uso: $0 <usuario_dockerhub> [tag]"
	echo "Ejemplo: $0 isabel 1.0.0"
	exit 1
fi

docker build -t "$USERNAME/dsgram:$TAG" .