package TwitterAdapter;

import org.dom4j.Element;

/**
 * Created by abc on 4/14/15.
 */
public class SystemOutTweets implements TweetConsumer {
    public String tweet(Element tweet) {
        System.out.println(tweet.asXML());
        return tweet.elementText("id");
    }

    public static void main(String[] args) throws TweetException {
        TweetProducer t = new TweetProducer();
        t.createClient("W0412_Wei", "jiushiTALE123456");
        t.setConsumer(new SystemOutTweets());
        System.out.println("Start tweeting:");
        String id = t.execute(null);
        System.out.println("The End; last tweet: " + id);
    }
}
