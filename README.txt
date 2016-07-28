********************************************************************************
README
********************************************************************************
Version 1.0 31/07/16

Sentiment Analyse der Tweets von Hillary Clinton und Donald Trump.

1) Bevor das Programm gestartet werden kann müssen zwei JAR Datein eingebunden werden.
Hierfür laden Sie zum einen Open Nlp https://opennlp.apache.org/ sowie Twitter4J 
http://twitter4j.org/en/index.html runter.

2) Binden Sie nun die zwei JAR-Dateien von Open NLP und Twitter4J ein.
Wählen Sie im Menü "Project" den Eintrag "Properties" aus. Nun wählen Sie 
"Java Build Path" und binden im Menü-Punkt Libraries die JAR-Dateien ein. 
z.B. opennlp-tools-1.6.0.jar, twitter4j-core-4.0.4.jar

3) Legen Sie sich über den Link https://apps.twitter.com/ eine neue App an.
Im Menüpunkt "Keys and Access Tokens" müssen Sie neue Access Tokens mit 
dem Button "Create my access token" anlegen. 

4) In Ihrer App können Sie die Keys und Access Tokens im Menüpunkt "OAuth" 
ansehen, fügen sie diese in die Klasse TwitterTweets, an den Stellen:
setOAuthConsumerKey, setOAuthConsumerSecret, setOAuthAccessToken und 
setOAuthAccessTokenSecret an die Stelle der x-en ein.

5) Nun ist das Programm bereit zum Laufen.


