package TwitterAdapter;

/**
 * Created by abc on 4/30/15.
 */
public class TweetWithSentiment {
    private String line;
    private String cssClass;


    public TweetWithSentiment() {
    }


    public TweetWithSentiment(String line, String cssClass) {
        super();
        this.line = line;
        this.cssClass = cssClass;
    }


    public String getLine() {
        return line;
    }


    public String getCssClass() {
        return cssClass;
    }


    @Override
    public String toString() {
        return "TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
    }
}
