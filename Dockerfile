FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
COPY build/libs/kotlin-micronaut-management-example-*-all.jar kotlin-micronaut-management-example.jar
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar kotlin-micronaut-management-example.jar