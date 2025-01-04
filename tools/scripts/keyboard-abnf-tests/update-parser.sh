#!/bin/sh
# usage:  tools/scripts/update-parser.sh
# this updates ParseFrom.java and ParseTo.java

ABNFDIR=./keyboards/abnf
for abnf in ${ABNFDIR}/*.abnf; do
    base=$(basename ${abnf} .abnf)
    stub=$(echo ${base} | cut -d- -f2)
    # title case it
    tstub=$(echo ${stub}| cut -c1 | tr a-z A-Z)$(echo ${stub} | cut -c2-)
    class=Parse${tstub}
    DIR=$(pwd)/tools/cldr-code/src/main/java/org/unicode/cldr/util/keyboard/
    IN=$(pwd)/${abnf}
    mvn --file=tools/cldr-code/pom.xml exec:java -Dexec.mainClass=apg.Generator -Dexec.args="/in=${IN} /javadoc=${class} /package=org.unicode.cldr.util.keyboard /dir=${DIR} /da /dm /de /dw"
done
