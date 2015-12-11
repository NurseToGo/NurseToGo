package edu.umkc.activity.classification

/**
 * Created by mali on 12/06/2015.
 */
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD


object ModelEvaluation {
  def evaluateModel(predictionAndLabels: RDD[(Double, Double)]) = {
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val cfMatrix = metrics.confusionMatrix
    println(" |=================== Confusion matrix ==========================")
    println(cfMatrix)

  }
}
