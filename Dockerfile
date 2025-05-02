#
# Build stage
#
FROM azul/zulu-openjdk:17-latest AS build
COPY . .
RUN ./gradlew bootJar --no-deamon

#
# Package stage
#
FROM azul/zulu-openjdk:17-latest
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]
