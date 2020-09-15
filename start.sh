#!/bin/bash

echo "Checking dependencies..."
file="gerbil_data/gerbil_data.zip"
url="https://github.com/AKSW/gerbil/releases/download/v1.2.4/gerbil_data.zip"

if [ ! -d "gerbil_data" ]; then
    mkdir -p "gerbil_data" || exit 1
    if [ ! -f "$file" ]; then
        echo "Downloading dependencies ... ($url)"
        curl --retry 4 -L -o "$file" "$url"

        if [ ! -f "$file" ]; then
            echo "Couldn't downloading dependency data: $file"
        else
            echo "Extracting dependencies ... "
            unzip "$file"
       fi
   fi
fi

echo "Checking properties files..."
dir="src/main/properties"
file="$dir/gerbil_keys.properties"

if [ ! -f "$file" ]; then
	echo "Creating empty $file file"
	mkdir -p "$dir";
	echo "##############################################################################"  > $file
	echo "# This is the properties file contains our keys for several annotator web    #"  >> $file
	echo "# services.                                                                  #"  >> $file
	echo "#                      IT SHOULD NOT BE DISTRIBUTED!!!                       #"  >> $file
	echo "##############################################################################"  >> $file
fi

echo "Building and starting GERBIL QA..."
#mvn clean tomcat:run -Dmaven.tomcat.port=1234
mvn clean package -Dmaven.test.skip=true cargo:run -Dcargo.servlet.port=5004
