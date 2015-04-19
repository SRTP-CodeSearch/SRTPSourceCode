package cn.edu.seu.ise.common.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * A helper class used to check the URL. 
 * 
 * @author Dong Qiu
 *
 */
public class URLHelper {
	
	/** The regex of the url address */
	private static String urlRegex = "^(https?|ftp|file|scp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
	/**
	 * Checks whether the URL address is reachable. 
	 * 
	 * @param urlAddress  the url address
	 * @return  {@code true} if the url address is reachable;
	 * {@code false} otherwise
	 */
	public static boolean isReachable(String urlAddress) {
		// if the format of the url is not correct
		if(!urlAddress.matches(urlRegex)) {
			return false;
		}
		// for file based url, return true
		if(urlAddress.startsWith("file")) {
			return true;
		}
        try {
            //make a URL to a known source
            URL url = new URL(urlAddress);
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setRequestMethod("HEAD");
            urlConnect.setConnectTimeout(1000);
            urlConnect.setReadTimeout(1000);
            // trying to retrieve data from the source. 
            // If there is no connection, this line will fail
           if(urlConnect.getResponseCode() == 200) {
        	   return true;
           }
           return false;
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
	
	/**
	 * Transforms jar path to jar url. 
	 * 
	 * @param jarPath  the path of jar file
	 * 
	 * @return  the url of jar 
	 * @throws MalformedURLException
	 */
	public static URL toJarUrl(String jarPath) throws MalformedURLException {
		return new URL("jar:file:" + jarPath + "!/"); 
	}
}
