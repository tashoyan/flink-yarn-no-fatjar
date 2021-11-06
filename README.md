# Packaging a Flink application without fat-jar

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
