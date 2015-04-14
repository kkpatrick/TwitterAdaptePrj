package TwitterAdapter;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by abc on 4/14/15.
 */
public class TweetProducer {
    protected HttpClient client;
    protected TweetConsumer consumer;

    public void createClient(String username, String password) {
        client = new HttpClient();
        client.getState().setCredentials(
                new AuthScope("twitter.com", 80, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(username, password));
        client.getParams().setAuthenticationPreemptive(true);
    }

    public void setConsumer(TweetConsumer consumer) {
        this.consumer = consumer;
    }

    public String execute(String since_id) throws TweetException {
        String id = null;
        String tmp;
        GetMethod get = new GetMethod("http://twitter.com/statuses/friends_timeline.xml");
        if (since_id != null && since_id.length() > 0) {
            get.setQueryString("?count=200&since_id=" + since_id);
        }
        else {
            get.setQueryString("?count=200");
        }
        get.setDoAuthentication(true);
        try {
            client.executeMethod(get);
            SAXReader reader = new SAXReader();
            Document document = reader.read(get.getResponseBodyAsStream());
            Element root = document.getRootElement();
            for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                tmp = consumer.tweet((Element)i.next());
                if (id == null) id = tmp;
            }
        } catch (HttpException e) {
            throw new TweetException(e);
        } catch (IOException e) {
            throw new TweetException(e);
        } catch (DocumentException e) {
            throw new TweetException(e);
        } finally {
            get.releaseConnection();
        }
        return id;
    }
}
