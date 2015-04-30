package TwitterAdapter;

/**
 * Created by abc on 4/30/15.
 */
import java.util.Properties;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
public class SentimentAnalyzer {

    private final static String DatabaseName = "Furious";

    public TweetWithSentiment findSentiment(String line) {


        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }


            }
        }
        if (/*mainSentiment == 2 || */mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }
        TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return tweetWithSentiment;


    }

/*
    private String toCss(int sentiment) {
        switch (sentiment) {
            case 0:
                return "alert alert-danger";
            case 1:
                return "alert alert-danger";
            case 2:
                return "alert alert-warning";
            case 3:
                return "alert alert-success";
            case 4:
                return "alert alert-success";
            default:
                return "";
        }
    }
*/
private String toCss(int sentiment) {
    switch (sentiment) {
        case 0:
            return "Extremly negative";
        case 1:
            return "Negative";
        case 2:
            return "Neutral";
        case 3:
            return "Positive";
        case 4:
            return "Extremly positive";
        default:
            return "";
    }
}

    public static void main(String[] args) {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

        TweetWithSentiment tweetWithSentiment = sentimentAnalyzer
                .findSentiment("I don't like Furious 7! It sucks.");
        System.out.println(tweetWithSentiment);


    }
}
