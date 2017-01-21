package me.nrubin29.pastebinapi;

import java.util.ArrayList;

public class PastebinAPI {

	private String apikey;
	
	/**
	 * Constructor for PastebinAPI.
	 * @param apikey Your API key. Get yours from http://pastebin.com/api/
	 */
	public PastebinAPI(String apikey) {
		this.apikey = apikey;
	}

    protected Poster getNewPoster() {
        return new Poster(this);
    }
	
	/**
	 * Get the supplied API key.
	 * @return The supplied API key.
	 */
	public String getAPIKey() {
		return apikey;
	}
	
	/**
	 * Begin constructing a paste. To create a paste with a user, use the createPaste() method in the User class.
	 * @return A new instance of the CreatePaste class.
	 */
	public CreatePaste createPaste() {
		return new CreatePaste(this);
	}

    /**
     * Get a new User with a username and password.
     * @param username The username.
     * @param password The password.
     * @return A new instance of User with the username and password authenticated.
     */
    public User getUser(String username, String password) throws PastebinException {
        return new User(this, username, password);
    }

    /**
     * Get a Paste[] containing all trending pastes.
     * @return A Paste[] containing all trending pastes.
     * @throws PastebinException Thrown if an error occurs.
     */
    public Paste[] getTrendingPastes() throws PastebinException {
        Poster p = getNewPoster();
        p.withArg("api_option", "trends");
        return parse(p.post());
    }

    protected Paste[] parse(String[] args) throws PastebinException {
        ArrayList<Paste> pastes = new ArrayList<Paste>();
        ArrayList<String> current = new ArrayList<String>();

        for (String str : args) {
            if (str != null && !str.equals("<paste>")) {
                if (str.equals("</paste>")) {
                    pastes.add(new Paste(this, current));
                    current = new ArrayList<>();
                }
                else if (str.startsWith("<paste_")) current.add(str);
            }
        }
        return pastes.toArray(new Paste[pastes.size()]);
    }
}