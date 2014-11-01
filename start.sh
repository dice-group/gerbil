#!/bin/bash

echo "Checking dependencies..."
file="gerbil_data/gerbil_data.zip"
url="http://139.18.2.164/mroeder/gerbil_data.zip"

mkdir -p "gerbil_data" || exit 1

if [ ! -f "$file" ]; then
    echo "Downloading dependencies ... ($url)"
    curl --retry 4 -o "$file" "$url"

    if [ ! -f "$file" ]; then
        echo "Couldn't downloading dependency data: $file"
    else
        echo "Extracting dependencies ... "
        unzip "$file"
    fi
fi

mvn clean tomcat:run -Dmaven.tomcat.port=1234
