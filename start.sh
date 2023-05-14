#!/bin/bash

source scripts/functions.sh

./scripts/download_data.sh

#####################################################################
# Check for property file
echo "Checking properties files..."
dir="src/main/properties"
file="$dir/gerbil_keys.properties"

if [ ! -f "$file" ]; then
	echo "Creating empty $file file"
	mkdir -p "$dir" || error "Could not create $dir directory"
	echo "##############################################################################"  > $file
	echo "# This is the properties file for keys of several annotator web services.    #"  >> $file
	echo "#                      IT SHOULD NOT BE DISTRIBUTED!!!                       #"  >> $file
	echo "##############################################################################"  >> $file
fi

./scripts/download_indexes.sh

echo "Building and starting GERBIL..."
mvn clean org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Dmaven.tomcat.port=1234
