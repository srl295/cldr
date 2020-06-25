# DOCKER DOCKER DOCKER

For the easy way to run in docker, do this (from the root 'cldr' directory):

A. create directory `tools/cldr-apps/local-cldr/`

B. create file `tools/cldr-apps/local-cldr/cldr.properties`

```
CLDR_PHASE=BETA
CLDR_TESTPW=something2
CLDR_OLDVERSION=36
CLDR_VAP=something1
CLDR_LASTVOTEVERSION=36
CLDR_DIR=/usr/local/tomcat/cldr/cldr-trunk
CLDR_NEWVERSION=37
```

```shell
ant all -f tools/java/build.xml
ant -DCLDR_TOOLS=$(pwd)/tools/java -DCATALINA_HOME=/usr/local/opt/tomcat/libexec -f tools/cldr-apps/build.xml clean war
docker-compose up
```

Note that `/usr/local/opt/tomcat/libexec` is the path to a tomcat8 installation, just used for .jar files.

congrats, you now have the Survey Tool running on port 8080
