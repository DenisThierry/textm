/**
 * Created by chillingwithasmile on 29.05.16.
 */

import java.io.*;
import opennlp.tools.doccat.*;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class OpenNLPCategorizer {
    private DoccatModel model = null;
    public static final String RES = "ressources/";

    // Boolean Werte ändern um entsprechende Twitter Methoden bzw. Klassen auszuführen!!!!!!!!!!
    private static final boolean TWEETS = false;
    private static final boolean SENTIMENTS = true;


    /**
     *   Main Methode, setzen der Boolean Variablen auf jeweils false oder true ermöglicht entweder
     *   das Ausführen der Sentiment Analyse oder Abruf, Reinigung und Speicherung der Tweets
     * */
    public static void main(String[] args) {
        OpenNLPCategorizer twitterCategorizer = new OpenNLPCategorizer();

        if (TWEETS) {
            TwitterTweets tt = new TwitterTweets();
            tt.getAllTweets();
        }

        if (SENTIMENTS) {
            twitterCategorizer.trainModel();
            twitterCategorizer.catModel();
            twitterCategorizer.catInput();
        }
    }

    /**
     *   Ausführen der Train Methode anhand eines von Hand kategoriesierten Datensatzes.
     *   Ändern der "Iterations" und des "Cutoffs" um Parameter des Modells zu ändern
     *   Iterations steht für die Trainingsiterationen, Cutoff für die Häufigkeit mit der
     *   ein Wort im Datensatz (hier train2.tsv) vorkommen muss damit es für das Training berücksichtig wird.
     *   Bei Cutoff 10 werden 10.160 Tokens nicht berücksichtig. Siehe "Dropped event 1:[bow=xxx]"
     * */
    public void trainModel() {
        File file = new File(RES + "train2.tsv");
        MarkableFileInputStreamFactory dataIn = null;
        try {
            dataIn = new MarkableFileInputStreamFactory(file);
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            TrainingParameters mlParams = new TrainingParameters();
            mlParams.put("Algorithm", "MAXENT");
            mlParams.put("TrainerType", "Event");
            mlParams.put("Iterations", "300");
            mlParams.put("Cutoff", "10");

            model = DocumentCategorizerME.train("en", sampleStream, mlParams, new DoccatFactory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Physische Speicherung des tranierten Modells auf Festplatte
     *  Achtung: Aus Performancegründen wird das Physische Modell jedoch nicht weiter benutzt!
     * */
    public void catModel() {
        try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(RES + "twitterSen.bin"))) {
            model.serialize(modelOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Einlesen des Datensatzes welcher einer Sentiment Analyse durchgeführt werden soll.
     *  Hier Tweets von Trump.
     *  Methode führt nach Einlesen catOutput Methode aus.
     * */
    public void catInput() {
        try (FileInputStream filestream = new FileInputStream(RES + "trumptweetsclean.txt");
             DataInputStream in = new DataInputStream(filestream);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String inputText = null;
            while ((inputText = br.readLine()) != null) {
                catOutput(inputText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Ausgabe der fertigen Sentiment Analyse, jeder Tweet erhält in einer seperaten Spalte der csv eine Sentiment Kategorie
     *  Hier: 0 bis 4, wobei 0 für Negativ steht, 4 für Positiv.
     *  Consolenausgabe und CSV-Ausgabe sind nicht identisch!
     *  In CSV Datei wird ausschließlich diejenige Kategorie ausgegeben welcher der Algorithmus als wahrscheinlichster Wert sieht.
     *  In der Consolen Ausgabe werden sämtliche mögliche Kategorien, einschließlich deren Wahrscheinlichkeiten ausgegeben.
     * */
    public void catOutput(String inputText) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(RES + "testbewertet.csv", true)))) {
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(inputText);

            String category = myCategorizer.getBestCategory(outcomes);
            String outcome = myCategorizer.getAllResults(outcomes);

            pw.append(inputText + ";" + category + "\n");
            System.out.println(inputText + "\n\tCategory: " + category + " (" + outcome + ")\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}