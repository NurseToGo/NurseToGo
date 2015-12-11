package edu.umkc.activity.classification;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Created by mali on 12/6/2015.
 */
public class KNNClassifier {

    final String INPUT_PATH = "experiment\\train\\multi\\activity_multi_class_traning.arff";
    final String MODEL_PATH = "experiment\\models\\";

    Instances train;
    Classifier classifier;

    IBk knn;


    Evaluation evaluation;

    void prepareInstances() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(INPUT_PATH);
        train = source.getDataSet();

        // Make the last attribute be the class
        train.setClassIndex(train.numAttributes() - 1);


    }

    void evaluateModel() throws Exception
    {
        knn = new IBk();

        knn.buildClassifier(train);

        StringBuffer output = new StringBuffer();
        PlainText display = new PlainText();
        display.setBuffer(output);

        evaluation = new Evaluation(train);

        evaluation.crossValidateModel(knn, train, 10, new Random(1), display);

        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toClassDetailsString());

    }

    void trainModel() throws Exception
    {
        knn.buildClassifier(train);


        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                MODEL_PATH + "KNNModel.model"));

        out.writeObject(knn);
        out.close();

        System.out.println(String.format(
                "successfully saved model %s", MODEL_PATH + "KNNModel.model"));
    }




}
