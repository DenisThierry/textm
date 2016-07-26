package main.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTweets {

	public void getAllTweets() {

		ConfigurationBuilder cf = new ConfigurationBuilder();

		cf.setDebugEnabled(true).setOAuthConsumerKey("").setOAuthConsumerSecret("").setOAuthAccessToken("")
				.setOAuthAccessTokenSecret("");

		TwitterFactory tf = new TwitterFactory(cf.build());
		twitter4j.Twitter twitter = tf.getInstance();

		int pageno = 1;

		/*
		 * Liste für alle Tweets
		 */
		List<String> allTweetsTrump = new ArrayList<String>();
		List<String> allTweetsClinton = new ArrayList<String>();
		String tweetsTrump;
		String tweetsClinton;

		while (true) {

			try {

				int size = allTweetsClinton.size();
				int sizes = allTweetsTrump.size();

				/*
				 * Paging page = new Paging(pageno++, 200); pageno++ --> damit
				 * alle seiten iteriert werden bis zum Ende 200 --> pro Seite
				 * kann Twitter 200 Tweets ausgeben
				 */
				Paging page = new Paging(pageno++, 200);

				/*
				 * Zugriff auf alle Tweets von Trump und Clinton
				 */
				List<Status> atweetsClinton = twitter.getUserTimeline("HillaryClinton", page);
				List<Status> firstTweetsTrump = twitter.getUserTimeline("realDonaldTrump", page);
				/*
				 * Listeninhalt durchlaufen und für alle Tweets nur Tweet
				 * auslesen und in eine neue Liste einfügen
				 */
				for (Status st : atweetsClinton) {
					tweetsClinton = st.getText();
					allTweetsClinton.add(tweetsClinton);
				}

				for (Status st : firstTweetsTrump) {
					tweetsTrump = st.getText();
					allTweetsTrump.add(tweetsTrump);
				}

				if (allTweetsClinton.size() == size && allTweetsTrump.size() == sizes)
					break;

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

		listInDatei(allTweetsTrump, new File("ressources/trumptweetswithRT.txt"));
		listInDatei(allTweetsClinton, new File("ressources/clintontweetswithRT.txt"));
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

		CleanTweets cleantweets = new CleanTweets();
		cleantweets.cleanTweets();
	}
}
