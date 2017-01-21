package me.nrubin29.pastebinapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class Paste {

    private PastebinAPI api;
    private String key, name;
    private int size, hits;
    private Date date, expiredate;
    private PrivacyLevel level;
    private Format format;
    private URL url;

    protected Paste(PastebinAPI api, ArrayList<String> args) throws PastebinException {
        this.api = api;
        for (String arg : args) {
            arg = arg.substring(arg.indexOf("_") + 1, arg.lastIndexOf("<"));
            if (arg.startsWith("key")) key = sub(arg);
            else if (arg.startsWith("date")) date = new Date(Long.valueOf(sub(arg)));
            else if (arg.startsWith("title")) name = sub(arg);
            else if (arg.startsWith("size")) size = Integer.valueOf(sub(arg));
            else if (arg.startsWith("expire_date")) expiredate = new Date(Long.valueOf(sub(arg)));
            else if (arg.startsWith("private")) level = PrivacyLevel.valueOf(Integer.parseInt(sub(arg)));
            else if (arg.startsWith("format_long")) format = Format.valueOf(sub(arg));
            else if (arg.startsWith("url")) {
                try {
                    url = new URL(sub(arg));
                } catch (MalformedURLException e) {
                    throw new PastebinException("Invalid URL returned!");
                }
            } else if (arg.startsWith("hits")) hits = Integer.valueOf(sub(arg));
        }
    }

    private String sub(String str) {
        return str.substring(str.indexOf(">") + 1);
    }

    /**
     * Gets the paste key.
     * @return The paste key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the paste name.
     * @return The paste name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the paste size.
     * @return The paste size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the number of paste hits.
     * @return The number of paste hits.
     */
    public int getHits() {
        return hits;
    }

    /**
     * Gets the date of creation.
     * @return The date of creation.
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * Gets the date of expiration.
     * @return The date of expiration.
     */
    public Date getExpireDate() {
        return new Date(expiredate.getTime());
    }

    /**
     * Gets the privacy level of the paste.
     * @return A value from the PrivacyLevel enum representing the paste privacy level.
     * @see PrivacyLevel
     */
    public PrivacyLevel getLevel() {
        return level;
    }

    /**
     * Gets the format of the paste.
     * @return A value from the Format enum representing the paste format.
     * @see Format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Gets the URL of the paste.
     * @return The URL of the paste.
     */
    public URL getURL() {
        return url;
    }

    /**
     * Gets the raw text of the paste.
     * @return A String[] containing all lines of the paste.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public String[] getText() throws PastebinException {
        Poster p = api.getNewPoster();
        try { return p.withURL(new URL("http://pastebin.com/raw.php?i=" + key)).post(); }
        catch (MalformedURLException ignored) { return null; }
    }

    /**
     * Gets the raw text of the paste.
     * @param lineSeparator The text that separates each line of the raw text.
     * @return A String containing all lines of the paste separated by the lineSeparator.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public String getText(String lineSeparator) throws PastebinException {
        StringBuffer result = new StringBuffer();

        for (String str : getText()) {
            result.append(str + lineSeparator);
        }

        return result.toString();
    }
}
