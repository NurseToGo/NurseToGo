package edu.umkc.activity.classification;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.StreamingContext;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
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


            //test/classify

            new ActivityClassifier().classify("4.19,-2.00,-1.50");

        }


        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    String classify(String content) throws Exception {
        //prepare attributes

        ArrayList<String> attributeClassList = new ArrayList<>();
        ArrayList<Attribute> attributeList = new ArrayList<>();
        attributeClassList = new ArrayList<>();

        attributeClassList.add("bending");
        attributeClassList.add("legslide");
        attributeClassList.add("resting");
        attributeClassList.add("lyingkicks");


        Attribute attributeZ = new Attribute("acc_z", (List<String>) null);
        Attribute attributeY = new Attribute("acc_y", (List<String>) null);
        Attribute attributeX = new Attribute("acc_x", (List<String>) null);
        Attribute attributeClass = new Attribute("activity", attributeClassList);
        attributeList.add(attributeClass);


        Instances thisInstance = new Instances("Activity_Classification", attributeList, 0);

        thisInstance.setClass(attributeClass);

        thisInstance.add(new DenseInstance(3));

        String[] data = content.split(",");

        thisInstance.instance(0).setValue(0, Double.parseDouble(data[0]));
        thisInstance.instance(0).setValue(1, Double.parseDouble(data[1]));
        thisInstance.instance(0).setValue(2, Double.parseDouble(data[2]));
        //thisInstance.instance(0).setValue(3, "?");

        Classifier classifier = loadTrainedModel("experiment\\models\\RFModel.model");

        String predicted = thisInstance.classAttribute().value(
                (int) classifier.classifyInstance(thisInstance
                        .instance(0)));

        double[] distribution = classifier
                .distributionForInstance(thisInstance.instance(0));

        String response;
        switch (predicted)
        {
            case "legslide":
                response = "You are performing leg slide exercise";
                break;
            case "bending":
                response = "You are performing knee bending exercise";
                break;
            case "lyingkicks":
                response = "You are performing kicks while lying exercise";
                break;
            case "resting":
                response = "You are now resting";
                break;
            default:
                response = "I am not really sure";// this should not happen anyway
        }

        return response;

    }

    private Classifier loadTrainedModel(String modelFileName) {

        try {

            ObjectInputStream is = new ObjectInputStream(new FileInputStream(
                    modelFileName));

            Object model = is.readObject();

            is.close();

            return (Classifier) model;
        } catch (Exception ex) {
            System.err.println("Could not load the trained model: "
                    + modelFileName);

            return null;
        }

    }


}
