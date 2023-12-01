FROM openjdk:21

VOLUME /tmp


COPY target/logbook-0.0.1-SNAPSHOT.jar /logbook-0.0.1-SNAPSHOT.jar

EXPOSE 4000 8000
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=*:4000,suspend=n"

ENTRYPOINT java $JAVA_OPTS $JAVA_ARGS -Dspring.profiles.active=devel -jar /logbook-0.0.1-SNAPSHOT.jar
