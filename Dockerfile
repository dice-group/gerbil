FROM tomcat:7-jre8-alpine
# Remove example projects, etc.
RUN rm -rf /usr/local/tomcat/webapps/*

# Download gerbil_data
RUN mkdir /data && mkdir /usr/local/tomcat/gerbil_data && mkdir /usr/local/tomcat/gerbil_data/cache  && mkdir /usr/local/tomcat/gerbil_data/configs && mkdir /usr/local/tomcat/gerbil_data/database && mkdir /usr/local/tomcat/gerbil_data/datasets && mkdir /usr/local/tomcat/gerbil_data/indexes && mkdir /usr/local/tomcat/gerbil_data/output && mkdir /usr/local/tomcat/gerbil_data/resources && mkdir /usr/local/tomcat/gerbil_data/upload && mkdir /usr/local/tomcat/gerbil_data/systems
COPY scripts/download_data.sh download_data.sh
COPY scripts/functions.sh functions.sh
RUN ./download_data.sh /data

# Copy GERBIL's war file
COPY target/gerbil-*.war /usr/local/tomcat/webapps/gerbil.war

# Copy GERBIL's properties files (from target, not from source!)
COPY target/gerbil-*/WEB-INF/classes/*.properties /data/properties/
RUN touch /data/properties/gerbil_keys.properties

# Set path to properties files
ENV GERBIL_PROP_DIR=/usr/local/tomcat/gerbil_properties/
# Create directory for properties
RUN mkdir /usr/local/tomcat/gerbil_properties/

# Copy start script
COPY start_in_docker.sh start_in_docker.sh

CMD ./start_in_docker.sh