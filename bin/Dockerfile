FROM java:8
#FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp
ADD ./target/auth-service.jar /app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 5000
ENV JAVA_OPTS=""
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]