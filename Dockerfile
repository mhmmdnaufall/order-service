FROM eclipse-temurin:22-jre as builder
WORKDIR extracted
COPY ./target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM  eclipse-temurin:22-jre
WORKDIR application
COPY --from=builder ./extracted/dependencies .
COPY --from=builder ./extracted/spring-boot-loader .
COPY --from=builder ./extracted/snapshot-dependencies .
COPY --from=builder ./extracted/application .
EXPOSE 8080
CMD java org.springframework.boot.loader.launch.JarLauncher
