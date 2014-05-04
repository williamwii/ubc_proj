package com.ezbook.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityKeyGenerator {
	
	public static String genNewPostKey(String email){
		String secret_key = email+"bookfighting";
		return sha256(secret_key);
	}
	
	public static String genEditPostKey(String email){
		String secret_key = email+"editbosungbook";
		return sha256(secret_key);
	}
	
	public static String genDeletePostKey(String postID){
		String secret_key = postID+"callbosungmaybe";
		return sha256(secret_key);
	}
	
	public static String genSearchMarketPlaceKey(){
		String secret_key = "whereisbosungbooks";
		return sha256(secret_key);
	}
	
	public static String genSearchSubscriptionKey(String ISBN){
		String secret_key = ISBN+"ismybookavailableyet";
		return sha256(secret_key);
	}
	
	public static String genSearchMySubscriptionKey(String email){
		String secret_key = email+"findmyfavouritebook";
		return sha256(secret_key);
	}
	
	public static String genSubscriptionKey(String email){
		String secret_key = email+"subgangnamstyle";
		return sha256(secret_key);
	}
	
	public static String genUnsubcriptionKey(String email,String ISBN){
		String secret_key = email+ISBN;
		return sha256(secret_key);
	}
	
	private static String sha256(String value){
		try{
		MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(value.getBytes());
        byte byteData[] = md.digest();
		
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
		return sb.toString();
		}
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return "";
	}
}