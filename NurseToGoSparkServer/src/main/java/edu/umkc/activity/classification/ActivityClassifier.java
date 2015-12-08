package edu.umkc.activity.classification;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.StreamingContext;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Created by mali on 12/6/2015.
 */
public class ActivityClassifier{



    public static void main(String[] args) {
        try {

            NBClassifier nbClassifier = new NBClassifier();

            nbClassifier.prepareInstances();

            nbClassifier.evaluateModel();

            nbClassifier.trainModel();

            //****************kNN********************
            KNNClassifier knnClassifier = new KNNClassifier();

            knnClassifier.prepareInstances();

            knnClassifier.evaluateModel();

            knnClassifier.trainModel();

            //****************Random Forest********************
            RFClassifier rfClassifier = new RFClassifier();

            rfClassifier.prepareInstances();

            rfClassifier.evaluateModel();

            rfClassifier.trainModel();

            //****************kNN********************
            J48Classifier j48Classifier = new J48Classifier();

            j48Classifier.prepareInstances();

            j48Classifier.evaluateModel();

            j48Classifier.trainModel();


        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


}
