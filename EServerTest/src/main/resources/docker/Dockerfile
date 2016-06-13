FROM java:openjdk-8u45-jdk
VOLUME /tmp
ADD netflix-eureka-mserver-1.0.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]
