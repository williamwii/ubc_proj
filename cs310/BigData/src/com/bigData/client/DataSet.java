package com.bigData.client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * A DataSet is a list of data imported from one URL or local file.
 *
 */
public class DataSet {

	// Title of the DataSet
	private String title;
	
	// A set of data, no duplicates
	private TreeSet<Data> dataSet;
	
	// A set of string representing the numerical keys
	private HashSet<String> numKeys;
	
	// A set of string representing the string keys
	private HashSet<String> strKeys;
	
	// if isPublic is false, this DataSet can only be viewed by authorized people
	private boolean isPublic;
	
	/**
	 * Create a new DataSet with the provided title.
	 * isPublic is set to be true by default.
	 * 
	 * @param title the title of the new DataSet
	 */
	public DataSet(String title){
		this.title = title;
		dataSet = new TreeSet<Data>();
		isPublic = true;
		numKeys = new HashSet<String>();
		strKeys = new HashSet<String>();
	}
	
	/**
	 * Create a new DataSet with the provided title, data, and isPublic.
	 * 
	 * @param title title of the DataSet
	 * @param data the data of the DataSet
	 * @param isPublic accessibility of the public
	 */
	public DataSet(String title,Collection<Data> data, boolean isPublic){
		this.title = title;
		dataSet = new TreeSet<Data>();
		dataSet.addAll(data);
		numKeys = new HashSet<String>();
		strKeys = new HashSet<String>();
		for (Data d : data){
			numKeys.addAll(d.getNumKeys());
			strKeys.addAll(d.getStrKeys());
		}
		this.isPublic = isPublic;
	}
	
	// Return the accessibility of the public
	public boolean isPublic(){
		return isPublic;
	}
	
	// Return the title of the DataSet
	public String getTitle(){
		return title;
	}
	
	/**
	 * Get and return the numerical data keys as a string array
	 * 
	 * @return string array containing the numerical data keys
	 */
	public HashSet<String> getnumKeys(){
		if ( numKeys.isEmpty() )
			setKeys();
		return numKeys;
	}
	
	/**
	 * Get and return the string data keys as a string array
	 * 
	 * @return string array containing the string data keys
	 */
	public HashSet<String> getStrKeys(){
		if ( strKeys.isEmpty() )
			setKeys();
		return strKeys;
	}
	
	// output a list of double specified by the key
	// e.g. a list of "Annual Mean"
	// can be used to calculate stats
	public ArrayList<Double> getNumData(String key){
		ArrayList<Double> lon = new ArrayList<Double>();
		for (Data d : dataSet){
			Double temp = d.getNumData(key);
			lon.add(temp);
		}
		return lon;
	}
	
	// Output a list of String data specified by the key.
	public ArrayList<String> getStrData(String key){
		ArrayList<String> los = new ArrayList<String>();
		for (Data d : dataSet){
			String temp = d.getStrData(key);
			los.add(temp);
		}
		return los;
	}
	
	/*
	 * Set numKeys and strKeys.
	 */
	private void setKeys(){
		for (Data d : dataSet){
			numKeys.addAll(d.getNumKeys());
			strKeys.addAll(d.getStrKeys());
		}
	}
	
	/*
	 * Set isPublic to be the provided boolean.
	 */
	public void setPublic(boolean bool){
		isPublic = bool;
	}
	
	/*
	 * Set the title of the DataSet to be the provided newTitle.
	 */
	public void setTitle(String newTitle){
		title = newTitle;
	}
	
	/**
	 * Add one data to the set
	 */
	public void addDataToSet(Data data){
		if (data!=null)
			dataSet.add(data);
	}

	/**
	 * Get the number of data in the DataSet.
	 * 
	 */
	public int getNumberOfData(){
		return dataSet.size();
	}
	
	/**
	 * Return the set of data.
	 */
	public TreeSet<Data> getDataSet(){
		return dataSet;
	}
}