#!/bin/bash

export PROJECT="gerbil"
VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

# build the docker container
docker build -t dicegroup/$PROJECT .

docker tag dicegroup/$PROJECT dicegroup/$PROJECT:$VERSION
docker tag dicegroup/$PROJECT dicegroup/$PROJECT:latest

# upload the image
if [[ $1 == "--upload" ]]
  then
    echo 'uploading...'
    sudo docker push dicegroup/$PROJECT:$VERSION
    sudo docker push dicegroup/$PROJECT:latest
fi