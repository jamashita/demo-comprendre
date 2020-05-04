FROM adoptopenjdk:11-jre-hotspot-bionic

ENV APPLICATION_USER=ktor \
    JAR_FILENAME=demo-comprendre-0.0.1-SNAPSHOT.jar

RUN mkdir /app

RUN groupadd --gid 10001 $APPLICATION_USER && \
    useradd --gid 10001 --uid 10001 --home-dir /app $APPLICATION_USER

RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/$JAR_FILENAME /app/$JAR_FILENAME
WORKDIR /app

CMD ["bash", "-c", "java -jar $JAR_FILENAME"]