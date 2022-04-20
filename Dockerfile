# syntax=docker/dockerfile:1

FROM openjdk:11 as base

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

RUN set -ex && apt update && apt upgrade && apt clean

# Add a user for running applications
RUN useradd apps
RUN mkdir -p /home/apps && chown apps:apps /home/apps

# Install the needed components
RUN apt-get install -y \
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

# Download matching Chrome Driver
# https://stackoverflow.com/a/61928952/167920
RUN chromeVersion=$(google-chrome --product-version) \
    && chromeMajorVersion=${chromeVersion%%.*} \
    && latestDriverReleaseURL=https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$chromeMajorVersion \
    && wget $latestDriverReleaseURL \
    && latestDriverVersionFileName="LATEST_RELEASE_"$chromeMajorVersion \
    && latestFullDriverVersion=$(cat $latestDriverVersionFileName) \
    && rm $latestDriverVersionFileName \
    && finalURL="http://chromedriver.storage.googleapis.com/"$latestFullDriverVersion"/chromedriver_linux64.zip" \
    && wget $finalURL

# Unzip the Chrome Driver executable and move it to the desired folder
RUN unzip chromedriver_linux64.zip \
    && rm chromedriver_linux64.zip \
    && mv chromedriver /usr/bin/

EXPOSE 8080

ENTRYPOINT ["java","-jar","/screenshotter.jar"]

CMD '/bootstrap.sh'
