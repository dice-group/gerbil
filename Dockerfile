FROM tomcat:7-jre8-alpine

RUN touch 20190114.txt

VOLUME /tmp

COPY target/gerbil-1.2.7.war webapps/gerbil.war