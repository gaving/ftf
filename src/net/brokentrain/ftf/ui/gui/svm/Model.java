package net.brokentrain.ftf.ui.gui.svm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.ProcessingArticle;
import net.brokentrain.ftf.ui.gui.GUI;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import sparseGP.GPResult;
import sparseGP.GaussianProcess;

public class Model implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private AbstractProcessor abstractProcessor;

    private transient Vector target;

    private transient Matrix data;

    private transient Matrix testData;

    private transient GaussianProcess gp;

    private transient GPResult gpResult;

    private transient HashMap<Article, Double> predictions;

    private Integer totalTrained;

    private double[] rawTargetData;

    private double[][] rawMatrixData;

    public Model(int minWordLen, int maxWordLen) {
        abstractProcessor = new AbstractProcessor();
        abstractProcessor.setMinWordLength(minWordLen);
        abstractProcessor.setMaxWordLength(maxWordLen);

        totalTrained = 0;

        /* New classifier */
        gp = new GaussianProcess();
        predictions = new HashMap<Article, Double>();
    }

    public void addTrainingSample(String abstractText) {
        abstractProcessor.addAbstract(abstractText);
    }

    private void buildMatrices() {

        data = new DenseMatrix(rawMatrixData);
        target = new DenseVector(rawTargetData);

        /* New classifier */
        gp = new GaussianProcess();
        predictions = new HashMap<Article, Double>();
    }

    public HashMap<Article, Double> getPredictions() {
        return predictions;
    }

    public int getTotalTrained() {
        return totalTrained;
    }

    public boolean hasTrained() {
        return ((abstractProcessor != null) && (data != null) && (target != null));
    }

    public void predict(List<Article> articles) {

        int featureCount = abstractProcessor.getFeatureCount();

        if (featureCount < 1) {
            return;
        }

        int itemCount = articles.size();

        testData = new DenseMatrix(itemCount, featureCount);

        int i = 0;
        for (Article article : articles) {

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            for (int j = 0; j < featureCount; ++j) {
                String feature = abstractProcessor.getFeatures().get(j);
                testData.add(i, j, abstractText.contains(feature) ? 1 : 0);
            }
            i++;
        }

        gpResult = gp.traingp(data, target, testData, null,
                GaussianProcess.RBF, 0.01, false);

        for (int k = 0; k < gpResult.numExamples; ++k) {
            Article article = articles.get(k);

            if (article != null) {
                Double value = gpResult.predictions[k];
                predictions.put(article, value);
            }
        }
    }

    public void printData() {
        gp.printMatrix(data);
    }

    public void printTargetData() {
        gp.printMatrix(target);
    }

    public void printTestData() {
        gp.printMatrix(testData);
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        GUI.log.debug("Reading model data");

        in.defaultReadObject();

        /* Rebuild the matrices with the saved data */
        buildMatrices();
    }

    public void train(Map<Article, Boolean> samples) {

        int featureCount = abstractProcessor.getFeatureCount();

        if (featureCount < 1) {
            return;
        }

        int itemCount = samples.size();
        totalTrained += itemCount;

        data = new DenseMatrix(itemCount, featureCount);

        /* Target vector for results */
        target = new DenseVector(itemCount);

        int i = 0;
        for (Article article : samples.keySet()) {

            boolean isPositive = samples.get(article);

            String abstractText = ((ProcessingArticle) article)
                    .getAbstractText();

            /* Add each feature */
            for (int j = 0; j < featureCount; ++j) {

                String feature = abstractProcessor.getFeatures().get(j);
                data.add(i, j, abstractText.contains(feature) ? 1 : 0);
            }

            target.add(i, isPositive ? 1 : -1);
            i++;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        GUI.log.debug("Writing model data");

        int rows = data.numRows();
        int columns = data.numColumns();

        rawMatrixData = new double[rows][columns];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                rawMatrixData[i][j] = data.get(i, j);
            }
        }

        rawTargetData = ((DenseVector) target).getData();

        out.defaultWriteObject();
    }

}
