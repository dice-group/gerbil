mvn clean install -Dmaven.test.skip=true 
cd gerbil.web
mvn package -Dmaven.test.skip=true cargo:run
