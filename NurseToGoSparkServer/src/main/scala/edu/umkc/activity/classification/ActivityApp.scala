package edu.umkc.activity.classification

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mali on 12/6/2015.
 */
object ActivityApp {

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName(s"IPApp")
      .setMaster("local[*]")
      .set("spark.executor.memory", "2g")
    //System.setProperty("hadoop.home.dir", "F:\\winutils")
    System.setProperty("hadoop.home.dir", "C:\\hadoop\\winutils")
    val ssc = new StreamingContext(conf, Seconds(2))
    val sc = ssc.sparkContext

    // Create a DStream that will connect to hostname:port
    val lines = ssc.socketTextStream("localhost", 4849);


    val data = lines.map(line => {
      line
    })

    data.print()


  }

}
