FROM tomcat:8

# RUN apt-get -qq -y update && apt-get -qq -y install libmariadb-java --no-install-recommends
# RUN ln -sv /usr/share/java/mariadb-java-client.jar /usr/local/tomcat/lib
RUN curl https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.20/mysql-connector-java-8.0.20.jar > /usr/local/tomcat/lib/mysql-connector.jar
# ADD cldr-apps.war /usr/local/tomcat/webapps/cldr-apps.war
