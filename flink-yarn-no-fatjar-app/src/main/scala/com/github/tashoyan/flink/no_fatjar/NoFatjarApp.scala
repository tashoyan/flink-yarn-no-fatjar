package com.github.tashoyan.flink.no_fatjar

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.eclipse.collections.api.factory.Lists
import org.eclipse.collections.api.list.ImmutableList

import java.util.Locale

/**
 * This test uses a third-party dependency (eclipse-collections) with the following properties:
 *
 * <ul>
 *   <li>not a part of JDK
 *   <li>not a part of Flink distribution
 * </ul>
 *
 * <p>The test is packaged as an ordinary jar (not a fat-jar). <br>
 * The resulting application is packaged as a tarball including:
 *
 * <ul>
 *   <li>the application jar
 *   <li>the jars of dependencies
 *   <li>the shell launcher
 * </ul>
 *
 * <p>Command-line argument: --output /path/to/write/output
 */
object NoFatjarApp {

    def main(args: Array[String]): Unit = {
        val params = ParameterTool.fromArgs(args)
        val outputPath = params.getRequired("output")

        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        /* Use some API outside of JDK - eclipse-collections for example. */
        val inputData: ImmutableList[String] = Lists.immutable.of("one", "two", "three")
        val input = env.fromCollection(inputData.castToCollection())
        val output = input.map(_.toUpperCase(Locale.ROOT))
        val sink =
                StreamingFileSink.forRowFormat(
                                new Path(outputPath), new SimpleStringEncoder[String]("UTF-8"))
                        .build()
        output.addSink(sink).setParallelism(1)
        env.execute(this.getClass.getSimpleName)
        ()
    }

}
