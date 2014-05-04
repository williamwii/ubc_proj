package com.bigData.client;

import java.util.HashSet;
import java.util.Collection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.ScatterChart;

/*
 *  Visualization for one DataSet
 */
public class Visualization {

	private final Integer WIDTH = 1200;
	private final Integer HEIGHT = 800;
	
	// DataSet for the visualization
	private DataSet dataSet;

	// A TabPanel containing all tables and graphs representing the DataSet
	private TabPanel displayTabs = new TabPanel();
	
	
	/*
	 * Construct a new visualization with the provided DataSet
	 */
	public Visualization(DataSet dataSet){
		this.dataSet = dataSet;
	}
	
	/*
	 * Visualize the DataSet and add it to the passed in VerticalPanel.
	 * The visualization contains Table, Statistics, Column Chart, Scatter Chart
	 * and Line Chart.
	 */
	public void visualize(final VerticalPanel panel)
	{

         Runnable onLoadCallback = new Runnable() {
 	        public void run() {
 	        	
 	        	displayTabs.setWidth(WIDTH.toString());
 	        	displayTabs.setHeight(HEIGHT.toString());
 	        	
 	        	HashSet<String> exclude = new HashSet<String>();
 	        	
 	        	displayTabs.add(generateTable(dataSet),"Table");
 	        	displayTabs.add(generateStatTable(dataSet, exclude),"Statistics");
 	        	displayTabs.add(generateColumnChart(dataSet,exclude),"Column Chart");
 	        	displayTabs.add(generateScatterChart(dataSet,exclude),"Scatter Chart");
 	        	displayTabs.add(generateLineChart(dataSet, exclude),"Line Chart");
 	        	
 	        	displayTabs.selectTab(0);
 	        	
 	        	// The most current visualization will be shown on top.
 	        	panel.insert(displayTabs, 0);
 	        }
 	    };
 	    VisualizationUtils.loadVisualizationApi(onLoadCallback,
 	    		Table.PACKAGE, ColumnChart.PACKAGE, ScatterChart.PACKAGE, LineChart.PACKAGE);
	}

	/*
	 * Generate the full table view of the DataSet
	 */
	private Widget generateTable(DataSet dataset) {
		return new Table(createTableWithStr(dataset,false),Table.Options.create());
	}
	
	/*
	 * Generate the statistic tables, the panel contains basic statistic table
	 * and the linear regression table.
	 */
	private Widget generateStatTable(DataSet dataset, Collection<String> exclude) {
		VerticalPanel statPanel = new VerticalPanel();
		statPanel.setSpacing(50);
		statPanel.add(generateBasicStatTable(dataset,exclude));
		statPanel.add(generateRegressionTable(dataset,exclude));
		
		return statPanel;
	}

	/*
	 * Generate the basic statistic table.
	 */
	private Widget generateBasicStatTable(DataSet dataset, Collection<String> exclude) {
		DataTable data = DataTable.create();
		
		Stats calculator = new Stats();
		
		// Do not need year and month for statistic.
		exclude.add("Year");
		exclude.add("Month");
		String[] numKeys = excludeKeys(dataset,exclude);
		exclude.remove("Year");
		exclude.remove("Month");
		
		data.addColumn(ColumnType.STRING, "Variable");
		data.addColumn(ColumnType.NUMBER, "Mean");
		data.addColumn(ColumnType.NUMBER, "Median");
		data.addColumn(ColumnType.NUMBER, "Mode");
		data.addColumn(ColumnType.NUMBER, "Variance");
		data.addColumn(ColumnType.NUMBER, "Standard Deviation");
		
		for (int i=0;i<numKeys.length;i++){
			int rowNum = data.addRow();
			data.setValue(rowNum, 0, numKeys[i]);
			data.setValue(rowNum, 1, calculator.mean(dataset.getNumData(numKeys[i])));
			data.setValue(rowNum, 2, calculator.median(dataset.getNumData(numKeys[i])));
			data.setValue(rowNum, 3, calculator.mode(dataset.getNumData(numKeys[i])));
			data.setValue(rowNum, 4, calculator.variance(dataset.getNumData(numKeys[i])));
			data.setValue(rowNum, 5, calculator.standardDeviation(dataset.getNumData(numKeys[i])));
		}
		
		// Wrapper that wrap up the title of table and the actual table.
		VerticalPanel wrapper = new VerticalPanel();
		
		Label title = new Label("Basic Statistic");
		Table table = new Table(data,Table.Options.create());
		table.setTitle("Basic Statistic");
		
		wrapper.add(title);
		wrapper.add(table);
		
		return wrapper;
	}
	
	/*
	 * Generate the linear regression table.
	 */
	private Widget generateRegressionTable(DataSet dataset, Collection<String> exclude) {
		
		DataTable data = DataTable.create();
		
		Stats calculator = new Stats();
		
		data.addColumn(ColumnType.STRING, "Variable");
		
		String[] keys = excludeKeys(dataset,exclude);
		
		data.addRows(keys.length);
		
		for (int i=0;i<keys.length;i++) {
			data.addColumn(ColumnType.NUMBER, keys[i]);
			data.setValue(i, 0, keys[i]);
		}
		
		for (int i=0;i<keys.length;i++){
			for (int j=0;j<keys.length;j++){
				int colNum = j + 1;
				if (i==j)
					data.setValueNull(i, colNum);
				else
					data.setValue(i, colNum, calculator.regression(dataset.getNumData(keys[i]), dataset.getNumData(keys[j])));
			}
		}
		
		// Wrapper that wrap up the title of table and the data table.
		VerticalPanel wrapper = new VerticalPanel();
		
		Label title = new Label("Linear Regression");
		Table table = new Table(data,Table.Options.create());
		table.setTitle("Linear Regression");
		
		wrapper.add(title);
		wrapper.add(table);
		
		return wrapper;
	}
	
	/*
	 * Generate Column Chart for the DataSet.
	 */
	private Widget generateColumnChart(DataSet dataSet, Collection<String> exclude) {		

		VerticalPanel displayPanel = new VerticalPanel();
		ColumnChart columnChart = new ColumnChart(createTable(dataSet,false,exclude), createOptions(dataSet.getTitle()));
		
		// check boxes that used to exclude/include data from/to chart.
		HorizontalPanel checkBoxPanel = checkBoxPanel(columnChart,exclude,false);

		displayPanel.add(checkBoxPanel);
		displayPanel.add(columnChart);
		
		return displayPanel;
	}

	/*
	 * Generate Scatter Chart for the DataSet.
	 */
	private Widget generateScatterChart(DataSet dataSet, Collection<String> exclude) {
		
		VerticalPanel displayPanel = new VerticalPanel();
		ScatterChart scatterChart = new ScatterChart(createTable(dataSet,true,exclude),createOptions(dataSet.getTitle()));
	
		// check boxes that used to exclude/include data from/to chart.
		HorizontalPanel checkBoxPanel = checkBoxPanel(scatterChart,exclude,true);
		
		displayPanel.add(checkBoxPanel);
		displayPanel.add(scatterChart);
		
		return displayPanel;
	}
	
	/*
	 * Generate Line Chart for the DataSet.
	 */
	private Widget generateLineChart(DataSet dataSet, Collection<String> exclude) {
		
		VerticalPanel displayPanel = new VerticalPanel();
		LineChart lineChart = new LineChart(createTable(dataSet,false,exclude),createOptions(dataSet.getTitle()));

		// check boxes that used to exclude/include data from/to chart.		
		HorizontalPanel checkBoxPanel = checkBoxPanel(lineChart,exclude,false);
		
		displayPanel.add(checkBoxPanel);
		displayPanel.add(lineChart);
		
		return displayPanel;
	}
	
	/*
	 * Create options for the charts with the provided title.
	 */
	private Options createOptions(String title) {
		Options options = Options.create();
		options.setWidth(WIDTH);
		options.setHeight(HEIGHT);
		options.setTitle(title);
		return options;
	}

	/*
	 * Create an AbstractDataTable with the provided DataSet,
	 * yearAsInt indicate if Year data on the table is shown as Integer or not.
	 * If yearAsInt is true, Year data is Integer, if false, Year is shown as String.
	 * Table will exclude the data in the exclude collection.
	 */
	private AbstractDataTable createTable(DataSet dataset, boolean yearAsInt, Collection<String> exclude) {
		DataTable data = DataTable.create();
		
		String[] numKeys = excludeKeys(dataset,exclude);
		
		for (int i=0;i<numKeys.length;i++){
			if (numKeys[i].equalsIgnoreCase("Year")){
				String temp = numKeys[0];
				numKeys[0] = numKeys[i];
				numKeys[i] = temp;
				break;
			}
		}
			
		int i = 0;
		if ( numKeys[i].equalsIgnoreCase("Year")&&(!yearAsInt) ){
			data.addColumn(ColumnType.STRING, numKeys[i]);
			i++;
		}

		while ( i<numKeys.length ){
			data.addColumn(ColumnType.NUMBER, numKeys[i]);
			i++;
		}
		data.addRows(dataset.getNumberOfData());

		i = 0;
		for ( Data d : dataset.getDataSet() ){
			for ( int j=0;j<numKeys.length;j++ ){
				if (numKeys[j].equalsIgnoreCase("Year")&&(!yearAsInt)){
					String year = Integer.toString(d.getNumData(numKeys[j]).intValue());
					data.setValue(i,j,year);
				}
				else
					data.setValue(i,j,d.getNumData(numKeys[j]).doubleValue());
			}
			i++;
		}
		return data;
	}

	/*
	 * Create an AbstractDataTable with also the String data,
	 * used to show the complete data table.
	 */
	private AbstractDataTable createTableWithStr(DataSet dataset, boolean yearAsInt) {

		DataTable table = (DataTable) createTable(dataset,yearAsInt,new HashSet<String>());
		int numCol = table.getNumberOfColumns();
		
		String[] strKeys = (String[]) dataset.getStrKeys().toArray(new String[0]);
		
		for ( int i=0;i<strKeys.length;i++ )
			table.addColumn(ColumnType.STRING, strKeys[i]);
		
		int i = 0;
		for ( Data d : dataset.getDataSet() ){
			for ( int j=0;j<strKeys.length;j++ ){
				table.setValue(i, j+numCol, d.getStrData(strKeys[j]));
			}
			i++;
		}
		
		return table;
	}

	/*
	 * Function used to create an array of keys not including the ones in toExclude collection.
	 */
	private String[] excludeKeys(DataSet dataset, Collection<String> toExclude) {
		
		String[] numKeys = (String[]) dataset.getnumKeys().toArray(new String[0]);

		int excludedKeys = 0;
		
		for ( int i=0;i<numKeys.length;i++ ){
			if ( toExclude.contains(numKeys[i]) ){
				numKeys[i] = null;
				excludedKeys++;
			}
		}
		
		String[] tempStr = new String[numKeys.length-excludedKeys];
		
		int j = 0;
		for (int i=0;i<numKeys.length;i++){
			if (numKeys[i]!=null){
				tempStr[j] = numKeys[i];
				j++;
			}
		}
		
		return tempStr;
	}
	
	/*
	 * Create a HorizontalPanel containing all the keys in the provided chart as check boxes,
	 * used to exclude/inlcude keys from/in the chart.
	 */
	private HorizontalPanel checkBoxPanel(final CoreChart chart, final Collection<String> exclude, final boolean yearAsInt){
		HorizontalPanel checkBoxPanel = new HorizontalPanel();
		final HashSet<String> thisExclude = new HashSet<String>();
		thisExclude.addAll(exclude);

		String[] numKeys = excludeKeys(dataSet,thisExclude);
		
		for (int i=0;i<numKeys.length;i++) {
			if ((!numKeys[i].equals("Year"))||yearAsInt){
				final CheckBox cb = new CheckBox(numKeys[i]);

				// Add a handler that update the chart when check box is clicked.
				cb.addClickHandler( new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (cb.getValue())
							thisExclude.remove(cb.getText());
						else
							thisExclude.add(cb.getText());
						chart.draw(createTable(dataSet,yearAsInt,thisExclude), createOptions(dataSet.getTitle()));
					}
				});
				cb.setValue(true);
				checkBoxPanel.add(cb);
			}
		}
		return checkBoxPanel;
	}
}
