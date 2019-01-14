FROM tomcat:7-jre8-alpine

RUN touch 20180507.txt

VOLUME /tmp

COPY target/gerbil-1.2.7.war webapps/gerbil.war