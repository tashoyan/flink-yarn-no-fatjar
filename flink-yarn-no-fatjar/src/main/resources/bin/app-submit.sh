#!/usr/bin/env bash

set -o nounset
set -o errexit
set -o pipefail

bin_dir="$(cd "$(dirname -- "$0")" ; pwd)"
product_dir="$(dirname -- "$bin_dir")"
lib_dir="$product_dir/lib"
conf_dir="$product_dir/conf"

source "$conf_dir/app-conf.sh"

app_main_class="com.github.tashoyan.flink.no_fatjar.NoFatjarApp"

app_jar_name="@{product.name}-app-@{project.version}.jar"
app_jar="$lib_dir/$app_jar_name"

jars=$(find "$lib_dir" -type f -name \*.jar | grep -v "$app_jar_name")

# Inject the jars into the classpath of the command-line client 'flink'
export FLINK_CLIENT_ADD_CLASSPATH="${jars//[[:space:]]/:}"

# Inject the jars into the classpath of Job Manager and Task Manager
yarn_jars="${jars//[[:space:]]/;}"

export FLINK_CONF_DIR="$conf_dir/app-flink-conf"

"$FLINK_HOME_DIR/bin/flink" \
  run \
  --target yarn-per-job \
  --class "$app_main_class" \
  -Dyarn.ship-files="$yarn_jars" \
  "$app_jar" \
  --output "$OUTPUT_LOCATION"
