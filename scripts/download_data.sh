#!/bin/bash

# Include file (from https://stackoverflow.com/a/12694189)
DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
source "$DIR/functions.sh"

if [ $# -eq 0 ]; then
    outputDir="$PWD"
    file="gerbil_data.zip"
else
    outputDir="$1"
    file="$outputDir/gerbil_data.zip"
fi

url="https://github.com/dice-group/gerbil/releases/download/v1.2.6/gerbil_data.zip"

if [ ! -d "$outputDir/gerbil_data" ]; then
    mkdir -p "$outputDir/gerbil_data" || error "Could not create gerbil_data directory"
    mkdir -p "$outputDir/gerbil_data/cache" || error "Could not create gerbil_data/cache directory"
    if [ ! -f "$file" ]; then
        echo "Downloading dependencies ... ($url)"
        #curl --retry 4 -L -o "$file" "$url" # Replaced by wget since curl is not available in our docker image
        wget -O "$file" "$url"

        if [ ! -f "$file" ]; then
            error "Couldn't downloading dependency data: $file"
        else
            echo "Extracting dependencies ... "
            unzip "$file" -d "$outputDir"
       fi
   fi
fi