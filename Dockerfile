# syntax=docker/dockerfile:1

FROM openjdk:11 as base

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY test-images ./test-images
COPY bootstrap.sh /

RUN ./mvnw clean package

FROM base as development

COPY --from=base /app/target/screenshotter-*.jar /screenshotter.jar

RUN set -ex && apt-get update && apt-get upgrade -y && apt-get clean

# Add a user for running applications
RUN useradd apps
RUN mkdir -p /home/apps && chown apps:apps /home/apps

# Install the needed components
RUN apt-get install -y \
            libnss3-dev \
            libgdk-pixbuf2.0-dev \
            libgtk-3-dev \
            libxss-dev \
            x11vnc \
            xvfb \
            fluxbox \
            wmctrl \
            wget

# Set the Chrome repo
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list

# Install Chrome
RUN apt-get update && apt-get -y install google-chrome-stable

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar","/screenshotter.jar"]

CMD '/bootstrap.sh'
