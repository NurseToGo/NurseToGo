package Kafka.Spark.Sentimental

/**
 * Created by hastimal on 10/31/2015.
 */
package Kafka.Spark.Sentimental

import Spark.Recommendation._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
/**
 * Created by hastimal on 9/28/2015.
 */
object TwitterSentimentMain {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir","F:\\winutils")
    //From MainClass.scala
    val filters = Array("food", "nutrition", "diet", "healthy", "diseasefree", "physician")
    val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
    val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment("A balanced nutritional diet and regular exercise can protect the brain and ward off mental disorders.")
    System.out.println(tweetWithSentiment)

    //val filters = args

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey", "t0tAnvsGPStnvRJe6LPOaIjLo")
    System.setProperty("twitter4j.oauth.consumerSecret", "tSeeyiOAfBJqaR9rvAmAt8ePZA3B6YSmymXmcyqeT0FWapPAb0")
    System.setProperty("twitter4j.oauth.accessToken", "1868076104-Zftf4ts0hHGVsnTLYDXneAyTxNXlkoLf86vC3ez")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "GXL9hhOnC9oaSR6xwjEnCuIF01fOaHsYuxo6DH7WcFXty")

    //Create a spark configuration with a custom name and master
    // For more master configuration see  https://spark.apache.org/docs/1.2.0/submitting-applications.html#master-urls
    val sparkConf = new SparkConf().setAppName("SparkTwitterApp").setMaster("local[*]")
    //Create a Streaming Context with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets
    val stream = TwitterUtils.createStream(ssc, None, filters)
    stream.print()
    stream.filter(t => (t.toString.length> 0)).saveAsTextFiles("src/main/resources/output/TwitterSentimentaAnalysis.txt", "data")
    //stream.saveAsTextFiles("src/main/resources/outputFile/TwitterSentimentaAnalysis","data")

    val sentiment:DStream[TweetWithSentiment]=stream.map{Status=>{
      val st=Status.getText()
      val sa=new SentimentAnalyzer()
      val tw=sa.findSentiment(st)
      tw
    }
    }



    val res = sentiment.foreachRDD{
      rdd=>rdd.foreach{
        tw=> {
          if(tw!=null) {
            println("Analyzed Tweets is::    " + tw.getLine + "     sentiment type::  " + tw.getCssClass)
            iOSConnector.sendCommandToRobot("Analyzed Tweets is::    " + tw.getLine + "     sentiment type::  " + tw.getCssClass)
          }
          //SocketClient.sendCommandToRobot(rdd+" This is ")
        }

      }

    }


    ssc.start()

   // ssc.awaitTermination()
  }

}
