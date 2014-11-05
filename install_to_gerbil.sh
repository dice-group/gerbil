#!/bin/bash

# usage ./install_to_gerbil <version> [path_to_gerbil_repo]

if [ $# -gt 0 ]
then
VERSION=$1
if [ $# -gt 1 ]
then
	GERBIL_REPO_PATH=$2
else
	GERBIL_REPO_PATH="../gerbil/repository"
fi

mvn clean package 
mvn install:install-file -Dfile=target/gerbil.nif.transfer-$VERSION.jar -Dpackaging=jar -Djavadoc=target/gerbil.nif.transfer-$VERSION-javadoc.jar -Dsources=target/gerbil.nif.transfer-$VERSION-sources.jar -DpomFile=pom.xml -DlocalRepositoryPath=$GERBIL_REPO_PATH

else
	echo "usage ./install_to_gerbil <version> [path_to_gerbil_repo]"
fi

