#!/bin/bash

export gerbil_home=~/gerbil/

gerbil_data=$gerbil_home/gerbil_data

data_url="https://github.com/dice-group/gerbil/releases/download/v1.2.6/gerbil_data.zip"
mapping_url="http://139.18.2.164/mroeder/gerbil/dbpedia_index.zip"
index_url="http://139.18.2.164/mroeder/gerbil/dbpedia_check_index.zip"

# Check for dependencies
if [ ! -e $gerbil_data ]
then
  echo "gerbil_data is not available locally, downloading.."
  
  zipped_data_file="/tmp/gerbil_data.zip"
  zipped_mapping_file="/tmp/dbpedia_index.zip"
  zipped_index_file="/tmp/dbpedia_check_index.zip"
  
  mapping_file="$gerbil_data/indexes/dbpedia"
  index_file="$gerbil_data/indexes/dbpedia_check"
  
  echo " .. downloading gerbil_data file to $zipped_data_file"
  curl --retry 4 -L -o "$zipped_data_file" "$data_url"

  echo "extracting .."
  mkdir -p "$gerbil_home"
  unzip "$zipped_data_file" -d "$gerbil_home"
  mkdir -p "$gerbil_data/indexes/"

  echo " .. downloading mappings to $zipped_mapping_file"
  curl --retry 4 -L -o "$zipped_mapping_file" "$mapping_url"
    
  echo "extracting .."
  unzip "$zipped_mapping_file" -d "$mapping_file"

  echo " .. downloading indexes to $zipped_index_file"
  curl --retry 4 -L -o "$zipped_index_file" "$index_url"
    
  echo "extracting .."
  unzip "$zipped_index_file" -d "$index_file"
  
  echo "download complete"
fi

echo "starting the docker container"

docker pull philippkuntschik/gerbil:latest

docker run  -d \
            --rm \
            --name gerbil \
            -p 8080:8080 \
            -v $gerbil_data/:/gerbil_data \
            -v /var/logs/:/logs/ \
            philippkuntschik/gerbil
