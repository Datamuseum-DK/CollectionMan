FROM docker.io/library/openjdk:23

EXPOSE 8080

RUN groupadd -g 932 regbase && \
    useradd -m -u 932 -g regbase regbase
USER regbase

WORKDIR /home/regbase

ADD target/mobilereg-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
