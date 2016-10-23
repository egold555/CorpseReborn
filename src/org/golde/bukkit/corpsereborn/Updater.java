package org.golde.bukkit.corpsereborn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Updater
{

    public enum UpdateResult
    {
        NO_UPDATE,
        DISABLED,
        FAIL,
        UPDATE_AVAILABLE,
        DEV;
    }

    public class UpdateResults
    {
        private UpdateResult result;
        private String version;

        public UpdateResults(UpdateResult result, String version)
        {
            this.result = result;
            this.version = version;
        }

        public UpdateResult getResult()
        {
            return result;
        }

        public String getVersion()
        {
            return version;
        }
    }

    private String urlBase;
    private String APIKey;
    private String currentVersion;

    private String resourceUrl;

    public Updater(String resourceUrl)
    {
        this.resourceUrl = resourceUrl;

        urlBase = "http://www.spigotmc.org/api/general.php";
        APIKey = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
        currentVersion = Main.getPlugin().getDescription().getVersion().split(" ")[0];
    }

    public UpdateResults checkForUpdates()
    {
        if(ConfigData.shouldCheckForUpdates())
        {
            try
            {
                HttpURLConnection con = (HttpURLConnection) new URL(urlBase).openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.getOutputStream()
                        .write(("key=" + APIKey + "&resource=" + resourceUrl).getBytes("UTF-8"));

                if (con.getResponseCode() == 500)
                    return new UpdateResults(UpdateResult.FAIL, "Server responded with code 500: Internal server error");

                String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();

                con.disconnect();

                switch(compareVersion(version)){
                	case 0: return new UpdateResults(UpdateResult.NO_UPDATE, null);
                	case -1: return new UpdateResults(UpdateResult.UPDATE_AVAILABLE, version);
                	case 1: return new UpdateResults(UpdateResult.DEV, version);
                	default: return new UpdateResults(UpdateResult.FAIL, "This was not suppost to happen!");
                }
                    
            }
            catch (Exception ex)
            {
                return new UpdateResults(UpdateResult.FAIL, ex.toString());
            }
        }
        else
            return new UpdateResults(UpdateResult.DISABLED, null);

    }

    private int compareVersion(String newVersion)
    {
        Scanner currentV = new Scanner(currentVersion);
        Scanner newV = new Scanner(newVersion);

        currentV.useDelimiter("\\.");
        newV.useDelimiter("\\.");

        while(currentV.hasNextInt() && newV.hasNextInt())
        {
            int cV = currentV.nextInt();
            int nV = newV.nextInt();

            if(cV < nV)
            {
                currentV.close();
                newV.close();

                return -1;
            }
            else if(cV > nV)
            {
                currentV.close();
                newV.close();

                return 1;
            }
        }

        if(currentV.hasNextInt())
        {
            currentV.close();
            newV.close();

            return 1;
        }

        currentV.close();
        newV.close();
        return 0;
    }
}