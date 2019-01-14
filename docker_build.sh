# #!/bin/bash

export PROJECT="gerbil"
VERSION=`mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec`-dev

# build the war file
mvn clean package -U -DskipTests

# build the docker container
docker build --no-cache -t philippkuntschik/$PROJECT .

docker tag philippkuntschik/$PROJECT philippkuntschik/$PROJECT:$VERSION
docker tag philippkuntschik/$PROJECT philippkuntschik/$PROJECT:dev

# upload the image
if [[ $1 == "--upload" ]]
  then
    echo 'uploading...'
    sudo docker push philippkuntschik/$PROJECT:$VERSION
    sudo docker push philippkuntschik/$PROJECT:dev
fi
