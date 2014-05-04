package com.bigData.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

// One chunk of data
// i.e. {
// 		"Year" : 1880,
// 		"Annual Mean" : -0.28,
// 		"5 yr Mean" : ""
// 		}
public class Data implements Comparable<Data>{

	// Map containing numerical data
	private HashMap<String,Double> numData;

	// Map containing string data
	private HashMap<String,String> strData;

	// Construct a new peice of data
	public Data(){
		numData = new HashMap<String,Double>();
		strData = new HashMap<String,String>();
	}

	// Add one numerical data
	public void addData(String str, Double data){
		numData.put(str, data);
	}

	// Add one string data
	public void addData(String str, String data){
		strData.put(str,data);
	}
	
	// Get the corresponding numerical data with the provided key
	public Double getNumData(String key){
		return numData.get(key);
	}
	
	// Get the corresponding string data with the provided key
	public String getStrData(String key){
		return strData.get(key);
	}
	
	// Get all the numerical data pairs
	public Set<Entry<String,Double>> getNumEntries(){
		return numData.entrySet();
	}
	
	// Get all the string data pairs
	public Set<Entry<String,String>> getStrEntries(){
		return strData.entrySet();
	}

	// Get all the numerical data keys
	protected HashSet<String> getNumKeys(){
		HashSet<String> keys = new HashSet<String>(numData.keySet());
		return keys;
	}
	
	// Get all the string data keys
	protected HashSet<String> getStrKeys(){
		HashSet<String> keys = new HashSet<String>(strData.keySet());
		return keys;
	}

	// Return true if two pieces of data is identical
	// false otherwise
	public boolean equals(Object other){
		if (other==null)
			return false;
		if (getClass()!=other.getClass())
			return false;

		Data otherData = (Data) other;
		return numData.equals(otherData.numData)
		&& strData.equals(otherData.strData);
	}

	// Return the hash code of the data
	public int hashCode(){
		return 7 + 11*numData.hashCode() + 17*strData.hashCode();
	}
	
	// Assume each data contains year field.
	// Compare this Data to other Data by year, if year is not present,
	// sorted randomly.
	public int compareTo(Data other) {
		Double year = this.getNumData("Year");
		Double otherYear = other.getNumData("Year");
		return year.compareTo(otherYear);
	}
}