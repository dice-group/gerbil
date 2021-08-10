#!/bin/bash

# Include file (from https://stackoverflow.com/a/12694189)
DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
source "$DIR/functions.sh"

#####################################################################
# Check for dbpedia sameAs index
echo "Checking dbpedia sameAs index..."
if [ ! -d "gerbil_data/indexes/dbpedia" ]; then
    echo "Couldn't find a dbpedia sameAs index"
    if yesno "Should the index be downloaded (~1GB zipped, ~2GB extracted)? (yes/no): "; then
        mkdir -p "gerbil_data/indexes/dbpedia" || error "Could not create gerbil_data/indexes/dbpedia directory"
        file="gerbil_data/indexes/dbpedia/dbpedia_index.zip"
        url="https://hobbitdata.informatik.uni-leipzig.de/gerbil/dbpedia_index_2016.zip"
        echo "Downloading index ... ($url)"
        curl --retry 4 -L -o "$file" "$url"

        if [ ! -f "$file" ]; then
            echo "Couldn't downloading index file: $file"
        else
            echo "Extracting index ... "
            unzip "$file" -d "gerbil_data/indexes/dbpedia"
        fi
    fi
fi

#####################################################################
# Check for dbpedia entity check index
echo "Checking dbpedia entity check index..."
if [ ! -d "gerbil_data/indexes/dbpedia_check" ]; then
    echo "Couldn't find a dbpedia entity check index"
    if yesno "Should the index be downloaded (~0.3GB zipped, ~0.7GB extracted)? (yes/no): "; then
        mkdir -p "gerbil_data/indexes/dbpedia_check" || error "Could not create gerbil_data/indexes/dbpedia_check directory"
        file="gerbil_data/indexes/dbpedia_check/dbpedia_check_index.zip"
        url="https://hobbitdata.informatik.uni-leipzig.de/gerbil/dbpedia_check_index_2017.zip"
        echo "Downloading index ... ($url)"
        curl --retry 4 -L -o "$file" "$url"

        if [ ! -f "$file" ]; then
            echo "Couldn't downloading index file: $file"
        else
            echo "Extracting index ... "
            unzip "$file" -d "gerbil_data/indexes/dbpedia_check"
        fi
    fi
fi