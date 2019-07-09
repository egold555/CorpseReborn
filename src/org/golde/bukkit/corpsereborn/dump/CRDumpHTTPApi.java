package org.golde.bukkit.corpsereborn.dump;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CRDumpHTTPApi {

	private static final String PASTE_URL = "http://web.golde.org/temp/pastetest/";
	private static final String ARGS_UPLOAD = "index.php";
	private static final String ARGS_VIEW = "view.php?id=";
	
	public static String paste(String body) throws IOException {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(PASTE_URL + ARGS_UPLOAD);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return PASTE_URL + ARGS_VIEW + rd.readLine();

        } 
        catch (IOException e) {
            throw e;
        } 
        finally {
            if (connection == null) {
            	return null;
            }
            connection.disconnect();
        }
    }
	
}
