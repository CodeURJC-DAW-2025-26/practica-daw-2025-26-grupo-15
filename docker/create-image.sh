#!/bin/bash

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
	echo "Use: $0 <DockerHub_username> [tag]"
	echo "Example: $0 isabel 1.0.0"
	exit 1
fi

USERNAME="$1"
TAG="${2:-latest}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

if [ -z "$USERNAME" ] || [ -z "$TAG" ]; then
	echo "Use: $0 <DockerHub_username> [tag]"
	echo "Example: $0 isabel 1.0.0"
	exit 1
fi

docker build -f "$SCRIPT_DIR/Dockerfile" -t "$USERNAME/dsgram-app:$TAG" "$REPO_ROOT"