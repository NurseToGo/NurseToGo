package Spark.Recommendation

import java.net.InetAddress

import Kafka.Spark.Sentimental.{SentimentAnalyzer, TweetWithSentiment}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by hastimal on 10/22/2015.
 */


object NurseToGoMain {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val sparkConf=new SparkConf().setAppName("NurseToGoMain").set("spark.executor.memory", "4g").setMaster("local[*]")
    val ssc= new StreamingContext(sparkConf,Seconds(2))
    val sc=ssc.sparkContext

    /*----to be removed----*/
    NutritionCalculation.findCalorie(sc,"vegetable burger")
    Recommendation.foodRecommend(sc,"5","male")
    val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
    val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment("Food is delicious")
    System.out.println(tweetWithSentiment.toString().replaceAll("sentiment : ",""))
    /*--------------------------*/

    var age:String = ""
    val ip=InetAddress.getByName("10.205.0.90").getHostName
    val lines=ssc.socketTextStream(ip,1234)

    val command= lines.map(x=>{
      val y=x.toUpperCase()
      y
    })
    command.foreachRDD(
      rdd=> {
        if (rdd.collect().contains("RECOMMEND:::age")) {
          System.out.println("Got Recommend command")
          Recommendation.recommend(rdd.context) //it was commented
          age = rdd.collect().mkString("").replace("RECOMMEND:::age","")
          System.out.println(age)

        }
        else if (rdd.collect().contains("RECOMMEND:::gender")) {
          System.out.println("Got Recommend command")
          //Recommendation.recommend(rdd.context)
          val gender = rdd.collect().mkString("").replace("RECOMMEND:::gender","")
          System.out.println(gender)
          Recommendation.foodRecommend(sc,age,gender)

        }
//        else if (rdd.collect().contains("SENTIMENT")) {
//          val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
//          val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment("click here for your Sachin Tendulkar personalized digital autograph.")
//          System.out.println(tweetWithSentiment.toString().replaceAll("sentiment : ",""))
//          iOSConnector.sendCommandToRobot("sentiment:::"+tweetWithSentiment.toString().replaceAll("sentiment : ",""))
//        }
       // TwitterSentimentMain.main(args: Array[String])

        else if (rdd.collect().contains("PARSE")) {
          val input = rdd.collect().toString().replaceAll("PARSE:","")
          NutritionCalculation.findCalorie(sc,input)
        }
      }
    )
    //TwitterSentimentMain.main(args: Array[String])
    lines.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
