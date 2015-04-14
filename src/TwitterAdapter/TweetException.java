package TwitterAdapter;

/**
 * Created by abc on 4/14/15.
 */
public class TweetException extends Exception {
    private static final long serialVersionUID = 7577136074623618615L;
    public TweetException(Exception e) {
        super(e);
    }
}
