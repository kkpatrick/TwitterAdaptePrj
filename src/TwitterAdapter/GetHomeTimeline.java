package TwitterAdapter;

import org.dom4j.DocumentException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.sql.*;
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

            GetHomeTimeline homeLine = new GetHomeTimeline();
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                pdf.tweet(status);
                homeLine.insertStatusIntoDb(status);
            }

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } finally {
            pdf.close();
        }
    }

    private void setUpNewDatabase() {
        try {
            Connection con = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance() ;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "", "");
            Statement stmt; //创建声明
            stmt = con.createStatement();

            //stmt.execute("CREATE table testTable(col1 int, col2 int);");
            //stmt.executeUpdate("INSERT INTO testTable values(12, 20);");
            //ResultSet res = stmt.executeQuery("SELECT * FROM testTable;");

            //stmt.executeUpdate("CREATE table Home_Time_Line_Table(ID INT NOT NULL AUTO_INCREMENT, tweeterName VARCHAR(30), createDate DATE, tweetContent VARCHAR(200), PRIMARY KEY (ID));");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean insertStatusIntoDb(Status status) {
        try {
            Connection con = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "", "");
            Statement stmt; //创建声明
            stmt = con.createStatement();

            String tweeterName = status.getUser().getName();
            java.util.Date utilDate = status.getCreatedAt();
            int year = utilDate.getYear() + 1900;
            String date = "" + year + "-" + utilDate.getMonth() + "-" + utilDate.getDate();
            java.sql.Date createDate = java.sql.Date.valueOf(date);
            String tweetContent = status.getText();

            String sqlStatement = "INSERT INTO Home_Time_Line_Table VALUES('" +tweeterName + "','" + createDate +"','" + tweetContent + "')";
            stmt.execute(sqlStatement);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }
}
