# syntax=docker/dockerfile:1

FROM openjdk:16-alpine3.13 as base

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY storage ./storage
COPY test-images ./test-images
COPY tools ./tools
COPY bootstrap.sh /

RUN ./mvnw clean package

FROM base as development

COPY --from=base /app/target/screenshotter-*.jar /screenshotter.jar

# Add glibc to be able to run Chrome browser
# Source: https://github.com/anapsix/docker-alpine-java

ENV GLIBC_REPO=https://github.com/sgerrand/alpine-pkg-glibc
ENV GLIBC_VERSION=2.30-r0

RUN set -ex && \
    apk --update add libstdc++ curl ca-certificates && \
    for pkg in glibc-${GLIBC_VERSION} glibc-bin-${GLIBC_VERSION}; \
        do curl -sSL ${GLIBC_REPO}/releases/download/${GLIBC_VERSION}/${pkg}.apk -o /tmp/${pkg}.apk; done && \
    apk add --allow-untrusted /tmp/*.apk && \
    rm -v /tmp/*.apk && \
    /usr/glibc-compat/sbin/ldconfig /lib /usr/glibc-compat/lib

RUN echo "http://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories \
    && echo "http://dl-cdn.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories \
    && apk update \
    && apk add \
        --no-cache \
          --repository http://dl-cdn.alpinelinux.org/alpine/edge/testing \
          --repository http://dl-cdn.alpinelinux.org/alpine/edge/main \
        bash \
        x11vnc \
        xvfb \
        fluxbox \
        wmctrl \
        wget \
        chromium \
        chromium-chromedriver

ENTRYPOINT ["java","-jar","/screenshotter.jar"]

CMD '/bootstrap.sh'
