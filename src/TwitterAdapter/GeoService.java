package TwitterAdapter;

//import com.oracle.tools.packager.IOUtils;
//import sun.misc.IOUtils;
import com.oracle.tools.packager.IOUtils;
import twitter4j.*;
//import twitter4j.JSONObject;

import javax.json.JsonArray;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by abc on 5/9/15.
 */
public class GeoService {
    private static final String GeoCodeUrl         = "http://maps.google.com/maps/geo?output=json";
    private static final String DEFAULT_KEY = "AIzaSyD1J9PQUrlTyFhe9fwbT728zwV3sXE2v8o";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    //call google geo code service to translate place to coordinate;
    public static GeoLocation translatePlaceToGeoLocation(Place place) throws IOException, JSONException {
        GeoLocation res = null;
        String address = place.getStreetAddress();
        URL url = new URL(GeoCodeUrl + "&q=" + URLEncoder.encode(address, "UTF-8")
                + "&key=" + DEFAULT_KEY);
        URLConnection conn = url.openConnection();
        int read;
        String jsonString;
        InputStream input = conn.getInputStream();
        if((read = input.read()) != -1) {
            int availableAmount = input.available();
            if(availableAmount > 0) {
                byte[] into = new byte[availableAmount +1];
                into[0] = (byte)read;
                int readAmount = input.read(into, 1, availableAmount);
                if(readAmount != -1) {
                    CharsetDecoder decoder = UTF8.newDecoder();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(into);

                    CharBuffer charBuffer = decoder.decode(byteBuffer);
                    jsonString = charBuffer.toString();
                    System.out.printf("the output json string is %s", jsonString);
                    JSONObject OringinalObj = new JSONObject(jsonString);
                    JSONArray results = OringinalObj.getJSONArray("results");
                    JSONObject coordinate = ((JSONObject)results.get(0)).getJSONObject("geometry").getJSONObject("location");
                    res = new GeoLocation((double)coordinate.get("lat"), (double)coordinate.get("lng"));
                }
            }
        }
        return res;
    }
    
}
