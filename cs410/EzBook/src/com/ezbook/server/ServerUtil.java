package com.ezbook.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class ServerUtil {

	protected static String SERVER_SECRET = "75970459fe79d758445da2b7c9edf3e46dcf99b8bdaad9420af1d3847e40acb4";
	
	private static Cache cache;
	
	public static Cache getCache(){
		if (cache==null) {
			try {
				HashMap<Object, Object> props = new HashMap<Object,Object>();
				props.put(GCacheFactory.EXPIRATION_DELTA, 300);
				CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
				cache = cacheFactory.createCache(props);
			} catch (CacheException e) {
				return null;
			}
		}
		return cache;
	}
	
    public static String fetchURL(String urlString) throws MalformedURLException, IOException{
        String retStr = "";
        
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = reader.readLine()) != null) { retStr += line; }
        reader.close();
        
        return retStr;
    }
    
    public static String fetchURLPost(HttpURLConnection connection) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String retStr = "";
        String line;
        while ((line = reader.readLine()) != null) { retStr += line; }
        reader.close();
        
		return retStr;
    }
    
	private static String CheckDigits = new String("0123456789X0");
	 /////////////// Change a character to its integer value ///////
	  static int CharToInt(char a) {
		  return Character.getNumericValue(a);
	  }

	  ////////////////////// Convert ISBN-13 to ISBN-10 //////
	  public static String ISBN1310(String ISBN) {
		  if(ISBN.length()==10)//already ISBN 10
			  return ISBN;
		  if(ISBN.length()<12){
			  return ISBN;
		  }
		  
	    String s9;
	    int i, n, v;
	    boolean ErrorOccurred;
	    ErrorOccurred = false;
	    s9 = ISBN.substring(3, 12);
	    n = 0;
	    for (i=0; i<9; i++) {
	      if (!ErrorOccurred) {
	        v = CharToInt(s9.charAt(i));
	        if (v==-1) ErrorOccurred = true;
	        else n = n + (10 - i) * v; 
	      }
	    }
	    if (ErrorOccurred) return "ERROR";
	    else {
	      n = 11 - (n % 11);
	      return s9 + CheckDigits.substring(n, n+1); 
	    }
	  }

	  ////////////////////// Convert ISBN-10 to ISBN-13 //////
	  public static String ISBN1013(String ISBN) {
		  if(ISBN.length()==13)//Already ISBN 13
			  return ISBN;
		  if(ISBN.length()<9){
			  return ISBN;
		  }
	    String s12;
	    int i, n, v;
	    boolean ErrorOccurred;
	    ErrorOccurred = false;
	    s12 = "978" + ISBN.substring(0, 9);
	    n = 0;
	    for (i=0; i<12; i++) {
	      if (!ErrorOccurred) {
	        v = CharToInt(s12.charAt(i));
	        if (v==-1) ErrorOccurred = true;
	        else {
	          if ((i % 2)==0) n = n + v;
	          else n = n + 3*v;
	        }
	      }
	    }
	    if (ErrorOccurred) return "ERROR";
	    else {
	      n = n % 10;
	      if (n!=0) n = 10 - n;
	      return s12 + CheckDigits.substring(n, n+1);
	    }
	  }
	
}
