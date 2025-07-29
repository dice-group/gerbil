#!/bin/bash

echo "Checking dependencies..."
file="gerbil_data/gerbil_data.zip"
url="https://github.com/dice-group/gerbil/releases/download/qa-v0.2.4/gerbil_data_QA.zip"

if [ ! -d "gerbil_data" ]; then
    mkdir -p "gerbil_data" || exit 1
    if [ ! -f "$file" ]; then
        echo "Downloading dependencies ... ($url)"
        curl --retry 4 -L -o "$file" "$url"

        if [ ! -f "$file" ]; then
            echo "Couldn't download dependency data: $file"
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

echo "Building and starting GERBIL QA in debug mode..."
MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
mvn clean package -Dmaven.test.skip=true cargo:run -Dcargo.servlet.port=5004
