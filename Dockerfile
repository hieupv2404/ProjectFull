FROM openjdk:11-jdk
VOLUME /tmp/InventoryManagementVolume
ARG JAR_FILE=target/group3-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]