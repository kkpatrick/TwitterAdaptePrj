package TwitterAdapter;

import org.dom4j.DocumentException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;

/**
 * Created by abc on 4/15/15.
 */
public class GetTimelineMain {

    private final static String HOME_TIMELINE_TABLE = "Home_Time_Line_Table";
    private final static String USER_TIMELINE_TABLE = "User_Time_Line_Table";
    private final static String RETWEETS_TABLE = "Retweets_Table";

    public static void main(String[] args) throws IOException, DocumentException, TweetException {
        PdfTwitter pdf = null;
        if(args.length > 1) return;

        try {
            TimelineType timelineType = TimelineType.HOME_TIMELINE;
            String pdfFileName = "empty.pdf";
            if("home".equals(args[0])) {
                timelineType = TimelineType.HOME_TIMELINE;
                pdfFileName = "tweetsHomeTimeline.pdf";
            }
            else if("user".equals(args[0])) {
                timelineType = TimelineType.USER_TIMELINE;
                pdfFileName = "tweetsUserTimeline.pdf";
            }
            // gets Twitter instance with default credentials
            pdf = null;
            try {
                pdf = new PdfTwitter(pdfFileName);
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
            Paging paging = new Paging(2, 800);
            List<Status> statuses = twitter.getHomeTimeline(paging);
            final String tableName;
            if(timelineType == TimelineType.HOME_TIMELINE) {
                //statuses = twitter.getHomeTimeline();
                tableName = HOME_TIMELINE_TABLE;
            }
            else if(timelineType == TimelineType.USER_TIMELINE) {
                statuses = twitter.getUserTimeline(paging);
                tableName = USER_TIMELINE_TABLE;
            }
            else {
                tableName = "";
            }
            System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");

            GetTimelineMain timeline = new GetTimelineMain();
            Connection con = getConnectionToDababase();
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                pdf.tweet(status);
                timeline.insertStatusIntoDb(status, timelineType, con, tableName);
                List<Status> retweetsStatuses = getRetweetsOfStatus(twitter, status);
                if(retweetsStatuses.size() == 0) {
                    System.out.println("no retweets for this tweet.");
                }
                else {
                    System.out.printf("There are %d retweets for this tweet.\n", retweetsStatuses.size());
                    for (Status retweets : retweetsStatuses) {
                        System.out.println("@" + retweets.getUser().getScreenName() + " - " + retweets.getText());
                    }
                }
            }



        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } finally {
            pdf.close();
        }
    }

    private static Connection getConnectionToDababase() {
        try {
            Connection con = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "", "");
            return con;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Status> getRetweetsOfStatus(Twitter twitter,Status tweet) {
        System.out.println("Showing up to 100 of the first retweets of the status id - [" + tweet.getId() + "].");
        List<Status> statuses = null;
        try {
            statuses = twitter.getRetweets(tweet.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
        for (Status status : statuses) {
            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        }
        return statuses;
    }

    private static boolean insertStatusIntoDb(Status status,
                                              TimelineType timelineType,
                                              Connection con, String tableName) {
        if("" == tableName) return false;
        try {
            //Connection con = null;
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "", "");
            Statement stmt; //创建声明
            stmt = con.createStatement();

            String tweeterName = status.getUser().getName();
            java.util.Date utilDate = status.getCreatedAt();
            int year = utilDate.getYear() + 1900;
            int month = utilDate.getMonth() + 1;
            String date = "" + year + "-" + month + "-" + utilDate.getDate();
            java.sql.Date createDate = java.sql.Date.valueOf(date);
            String tweetContent = status.getText().replace("'", " ");
            String sqlStatement = "INSERT INTO " + tableName + " VALUES('" + tweeterName + "','" + createDate + "','" + tweetContent + "')";
            /*if(timelineType == TimelineType.HOME_TIMELINE) {
            }
            else if(timelineType == TimelineType.USER_TIMELINE) {
                sqlStatement = "INSERT INTO User_Time_Line_Table VALUES('" + tweeterName + "','" + createDate + "','" + tweetContent + "')";
            }*/
            stmt.execute(sqlStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
