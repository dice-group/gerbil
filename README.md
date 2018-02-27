Gerbil
========
<i>General Entity Annotator Benchmark</i>

This branch is part of the Gerbil project. It contains a very simple Webservice that wraps the DBpedia Spotlight Webservice and implements the NIF communication. We use it to test the NIF based Webservice annotator adapter that is part of Gerbil.

### Execution with Docker and Docker-compose
Building and starting the container:
```
make build
docker-compose build
docker-compose up
```

testing the service:
```
curl -d "@example.ttl" -H "Content-Type: application/x-turtle" http://localhost:8080/spotlight
```

### Execution with Maven
Building and starting the service:
```
make build
mvn tomcat:run
```

testing the service:
```
curl -d "@example.ttl" -H "Content-Type: application/x-turtle" http://localhost:8080/gerbil-spotWrapNifWS4Test/spotlight
```
