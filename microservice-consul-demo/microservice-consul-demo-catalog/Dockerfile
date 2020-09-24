FROM openjdk:11.0.2-jre-slim
COPY target/microservice-consul-demo-catalog-0.0.1-SNAPSHOT.jar .
CMD /usr/bin/java -Dlogging.path=/log/ -Xmx400m -Xms400m -jar microservice-consul-demo-catalog-0.0.1-SNAPSHOT.jar
EXPOSE 8080
