# CDN-ImageRecognition
Application providing CDN and Image Recognition services. Implemented in the context of Serice Engineering course from University of Aveiro.

## Prerequisities

* Maven
* Spring Boot
* Postgres
* Docker
* Docker-compose

## Instructions

To run the application, clone to your machine, define a Postgres user and its password in the files application.properties and docker-compose.yml.
Next, run the following command
```
docker-compose build
```
to build the necessary images for Docker. Finally, run the command
```
docker-compose up
```
to run the application. This mat take a few minutes, since it will be installing Maven dependencies to run the application.

## Version
Version 1.0
