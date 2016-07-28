/**
 * Created by chillingwithasmile on 29.05.16.
 */

import java.io.*;

import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class OpenNLPCategorizer {
    DoccatModel model = null;
    String inputText = null;

    public static void main(String[] args) {
        OpenNLPCategorizer twitterCategorizer = new OpenNLPCategorizer();
        twitterCategorizer.trainModel();
        twitterCategorizer.catModel();
        twitterCategorizer.catInput();

/*        String[] inputTexts = {"I hate tea", "I am sick of this tea", "I am tired of drinking tea", "i really do not like tea", "tea isn't exactly my favorite beverage"};
        for (String inputText : inputTexts) {
            twitterCategorizer.catTest(inputText);
        }*/
    }

    public void trainModel() {
        File file = new File ("ressources/train2.tsv");
        MarkableFileInputStreamFactory dataIn = null;
        try {
            dataIn = new MarkableFileInputStreamFactory(file);
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            //Finden der Problem line von Hr Eckert
          /* String sampleString;
            int ln = 0;
            while ((sampleString = (String) lineStream.read())!=null){

                String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(sampleString);
                if (tokens.length > 1) {
                    ln++;
                } else {
                    throw new IOException("Empty lines, or lines with only a category string are not allowed! AND THE TOKEN IS: " + tokens[0] + " AT THE FUCKING LINE " + ln);
                }
            }*/

                TrainingParameters mlParams = new TrainingParameters();
                mlParams.put("Algorithm", "MAXENT");
                mlParams.put("TrainerType", "Event");
                mlParams.put("Iterations", Integer.toString(300));
                mlParams.put("Cutoff", Integer.toString(10));

            model = DocumentCategorizerME.train("en", sampleStream, mlParams, new DoccatFactory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void catModel() {
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream("ressources/twitterSen.bin"));
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

    public void catInput() {
        try{
            FileInputStream filestream = new FileInputStream("ressources/trumptweetsclean.txt");
            DataInputStream in = new DataInputStream(filestream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((inputText = br.readLine()) != null)   {
                    catOutput(inputText);
            }
            in.close();

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void catOutput(String inputText) {

        InputStream modelIn = null;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("ressources/testbewertet.csv", true)));
            modelIn = new FileInputStream("ressources/twitterSen.bin");
            model = new DoccatModel(modelIn);
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(inputText);

            String category = myCategorizer.getBestCategory(outcomes);
            String outcome = myCategorizer.getAllResults(outcomes);

            if (category.equalsIgnoreCase("0")) {
                pw.append(inputText + ";0\n");
                System.out.println(inputText + ": Category: 0 - The tweet is negative with a possibility of: " + outcome);
            } else if (category.equalsIgnoreCase("1")) {
                pw.append(inputText + ";1\n");
                System.out.println(inputText + ": Category: 1 - The tweet is somewhat negative with a possibility of: " + outcome);
            } else if (category.equalsIgnoreCase("2")) {
                pw.append(inputText + ";2\n");
                System.out.println(inputText + ": Category: 2 - The tweet is neutral with a possibility of: " + outcome);
            } else if (category.equalsIgnoreCase("3")) {
                pw.append(inputText + ";3\n");
                System.out.println(inputText + ": Category: 3 - The tweet is somewhat positive with a possibility of: " + outcome);
            } else if (category.equalsIgnoreCase("4")) {
                pw.append(inputText + ";4\n");
                System.out.println(inputText + ": Category: 4 - The tweet is positive with a possibility of: " + outcome);
            }

            pw.close();
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