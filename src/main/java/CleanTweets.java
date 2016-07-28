package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanTweets {

	public void cleanTweets(){
		
		String fulltweetsTrump = "";
		String fulltweetsClinton = "";
		try {
			// Read Textfile
			File fileTrump = new File("ressources/trumptweetswithRT.txt");
			File fileClinton = new File("ressources/clintontweetswithRT.txt");
			FileReader fileReaderTrump = new FileReader(fileTrump);
			FileReader fileReaderClinton = new FileReader(fileClinton);
			BufferedReader inTrump = new BufferedReader(fileReaderTrump);
			BufferedReader inClinton = new BufferedReader(fileReaderClinton);
			StringBuffer stringBufferTrump = new StringBuffer();
			StringBuffer stringBufferClinton = new StringBuffer();
			String line;
			while ((line = inTrump.readLine()) != null) {
				stringBufferTrump.append(line);
				stringBufferTrump.append("\n");
			}
			while ((line = inClinton.readLine()) != null) {
				stringBufferClinton.append(line);
				stringBufferClinton.append("\n");
			}
			fulltweetsTrump = stringBufferTrump.toString();
			fulltweetsClinton = stringBufferClinton.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Regulärer Asudruck zum Filtern der Retweets
		 */
		
		// entfernen von Retweets
		Pattern p = Pattern.compile(".*RT @[A-Za-z1-9]+:.*\n");
		// entfernen von Links
		Pattern phttp = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		// entfernen von Hashtags
		Pattern phash = Pattern.compile("[#][A-Za-z].*\n");
		
		Matcher mtrump = p.matcher(fulltweetsTrump);
		Matcher mclinton = p.matcher(fulltweetsClinton);
		fulltweetsTrump = mtrump.replaceAll("");
		fulltweetsClinton = mclinton.replaceAll("");
		
		Matcher mtrumphttp = phttp.matcher(fulltweetsTrump);
		Matcher mclintonhttp = phttp.matcher(fulltweetsClinton);
		fulltweetsClinton = mclintonhttp.replaceAll("");
		fulltweetsTrump = mtrumphttp.replaceAll("");
		
		Matcher mtrumphash = phash.matcher(fulltweetsTrump);
		Matcher mclintonhash = phash.matcher(fulltweetsClinton);
		fulltweetsTrump = mtrumphash.replaceAll("");
		fulltweetsClinton = mclintonhash.replaceAll("");

		/**
		 * Gefilterte Tweets für die Ausgabe der .txt Datei in eine Liste
		 * einfügen
		 */
		List<String> allTweetsTrump = new ArrayList<String>();
		List<String> allTweetsClinton = new ArrayList<String>();
		allTweetsTrump.add(fulltweetsTrump);
		allTweetsClinton.add(fulltweetsClinton);
		listInDatei(allTweetsTrump, new File("ressources/trumptweets.txt"));
		listInDatei(allTweetsClinton, new File("ressources/clintontweets.txt"));
	}

	/*
	 * Liste mit Tweets von Trump in .txt Datei ausgeben
	 */
	private static void listInDatei(List list, File datei) {

		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new FileWriter(datei));
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Object o = iter.next();
				printWriter.println(o);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (printWriter != null)
				printWriter.close();

		}
	}

	}

