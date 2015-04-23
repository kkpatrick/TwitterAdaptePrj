package TwitterAdapter;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by abc on 4/14/15.
 */
public class PdfTwitter{
    protected int counter = 0;
    protected Document document;

    public PdfTwitter(String file) throws DocumentException, IOException, com.lowagie.text.DocumentException {
        document = new Document(PageSize.A4, 72, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
    }

    public static void main(String[] args)
            throws DocumentException, org.dom4j.DocumentException,
            FileNotFoundException, IOException, TweetException {
        PdfTwitter pdf = null;
        try {
            pdf = new PdfTwitter("tweets.pdf");
        } catch (com.lowagie.text.DocumentException e) {
            e.printStackTrace();
        }
        try {
            // gets Twitter instance with default credentials
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
        }
        pdf.close();
    }

    public String tweet(Status tweet) throws TweetException {
        try {
            //org.dom4j.Element user = tweet.element("user");
            // we create the table
            PdfPTable table = new PdfPTable(new float[]{ 1, 5, 6 });
            table.setWidthPercentage(100);
            table.setSpacingBefore(8);
            // first row
            table.getDefaultCell().setPadding(5);
            // first column = empty
            table.addCell("");
            // second column = screen name
            //table.addCell(user.elementText("screen_name"));
            table.addCell(tweet.getUser().getScreenName());
            // third column = date
            //table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setHorizontalAlignment(0);
            //table.addCell(tweet.elementText("created_at"));
            table.addCell(tweet.getCreatedAt().toString());
            // second row
            //table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(1);
            table.getDefaultCell().setRotation(90);
            table.getDefaultCell().setFixedHeight(34);
            // first column = counter
            table.addCell(String.valueOf(++counter));
            table.getDefaultCell().setRotation(0);
            // second and third column = tweet text
            table.getDefaultCell().setColspan(2);
            Phrase p = new Phrase();
            p.add(new Phrase(tweet.getUser().getName()));
            p.add(new Phrase(" tweets: "));
            p.add(new Phrase(tweet.getText()));
            table.addCell(p);
            // we add the table
            document.add(table);
            // we return the tweet id
            return String.valueOf(tweet.getId());
        } catch (com.lowagie.text.DocumentException e) {
            e.printStackTrace();
            return "error";
        }
/*
    @Override
    public String tweet(Element tweet) throws TweetException {
        try {
            org.dom4j.Element user = tweet.element("user");
            // we create the table
            PdfPTable table = new PdfPTable(new float[]{ 1, 5, 6 });
            table.setWidthPercentage(100);
            table.setSpacingBefore(8);
            // first row
            table.getDefaultCell().setPadding(5);
            // first column = empty
            table.addCell("");
            // second column = screen name
            table.addCell(user.elementText("screen_name"));
            // third column = date
            //table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setHorizontalAlignment(0);
            table.addCell(tweet.elementText("created_at"));
            // second row
            //table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(1);
            table.getDefaultCell().setRotation(90);
            table.getDefaultCell().setFixedHeight(34);
            // first column = counter
            table.addCell(String.valueOf(++counter));
            table.getDefaultCell().setRotation(0);
            // second and third column = tweet text
            table.getDefaultCell().setColspan(2);
            Phrase p = new Phrase();
            p.add(new Phrase(user.elementText("name")));
            p.add(new Phrase(" tweets: "));
            p.add(new Phrase(tweet.elementText("text")));
            table.addCell(p);
            // we add the table
            document.add(table);
            // we return the tweet id
            return tweet.elementText("id");
        } catch (com.lowagie.text.DocumentException e) {
            e.printStackTrace();
            return "error";
        }
    }
*/


}
    public void open() {
        document.open();
    }
    public void close() {
        document.close();
    }
}
