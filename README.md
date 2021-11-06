# Packaging a Flink application without fat-jar

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
