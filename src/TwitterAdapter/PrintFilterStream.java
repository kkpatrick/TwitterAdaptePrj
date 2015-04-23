package TwitterAdapter;

/**
 * Created by abc on 4/22/15.
 */
import com.sun.jdi.LongValue;
import org.dom4j.DocumentException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
public class PrintFilterStream {
    private final static String DATABASE_TABLE_NAME = "topic1";
    private final static String STATEMENT_CREATE_TABLE = "CREATE TABLE topic1 (\n" +
            "  status_ID VARCHAR(32) NOT NULL ,\n" +
            "  user_ID LONG,\n" +
            "  user_name VARCHAR(50),\n" +
            "  create_date DATE,\n" +
            "  geo_location_latitude DOUBLE,\n" +
            "  geo_location_longitude DOUBLE,\n" +
            "  language_code VARCHAR(10),\n" +
            "  is_retweet BOOLEAN,\n" +
            "  is_retweeted BOOLEAN,\n" +
            "  retweet_count INT,\n" +
            "  is_favorited BOOLEAN,\n" +
            "  favorite_count INT,\n" +
            "  is_possibly_sensitive BOOLEAN,\n" +
            "  PRIMARY KEY (status_ID)\n" +
            ")";
    private final static String PDF_FILE_NAME = "File_Topic_1.pdf";
    private final static Connection con = getConnectionToDababase();
    private static PdfTwitter pdf = null;
    private static int statusCount = 0;
    private final static int maxStatusCount = 100;
    /**
     * Main entry of this application.
     *
     * @param args follow(comma separated user ids) track(comma separated filter terms)
     * @throws twitter4j.TwitterException
     */
    public static void main(String[] args) throws TwitterException, DocumentException, com.lowagie.text.DocumentException, IOException {
        if (args.length < 1) {
            System.out.println("Usage: java twitter4j.examples.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]");
            System.exit(-1);
        }

        pdf = new PdfTwitter(PDF_FILE_NAME);

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                statusCount++;
                if(statusCount < maxStatusCount) {
                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getId() + " - " + status.getText());
                    insertStatusIntoDb(status);
                    try {
                        pdf.tweet(status);
                    } catch (TweetException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println("Status count reached max value, exit");
                    pdf.close();
                    System.exit(0);
                }
            }


            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }


            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }


            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }


            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }


            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("8xVdASCKaXO7QHO7CO38ctGsG")
                .setOAuthConsumerSecret("KOlHitjCWAcqp9vX9P9f07bKtMsgf0B6sQMoNGZNXpsnwq2UGS")
                .setOAuthAccessToken("1547081905-JLVDbHMF0KVQHJXVOKBMxJTBrPvmibRrD1fDSH3")
                .setOAuthAccessTokenSecret("BfO2cOsLBAXetL99ULgtOpN9UBDpzW9oD3IL28WrQA1Bc");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(listener);
        ArrayList<Long> follow = new ArrayList<Long>();
        ArrayList<String> track = new ArrayList<String>();
        for (String arg : args) {
            if (isNumericalArgument(arg)) {
                for (String id : arg.split(",")) {
                    follow.add(Long.parseLong(id));
                }
            } else {
                track.addAll(Arrays.asList(arg.split(",")));
            }
        }
        long[] followArray = new long[follow.size()];
        for (int i = 0; i < follow.size(); i++) {
            followArray[i] = follow.get(i);
        }
        String[] trackArray = track.toArray(new String[track.size()]);


        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.filter(new FilterQuery(0, followArray, trackArray));

    }


    private static boolean isNumericalArgument(String argument) {
        String args[] = argument.split(",");
        boolean isNumericalArgument = true;
        for (String arg : args) {
            try {
                Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                isNumericalArgument = false;
                break;
            }
        }
        return isNumericalArgument;
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

    private static void insertStatusIntoDb(Status status) {
        final String status_ID = ((Long)(status.getId())).toString();
        final Long user_ID = status.getUser().getId();
        final String user_name = status.getUser().getName();

        final java.util.Date utilDate = status.getCreatedAt();
        final int year = utilDate.getYear() + 1900;
        final int month = utilDate.getMonth() + 1;
        final String date = "" + year + "-" + month + "-" + utilDate.getDate();
        final java.sql.Date create_date = java.sql.Date.valueOf(date);

        Double geo_location_latitude = null;
        Double geo_location_longitude = null;
        if(null != status.getGeoLocation()) {
            geo_location_latitude = status.getGeoLocation().getLatitude();
            geo_location_longitude = status.getGeoLocation().getLongitude();
        }
        final String language_code = status.getLang();
        final boolean is_retweet = status.isRetweet();
        final boolean is_retweeted = status.isRetweeted();
        final int retweet_count = status.getRetweetCount();
        final boolean is_favorited = status.isFavorited();
        final int favorit_count = status.getFavoriteCount();
        final boolean is_possibly_sensitive = status.isPossiblySensitive();
        final String text = status.getText().replace("'", " ");

        final String STATEMENT_INSERT = "INSERT INTO " + DATABASE_TABLE_NAME + " VALUES('" + status_ID + "','" +
                user_ID + "','" +
                user_name + "','" + create_date + "'," + geo_location_latitude + "," + geo_location_longitude + ",'" + language_code + "'," +
                is_retweet + "," + is_retweeted + "," + retweet_count + "," + is_favorited + "," + favorit_count + "," + is_possibly_sensitive + ",'" + text + "')";

        try {
            Statement stmt; //创建声明
            stmt = con.createStatement();
            stmt.execute(STATEMENT_INSERT);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
