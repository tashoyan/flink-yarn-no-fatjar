# Packaging a Flink application without fat-jar

Demonstration of a solution for [FLINK-23972](https://issues.apache.org/jira/browse/FLINK-23972).

Usually Flink applications are packaged into a so-called fat-jar.
Fat-jar is a jar-file containing not only the classes of the application,
but also classes of all direct and transitive dependencies of the application.
Fat-jars has severe disadvantages:
- huge jars with duplicated classes - no sharing via libraries
- impossible to see the actually used dependencies and their versions

This project demonstrates, how to run an application without packaging it into a fat-jar.
This application is delivered as a tarball. After deployment, the directory layout looks like:
```text
bin/
  app-submit.sh
conf/
  app-conf.sh
lib/
  <application jar>
  <dependency jars>
```

The proposed solution does the following:

1. Customize the classpath of the `flink` console client - provide
a customizable environment variable: `FLINK_CLIENT_ADD_CLASSPATH`.
When submitting the application, put the dependency jars into `FLINK_CLIENT_ADD_CLASSPATH`,
thus making them available in the `flink` client classpath.
1. Customize the classpath of Job Manager and Task Manager - provide
the libraries in the `yarn.ship-files` setting.

For the implementation, see `app-submit.sh`.

## Enable customizable classpath for Flink client

Go to the edge node, where your Flink distribution is installed and where you submit your Flink application.
```shell
ssh <edge node>
```
Edit `FLINK_HOME/bin/config.sh`. Find the function `constructFlinkClassPath()`. Replace the string:
```shell
    echo "$FLINK_CLASSPATH""$FLINK_DIST"
```
with the code:
```shell
    if [[ "$FLINK_CLIENT_ADD_CLASSPATH" == "" ]]; then
        echo "$FLINK_CLASSPATH""$FLINK_DIST"
    else
        echo "$FLINK_CLIENT_ADD_CLASSPATH":"$FLINK_CLASSPATH""$FLINK_DIST"
    fi
```

## Build

mvn clean install

## Install

```shell
scp flink-yarn-no-fatjar/target/flink-yarn-no-fatjar-*.tar.gz <edge node>:
ssh <edge node>
test -d flink-yarn-no-fatjar && \
  rm -r flink-yarn-no-fatjar ; \
  tar -xzvf flink-yarn-no-fatjar-*.tar.gz
```

## Configure

Edit file `flink-yarn-no-fatjar/conf/app-conf.sh`, adjust if needed.

## Run

```shell
./flink-yarn-no-fatjar/bin/app-submit.sh
```
After the application is finished, check the HDFS output directory:
```shell
hdfs dfs -cat /tmp/output/*/*
```
