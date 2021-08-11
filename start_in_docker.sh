#!/bin/bash

COPY_DIRS="${GERBIL_COPY_DIRS:-true}"  # If variable not set or null, use default.
COPY_PROPS="${GERBIL_COPY_PROPS:-true}"  

if [ $COPY_DIRS -eq "true" ]; then
    echo "Copying data directories if missing..."
    # copy directories that do not already exist
    cp -r -u /data/gerbil_data/configs/* gerbil_data/configs/
    cp -r -u /data/gerbil_data/datasets/* gerbil_data/datasets/
    cp -r -u /data/gerbil_data/resources/* gerbil_data/resources/
    cp -r -u /data/gerbil_data/systems/* gerbil_data/systems/
fi

if [ $COPY_PROPS -eq "true" ]; then
    echo "Copying properties files if missing..."
    # Copy properties files
    cp -v -u /data/properties/* /usr/local/tomcat/gerbil_properties/
fi

# Start Tomcat
echo "Starting Tomcat..."
catalina.sh run