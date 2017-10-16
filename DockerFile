FROM ubuntu:16.04

MAINTAINER Magomed Gadzhiev

# Updating packages
RUN apt-get -y update --fix-missing

# Install JDK
RUN apt-get install -y openjdk-8-jdk-headless

RUN apt-get install -y maven

RUN apt-get install -y git

ENV WORK /opt/highload

ADD src/ $WORK/src/
ADD pom.xml $WORK/

RUN mkdir -p /var/www/html &&\
 git clone https://github.com/init/http-test-suite.git &&\
 mv ./http-test-suite/httptest /var/www/html

WORKDIR $WORK/
RUN mvn package

EXPOSE 80

CMD java -jar target/server-1.0-SNAPSHOT.jar