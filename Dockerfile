#
# Build stage
#
FROM azul/zulu-openjdk:17-latest AS build
COPY . .
RUN gradle clean build

#
# Package stage
#
FROM azul/zulu-openjdk:17-latest
COPY --from=build /home/app/target/Falcon-0.0.1.jar /usr/local/lib/falcon.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/falcon.jar"]