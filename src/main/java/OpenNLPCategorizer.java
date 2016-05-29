/**
 * Created by chillingwithasmile on 29.05.16.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;


import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;


public class OpenNLPCategorizer {
    DoccatModel model = null;

    public static void main(String[] args) {
        OpenNLPCategorizer twitterCategorizer = new OpenNLPCategorizer();
        twitterCategorizer.trainModel();
        twitterCategorizer.catModel();
        String[] inputTexts = {"I love you", "I am sick", "I am tired", "nice jeans"};
        for (String inputText : inputTexts) {
            twitterCategorizer.catTest(inputText);
        }
    }

    public void trainModel() {
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream("/Users/chillingwithasmile/Downloads/tweets.txt");
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

                TrainingParameters mlParams = new TrainingParameters();
                mlParams.put("Algorithm", "MAXENT");
                mlParams.put("TrainerType", "Event");
                mlParams.put("Iterations", Integer.toString(100));
                mlParams.put("Cutoff", Integer.toString(2));

            //DoccatFactory WTF?!?!
            model = DocumentCategorizerME.train("en", sampleStream, mlParams, new DoccatFactory());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataIn != null) {
                try {
                    dataIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void catModel() {
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream("/Users/chillingwithasmile/Downloads/twitterSen.bin"));
            model.serialize(modelOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void catTest(String inputText) {
        InputStream modelIn = null;
        try {
            modelIn = new FileInputStream("/Users/chillingwithasmile/Downloads/twitterSen.bin");
            model = new DoccatModel(modelIn);
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

            double[] outcomes = myCategorizer.categorize(inputText);

            String category = myCategorizer.getBestCategory(outcomes);

            if (category.equalsIgnoreCase("1")) {
                System.out.println("The tweet is positive :) ");
            } else {
                System.out.println("The tweet is negative :( ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}