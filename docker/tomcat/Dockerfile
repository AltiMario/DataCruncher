FROM tomcat

MAINTAINER Mario Altimari <mario.altimari@see-r.com>

ENV URL_FILE http://see-r.com/wp-content/uploads/
ENV APP SeerDataCruncher-1.1.war

RUN apt-get update -y && \
  apt-get install wget -y && \
  wget -O /usr/local/tomcat/webapps/ROOT.war ${URL_FILE}/${APP} && \
  mv /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/_ROOT

