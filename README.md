# CDN-ImageRecognition
Application providing CDN and Image Recognition services. Implemented in the context of Service Engineering course from University of Aveiro.

## Prerequisities

* Maven
* Spring Boot
* Postgres
* Docker
* Docker-compose

## Configurations

To run the CDN services, the user must:
* Clone the repository to the local machine
* For Version 1.0:
	* Define a Postgres user and its password in the files application.properties and docker-compose.yml
	* Copy a cloudinary url to the ImgSystemStorageService.java file
* For Version 2.0:
	* There's no need for configuration since it stores in a local file system
* Define the ports in which the services will communicate in the docker-compose.yml file

TO run the Image Recognition service, the user must:
* Clone the repository to the local machine
* Copy an API Key from Google Cloud platform
* Rename it as credentials.json
* Save it in the resources folder
* Define the ports in which the service will communicate in the docker-compose.yml file

## Running the services
After all the configurations have been made, the user must run the following command
```
docker-compose build
```
to build the necessary Docker images. Finally, the user shall run the command
```
docker-compose up
```
to run the containers. This may take a few minutes, since it will be installing Maven dependencies to run each service.

## Version
CDN - Version 2.0
ImageRecognition - Version 1.0
