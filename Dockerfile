#############################
# BUILD THE WAR FILE
#############################

FROM maven:3.6.0-jdk-8 AS build

COPY src /tmp/src/
COPY repository /tmp/repository/
COPY pom.xml /tmp/

# overwrite gerbil-data path: 
COPY docker-config/* /tmp/src/main/properties/

WORKDIR /tmp/

RUN mvn package -U -DskipTests

#############################
# BUILD THE DOCKER CONTAINER
#############################

FROM tomcat:7-jre8-alpine

RUN touch 20190115.txt

COPY --from=build /tmp/target/gerbil-*.war $CATALINA_HOME/webapps/$gerbil.war