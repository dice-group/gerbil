mvn clean install -Dmaven.test.skip=true 
cd gerbil.web
mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Dmaven.tomcat.port=1234
