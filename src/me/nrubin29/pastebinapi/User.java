package me.nrubin29.pastebinapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class User {

    private PastebinAPI api;
    private String username, password, userkey;
    private ExpireDate expire;
    private URL avatarURL, website;
    private PrivacyLevel privacyLevel;
    private String email, location;
    private AccountType type;

    protected User(PastebinAPI api, String username, String password) throws PastebinException {
        this.api = api;
        this.username = username;
        this.password = password;

        Poster login = api.getNewPoster();

        try { login.withURL(new URL("http://pastebin.com/api/api_login.php")); }
        catch (MalformedURLException ignored) { }

        login.withArg("api_user_name", username);
        login.withArg("api_user_password", password);

        userkey = login.post()[0];

        Poster info = api.getNewPoster();
        info.withArg("api_user_key", userkey);
        info.withArg("api_option", "userdetails");

        Parser p = new Parser(info.post());
        p.addKey("expiration, avatar_url", "private", "website", "email", "location", "account_type");
        HashMap<String, String> ret = p.parse();
        for (String key : ret.keySet()) {
            if (key.equals("expiration")) expire = ExpireDate.valueOf(key);
            else if (key.equals("avatar_url")) avatarURL = getURL(key);
            else if (key.equals("private")) privacyLevel = PrivacyLevel.valueOf(Integer.parseInt(key));
            else if (key.equals("website")) website = getURL(key);
            else if (key.equals("email")) email = key;
            else if (key.equals("location")) location = key;
            else if (key.equals("account_type")) type = AccountType.valueOf(Integer.parseInt(key));
        }
    }

    private URL getURL(String url) {
        try { return new URL(url); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }

    /**
     * Get all user pastes.
     * @return An ArrayList<Paste> containing all pastes.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public Paste[] getPastes() throws PastebinException {
        return getPastes(Integer.MAX_VALUE);
    }

    /**
     * Get all user pastes.
     * @param results_limit Amount of results to limit to.
     * @return An ArrayList<Paste> containing all pastes.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public Paste[] getPastes(int results_limit) throws PastebinException {
        return api.parse(api.getNewPoster().withArg("api_results_limit", results_limit).withArg("api_option", "list").post());
    }

    /**
     * Remove a user's paste
     * @param pasteKey The paste's key.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public void removePaste(String pasteKey) throws PastebinException {
        api.getNewPoster().withArg("api_user_key", userkey).withArg("api_paste_key", pasteKey).withArg("api_option", "delete");
    }

    /**
     * Remove a user's paste
     * @param p The paste to remove.
     * @throws PastebinException Thrown if an error occurs; contains the error message.
     */
    public void removePaste(Paste p) throws PastebinException {
        removePaste(p.getKey());
    }

    /**
     * Begin constructing a paste.
     * @return A new instance of the CreatePaste class.
     */
    public CreatePaste createPaste() {
        return new CreatePaste(api).withUser(this);
    }

    /**
     * Get the user's username.
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the user's password.
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    protected String getUserKey() {
        return userkey;
    }

    /**
     * Get the user's expire date.
     * @return A value from the ExpireDate enum representing the user's expire date.
     */
    public ExpireDate getExpireDate() {
        return expire;
    }

    /**
     * Get the user's avatar URL.
     * @return The user's avatar URL.
     */
    public URL getAvatarURL() {
        return avatarURL;
    }

    /**
     * Get the user's website.
     * @return The user's website.
     */
    public URL getWebsite() {
        return website;
    }

    /**
     * Get the user's privacy level.
     * @return A value from the PrivacyLevel enum representing the user's privacy level.
     */
    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    /**
     * Get the user's email.
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the user's location.
     * @return The user's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get the user's account type.
     * @return A value from the AccountType enum representing the user's account type.
     */
    public AccountType getAccountType() {
        return type;
    }
}