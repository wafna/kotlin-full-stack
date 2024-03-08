FROM amazoncorretto:20-alpine-jdk
WORKDIR /usr/app
COPY --chown=daemon:daemon ./build/kotlin-full-stack-demo/ .
RUN chmod +x bin/server
COPY ./app.sh .
RUN chmod +x app.sh
COPY ./config.docker.yml ./config.yml
ENTRYPOINT ["bin/server"]
