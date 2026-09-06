FROM gcr.io/distroless/java25-debian13:debug

EXPOSE 8080

ADD target/mobilereg-jar-with-dependencies.jar app.jar

USER 932:932
ENTRYPOINT ["java","-jar","app.jar"]
