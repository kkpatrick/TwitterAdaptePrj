package TwitterAdapter;

import org.dom4j.Element;

/**
 * Created by abc on 4/14/15.
 */
public interface TweetConsumer {
    public String tweet(Element element) throws TweetException;
}
