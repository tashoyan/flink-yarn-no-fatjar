## Hadoop classpath
HADOOP_CLASSPATH="$(hadoop classpath)"
export HADOOP_CLASSPATH

## Path to the Flink distribution
export FLINK_HOME_DIR="$HOME/flink/latest"

## Where to write the output
export OUTPUT_LOCATION="hdfs:///tmp/output"
