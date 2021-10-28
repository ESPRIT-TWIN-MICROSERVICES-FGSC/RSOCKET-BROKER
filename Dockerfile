FROM openjdk:17
EXPOSE 8081
ADD /target/notifications-socket-broker-0.0.1-SNAPSHOT.jar docker-client.jar
ENTRYPOINT ["java","-jar","docker-client.jar"]
