package me.nrubin29.pastebinapi;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Poster {

    private PastebinAPI api;
    private HashMap<String, Object> args = new HashMap<>();
    private URL url;

    protected Poster(PastebinAPI api) {
        this.api = api;
    }

    protected Poster withArg(String key, Object value) {
        args.put(key, value);
        return this;
    }

    protected Poster withURL(URL url) {
        this.url = url;
        return this;
    }

    protected String[] post() {
        try {
            StringBuffer a = new StringBuffer("api_dev_key=" + api.getAPIKey());

            for (Map.Entry<String, Object> e : args.entrySet()) {
                a.append("&" + e.getKey() + "=" + e.getValue());
            }

            String text = a.toString();

            if (url == null) url = new URL("http://pastebin.com/api/api_post.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + text.getBytes().length);
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(text);
            wr.flush();
            wr.close();

            connection.disconnect();

            Scanner s = new Scanner(connection.getInputStream());

            ArrayList<String> output = new ArrayList<String>();

            while (s.hasNext()) {
                String next = s.nextLine();
                output.add(next);
            }

            if (output.get(0).startsWith("Bad API request")) {
            	s.close();
                throw new PastebinException(output.get(0));
            }
            s.close();
            return output.toArray(new String[output.size()]);
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }
}