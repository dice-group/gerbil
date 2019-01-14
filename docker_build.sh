# #!/bin/bash

mvn clean package -U -DskipTests
docker build --no-cache -t local/gerbil .