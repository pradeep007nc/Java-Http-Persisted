FROM gradle:8.1.1-jdk17@sha256:0d2125007bd9aadcf5a3b412e2823f728ca219b4eae2a1d7107244ca243305be as library
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle gradle.properties settings.gradle /home/gradle/code/
WORKDIR /home/gradle/code
RUN gradle dependencies --no-daemon || true

FROM gradle:8.1.1-jdk17@sha256:0d2125007bd9aadcf5a3b412e2823f728ca219b4eae2a1d7107244ca243305be as cache
COPY --from=library /home/gradle/cache_home /home/gradle/.
ENV GRADLE_USER_HOME /home/gradle/.
WORKDIR /home/app/
COPY --from=library /home/gradle/code/build.gradle /home/app/
COPY --from=library /home/gradle/code/gradle.properties /home/app/
COPY --from=library /home/gradle/code/settings.gradle /home/app/

FROM cache as builder
COPY src /home/app/src
RUN gradle buildLayers --no-daemon

FROM gcr.io/distroless/java17-debian11:nonroot@sha256:f6fdb080bfa346133158569ebe4faef92a0d295739b41f01527c567b0a1ec029
COPY --from=builder /home/app/build/docker/main/layers/libs /home/app/libs
COPY --from=builder /home/app/build/docker/main/layers/classes /home/app/classes
COPY --from=builder /home/app/build/docker/main/layers/resources /home/app/resources
COPY --from=builder /home/app/build/docker/main/layers/application.jar /home/app/application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/home/app/application.jar"]