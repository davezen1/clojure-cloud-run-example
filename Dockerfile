FROM clojure AS build-env
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

FROM gcr.io/distroless/java:11
COPY --from=build-env /usr/src/app /usr/src/app
WORKDIR /usr/src/app
ENTRYPOINT ["java","-jar","app-standalone.jar"]
