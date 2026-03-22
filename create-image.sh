#!/bin/bash

USERNAME=$1
docker build -t $USERNAME/dsgram:latest .