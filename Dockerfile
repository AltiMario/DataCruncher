FROM dordoka/tomcat

ADD target/DataCruncher-1.1.war /opt/tomcat/webapps/ROOT.war
ADD ./docker_start.sh /docker_start.sh

CMD /docker_start.sh