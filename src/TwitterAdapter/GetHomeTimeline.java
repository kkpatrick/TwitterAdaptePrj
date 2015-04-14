package TwitterAdapter;

import org.dom4j.DocumentException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by abc on 4/14/15.
 */
public class GetHomeTimeline {
    public static void main(String[] args) throws IOException, DocumentException, TweetException {
        PdfTwitter pdf = null;
        try {
            // gets Twitter instance with default credentials
            pdf = null;
            try {
                pdf = new PdfTwitter("tweetsHomeTimeLine.pdf");
            } catch (com.lowagie.text.DocumentException e) {
                e.printStackTrace();
            }

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("8xVdASCKaXO7QHO7CO38ctGsG")
                    .setOAuthConsumerSecret("KOlHitjCWAcqp9vX9P9f07bKtMsgf0B6sQMoNGZNXpsnwq2UGS")
                    .setOAuthAccessToken("1547081905-JLVDbHMF0KVQHJXVOKBMxJTBrPvmibRrD1fDSH3")
                    .setOAuthAccessTokenSecret("BfO2cOsLBAXetL99ULgtOpN9UBDpzW9oD3IL28WrQA1Bc");
            TwitterFactory twitterFactory = new TwitterFactory(cb.build());
            //Twitter twitter = new TwitterFactory().getInstance();
            Twitter twitter = twitterFactory.getInstance();
            User user = twitter.verifyCredentials();
            List<Status> statuses = twitter.getHomeTimeline();
            System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                pdf.tweet(status);
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } finally {
            pdf.close();
        }
    }
}
