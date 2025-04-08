# README #

### How do I get set up? ###

* Install jdk 17 https://www.oracle.com/java/technologies/downloads/#jdk17-windows
* Set JAVA_HOME to the jdk folder
* Install docker https://docs.docker.com/desktop/install/windows-install/

### How to run the app ###
* ./gradlew clean build
* docker-compose up --build
* Your application along with a database should be up and running in the port specified in application.yaml

### Running the jar file for deploying on aws ###
* UAT: `java -jar .\lv-0.0.1-SNAPSHOT.jar --spring.profiles.active=uat -DAwsAccessKey="<AWS_ACCESS_KEY>" -DAwsSecretKey="<AWS_SECRET_KEY>"`
* DEV: `java -jar .\lv-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev -DAwsAccessKey="<AWS_ACCESS_KEY>" -DAwsSecretKey="<AWS_SECRET_KEY>"`
* PROD: `java -jar .\lv-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod -DAwsAccessKey="<AWS_ACCESS_KEY>" -DAwsSecretKey="<AWS_SECRET_KEY>"`

### API's ###
* You can access the swagger on: http://localhost:8080/swagger-ui/index.html