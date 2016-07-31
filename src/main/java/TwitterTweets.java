
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTweets {
	public void getAllTweets() {
		ConfigurationBuilder cf = new ConfigurationBuilder();

		//Twitter Keys hier einfügen
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
				int clintonSize = allTweetsClinton.size();
				int trumpSize = allTweetsTrump.size();

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

				if (allTweetsClinton.size() == clintonSize && allTweetsTrump.size() == trumpSize)
					break;
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}

		listInDatei(allTweetsTrump, new File(OpenNLPCategorizer.RES + "trumptweetswithRT.txt"));
		listInDatei(allTweetsClinton, new File(OpenNLPCategorizer.RES + "clintontweetswithRT.txt"));

		CleanTweets cleanTweets = new CleanTweets();
		cleanTweets.cleanTweets();
	}

	/**
	 * Liste mit Tweets in .txt Datei ausgeben
	 */
	private static void listInDatei(List<String> list, File datei) {
		try (PrintWriter printWriter = new PrintWriter(new FileWriter(datei))) {
			for (String s : list) {
				printWriter.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
