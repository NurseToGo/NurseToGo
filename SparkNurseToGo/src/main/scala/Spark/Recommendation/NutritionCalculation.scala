package Spark.Recommendation

import java.io.File

import org.apache.spark.SparkContext

/**
 * Created by hastimal on 10/23/2015.
 */
object NutritionCalculation {

  def findCalorie(sc: SparkContext, input: String) {

    val nutritionDataHomeDir = "src/main/resources/NutritionData"
    val file = sc.textFile(new File(nutritionDataHomeDir, "food-calorie.dat").toString).map { line =>

      val fields = line.split("::")
      (fields(0), fields(1))
    }

    val calorieData = sc.textFile(new File(nutritionDataHomeDir,"food-calorie.dat").toString).filter(line => line.contains(input)).collect()
    System.out.println("Input String:" + input + "\tCalories:")
    val output = calorieData.mkString("").replaceAll(input+"-","")
    System.out.println(output)
    //iOSConnector.sendCommandToRobot("calories:::"+output)
  }
}
