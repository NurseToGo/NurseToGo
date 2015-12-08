package edu.umkc.image.classification
/**
 * Created by pradyumnad on 10/07/15.
 * Modified by hastimal on 11/06/2015
 */

import java.io.{ByteArrayInputStream, File}
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO

import com.sun.jersey.core.util.Base64
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel, LDA}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.bytedeco.javacpp.opencv_highgui._
import sun.misc.BASE64Decoder

import scala.collection.mutable

object IPApp {
  val featureVectorsCluster = new mutable.MutableList[String]

  val IMAGE_CATEGORIES = List("cough", "headache", "legpain")

  /**
   *
   * @param sc : SparkContext
   * @param images : Images list from the training set
   */
  def extractDescriptors(sc: SparkContext, images: RDD[(String, String)]): Unit = {

    if (Files.exists(Paths.get(IPSettings.FEATURES_PATH))) {
      println(s"${IPSettings.FEATURES_PATH} exists, skipping feature extraction..")
      return
    }

    val data = images.map {
      case (name, contents) => {
        val n = name.split("file:/")(1)
        val desc = ImageUtils.descriptors(n)
        val list = ImageUtils.matToString(desc)
        println("-- " + list.size)
        list
      }
    }.reduce((x, y) => x ::: y)

    val featuresSeq = sc.parallelize(data)

    featuresSeq.saveAsTextFile(IPSettings.FEATURES_PATH)
    println("Total size : " + data.size)
  }

  def LDAGrouping(sc: SparkContext): Unit = {
    // Load and parse the data
    val data = sc.textFile(IPSettings.FEATURES_PATH)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    val corpus = parsedData.zipWithIndex.map(_.swap).cache()

    // Cluster the documents into three topics using LDA
    val ldaModel = new LDA().setK(3).run(corpus)

    // Output topics. Each is a distribution over words (matching word count vectors)
    println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize + " words):")
    val topics = ldaModel.topicsMatrix
    for (topic <- Range(0, 3)) {
      print("Topic " + topic + ":")
      for (word <- Range(0, ldaModel.vocabSize)) {
        print(" " + topics(word, topic));
      }
      println()
    }

  }

  def kMeansCluster(sc: SparkContext): Unit = {
    if (Files.exists(Paths.get(IPSettings.KMEANS_PATH))) {
      println(s"${IPSettings.KMEANS_PATH} exists, skipping clusters formation..")
      return
    }

    // Load and parse the data
    val data = sc.textFile(IPSettings.FEATURES_PATH)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    // Cluster the data into two classes using KMeans
    val numClusters = 400
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    clusters.save(sc, IPSettings.KMEANS_PATH)
    println(s"Saves Clusters to ${IPSettings.KMEANS_PATH}")
  }

  def createHistogram(sc: SparkContext, images: RDD[(String, String)]): Unit = {
    if (Files.exists(Paths.get(IPSettings.HISTOGRAM_PATH))) {
      println(s"${IPSettings.HISTOGRAM_PATH} exists, skipping histograms creation..")
      return
    }

    val sameModel = KMeansModel.load(sc, IPSettings.KMEANS_PATH)

    val kMeansCenters = sc.broadcast(sameModel.clusterCenters)

    val categories = sc.broadcast(IMAGE_CATEGORIES)


    val data = images.map {
      case (name, contents) => {

        val vocabulary = ImageUtils.vectorsToMat(kMeansCenters.value)

        val desc = ImageUtils.bowDescriptors(name.split("file:/")(1), vocabulary)
        val list = ImageUtils.matToString(desc)
        // println("-- " + list.size)

        val segments = name.split("file:/")
        val segment = segments(1).split("/")
        val cat = segment(segment.length - 2)
        List(categories.value.indexOf(cat) + "," + list(0))
      }
    }.reduce((x, y) => x ::: y)

    val featuresSeq = sc.parallelize(data)

    featuresSeq.saveAsTextFile(IPSettings.HISTOGRAM_PATH)
    println("Total size : " + data.size)
  }

  def generateNaiveBayesModel(sc: SparkContext): Unit = {
    if (Files.exists(Paths.get(IPSettings.NAIVE_BAYES_PATH))) {
      println(s"${IPSettings.NAIVE_BAYES_PATH} exists, skipping Naive Bayes model formation..")
      return
    }

    val data = sc.textFile(IPSettings.HISTOGRAM_PATH)
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    // Split data into training (60%) and test (40%).
    val splits = parsedData.sample(true,0.6, seed = 11L)
    val training = parsedData
    val test = splits

    val model = NaiveBayes.train(training, lambda = 1.0)

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    predictionAndLabel.collect().foreach(f=>println(f))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    ModelEvaluation.evaluateModel(predictionAndLabel)

    // Save and load model
    model.save(sc, IPSettings.NAIVE_BAYES_PATH)
    println("Naive Bayes Model generated")
  }

  /**
   * @note Test method for classification on Spark
   * @param sc : Spark Context
   * @return
   */
  def testImageClassification(sc: SparkContext) = {

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)

    val path = "newLabel.jpg"
    val desc = ImageUtils.bowDescriptors(path, vocabulary)
    println("Descriptors  :")
    println(desc.asCvMat())

    val testImageMat = imread(path)
    imshow("Test Image", testImageMat)

    val histogram = ImageUtils.matToVector(desc)

    println("-- Histogram size : " + histogram.size)
    println(histogram.toArray.mkString(" "))

    val nbModel = NaiveBayesModel.load(sc, IPSettings.NAIVE_BAYES_PATH)
    println(nbModel.labels.mkString(" "))

    val p = nbModel.predict(histogram)
    println(s"Predicting test image : " + IMAGE_CATEGORIES(p.toInt))

  }

  /**
   * @note Test method for classification from Client
   * @param sc : Spark Context
   * @param path : Path of the image to be classified
   */
  def classifyImage(sc: SparkContext, path: String): Unit = {

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)

    val desc = ImageUtils.bowDescriptors(path, vocabulary)

    val histogram = ImageUtils.matToVector(desc)

    println("--Histogram size : " + histogram.size)

    val nbModel = NaiveBayesModel.load(sc, IPSettings.NAIVE_BAYES_PATH)
    println(nbModel.labels.mkString(" "))

    val p = nbModel.predict(histogram)
    println(s"Predicting test image : " + IMAGE_CATEGORIES(p.toInt))

    IMAGE_CATEGORIES(p.toInt)
  }

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName(s"IPApp")
      .setMaster("local[*]")
      .set("spark.executor.memory", "2g")
    //System.setProperty("hadoop.home.dir", "F:\\winutils")
    System.setProperty("hadoop.home.dir", "C:\\hadoop\\winutils")
    val ssc = new StreamingContext(conf, Seconds(2))
    val sc = ssc.sparkContext

    //val images = sc.wholeTextFiles(s"${IPSettings.INPUT_DIR}/*/*.jpg").cache()

    /**
     * Extracts Key Descriptors from the Training set
     * Saves it to a text file
     */
    //extractDescriptors(sc, images)

    /**
     * Reads the Key descriptors and forms a 'K' topics
     * Saves the centers as a text file
     */
    // LDAGrouping(sc);
    /**
     * Reads the Key descriptors and forms a 'K' cluster
     * Saves the centers as a text file
     */

    //kMeansCluster(sc)

    /**
     * Forms a labeled Histogram using the Training set
     * Saves it in the form of label, [Histogram]
     *
     * This shall be used as a input to Naive Bayes to create a model
     */
    //createHistogram(sc, images)

    /**
     * From the labeled Histograms a Naive Bayes Model is created
     */
    generateNaiveBayesModel(sc)

    testImageClassification(sc)

    //   val ip = InetAddress.getByName("10.182.0.192").getHostName

    //    val lines = ssc.receiverStream(new CustomReceiver(ip,5555))
    // val lines = ssc.socketTextStream(ip, 5555)
    val lines = ssc.socketTextStream("10.205.0.71", 4321) //10.205.0.71
   // val lines = "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"
    println("Connected!!!!")

    val data = lines.map(line => {
      line
    })
    println("I am above ")
    data.print()
    println("I am below ")
    //Filtering out the non base64 strings
    val base64Strings = lines.filter(line => {
      Base64.isBase64(line)
    })

    base64Strings.foreachRDD(rdd => {
      val base64s = rdd.collect()
      for (base64 <- base64s) {
        val bufferedImage = ImageIO.read(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(base64)))
        val imgOutFile = new File("newLabel.jpg")
        val saved = ImageIO.write(bufferedImage, "jpg", imgOutFile)
        println("Saved : " + saved)

        if (saved) {
          val category = classifyImage(rdd.context, "newLabel.jpg")
          println(category)
        }
      }
    })

    ssc.start()

    ssc.awaitTermination()

    //    ssc.stop()
  }
}