package me.nrubin29.pastebinapi;

import java.io.*;

public class CreatePaste {
	
	private PastebinAPI api;
	
	protected CreatePaste(PastebinAPI api) {
		this.api = api;
	}

    private User user;
    private File file;
	private String text, name;
	private Format format;
	private PrivacyLevel privacylevel = PrivacyLevel.PUBLIC;
	private ExpireDate expiredate = ExpireDate.NEVER;

    protected CreatePaste withUser(User user) {
        this.user = user;
        return this;
    }

	/**
	 * Sets the text of the paste. If you use this method, you cannot use withFile.
	 * @param text The text of the paste.
	 * @return The same instance of CreatePaste with the text set as given.
	 */
	public CreatePaste withText(String text) {
		this.text = text;
		return this;
	}

    /**
     * Sets the text of the paste to the contents of the file. If you use this method, you cannot use withText.
     * @param file The file from which to get the text of the paste.
     * @return The same instance of CreatePaste with the file set as given.
     */
    public CreatePaste withFile(File file) {
        this.file = file;
        return this;
    }

	/**
	 * Sets the name of the paste.
	 * @param pastename The name of the paste.
	 * @return The same instance of CreatePaste with the name set as given.
	 */
	public CreatePaste withName(String pastename) {
		this.name = pastename;
		return this;
	}
	
	/**
	 * Sets the paste format.
	 * @param pasteformat The paste format.
	 * @return The same instance of CreatePaste with the format set as given.
	 */
	public CreatePaste withFormat(Format pasteformat) {
		this.format = pasteformat;
		return this;
	}
	
	/**
	 * Sets the expiration date of the paste.
	 * @param pasteexpiredate The expiration date of the paste.
	 * @return The same instance of CreatePaste with the expiration date set as given.
	 */
	public CreatePaste withExpireDate(ExpireDate pasteexpiredate) {
		this.expiredate = pasteexpiredate;
		return this;
	}
	
	/**
	 * Sets the privacy level of the paste.
	 * @param privacylevel The privacy level of the paste.
	 * @return The same instance of CreatePaste with the privacy level set as given.
	 */
	public CreatePaste withPrivacyLevel(PrivacyLevel privacylevel) {
		this.privacylevel = privacylevel;
		return this;
	}
	
	/**
	 * Posts using the information given. You should probably run this in a new thread.
	 * @return The URL of the paste.
     * @throws PastebinException Thrown if an error occurs; contains the error message returned by Pastebin.
     * @throws IOException Thrown if you use withFile instead of withText and an IOException is thrown.
	 */
	public String post() throws PastebinException, IOException {
		Poster p = api.getNewPoster();

        p.withArg("api_option", "paste");
        if (text != null) p.withArg("api_paste_code", text);
        else if (file != null) {
            StringBuffer contents = new StringBuffer();
            BufferedReader read = new BufferedReader(new FileReader(file));
            while(read.ready()) contents.append(read.readLine() + "\n");
            read.close();
            p.withArg("api_paste_code", contents.toString());
        }
        if (name != null) p.withArg("api_paste_name", name);
        if (format != null) p.withArg("api_paste_format", format.getFormat());
        p.withArg("api_paste_expire_date", expiredate.getCode());
        p.withArg("api_paste_private", privacylevel.getLevel());
        if (user != null) p.withArg("api_user_key", user.getUserKey());

        return p.post()[0];
	}
}