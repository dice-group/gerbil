#!/bin/bash

PROJECT="gerbil"
VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
echo "Building image for version ${VERSION} ..."

# build the docker container
#docker build -t "dicegroup/${PROJECT}:latest" .

tagged_versions=("latest")

# If this is a snapshot version
if [[ "${VERSION}" == *-SNAPSHOT ]]
then
    echo "The image will be tagged as snapshot"
    #docker tag dicegroup/$PROJECT:latest "dicegroup/${PROJECT}:snapshot"
    tagged_versions+=("snapshot")
else
    # Tag the image with the versions by splitting the version string
    IFS="."
    read -r -a version_array  <<< "${VERSION}"
    TAG=""
    for i in "${version_array[@]}"
    do
        # If the tag is empty so far
        if [[ -z "${TAG}" ]]
        then
            TAG="${i}"
        else
            TAG="${TAG}.${i}"
        fi
        echo "The image will be tagged as ${TAG}"
        docker tag dicegroup/$PROJECT:latest "dicegroup/${PROJECT}:${TAG}"
        tagged_versions+=("${TAG}")
    done
fi

# upload the image
if [[ $1 == "--upload" ]]
then
    echo 'uploading...'
    for i in "${tagged_versions[@]}"
    do
        sudo docker push "dicegroup/${PROJECT}:${i}"
    done
fi