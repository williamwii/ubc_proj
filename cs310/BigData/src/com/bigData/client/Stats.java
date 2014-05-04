package com.bigData.client;


import java.util.Collection;


public class Stats {
	private final int NONE = 0;
	private final int ROUNDING = 100;
	private final double INVALIDNUM = -0.0;
	
	private Double[] createArray(Collection<Double> list)
	{
		Double[] myArray;
		myArray = (Double[]) list.toArray(new Double[0]);
		return myArray;
	}
	private Double[] orderArray(Double[] data)
	{
		Double newValue;
		int index;
		
		for(int i = 1; i < data.length; i++)
		{
			newValue = data[i];
			index = i;
			while(index > 0 && data[index-1] > newValue)
			{
				data[index] = data[index - 1];
				index--;
			}
			data[index] = newValue;
		}
		
		return data;
	}
	
	public Double mean(Collection<Double> list)
	{
		Double number = new Double(0);
		Double sum = new Double(0);
		Double avg =  new Double(0);
		Double[] data = createArray(list);
		for(int i = 0; i < data.length; i++)
		{
			//check to ensure value isn't null
			if(data[i] != null)
			{
				sum = sum + data[i];
				number++;
			}
		}
		avg = (double)Math.round(sum/number*ROUNDING)/ROUNDING;
		return avg;
	}
	public Double median(Collection<Double> list)
	{
		double median = 0;
		int middleIndex = 0;
		int lowerIndex = 0;
		int higherIndex = 0;

		Double[] data = createArray(list);
		data = orderArray(data);
		
		if((data.length)%2 == 0)
		{
			lowerIndex = ((data.length)-2)/2;
			higherIndex = (data.length)/2;
			median = (data[lowerIndex] + data[higherIndex])/2;
		}
		else
		{
			middleIndex = ((data.length)-1)/2;
			median = data[middleIndex];
		}
		median = (double)Math.round(median*ROUNDING)/ROUNDING;
		return median;
	}

	public Double mode(Collection<Double> list)
	{
		double mode = 0;
		double mostCommonOccurences = 0;
		double numOccurences = 1;
		
		Double[] data = createArray(list);
		data = orderArray(data);
		mode = data[0];
		for(int i = 1; i < data.length; i++)
		{
			int prevIndex = i - 1;
			if(data[i].compareTo(data[prevIndex]) == NONE)
			{
				numOccurences++;
				if(numOccurences > mostCommonOccurences)
				{
					mode = data[prevIndex];
					mostCommonOccurences = numOccurences;
				}
			}
			else
			{
				numOccurences = 1;
			}
		}
		//if all occur equally then last number is returned
		if(mostCommonOccurences == 0)
		{
			mode = data[data.length - 1];
		}
		return mode;
	}
	public Double variance(Collection<Double> list)
	{
		double variance = 0;
		double avg = 0;
		double sum = 0;
		double num = 0;
		
		avg = mean(list);
		Double[] data = createArray(list);

		for(int i = 0; i < data.length; i++)
		{
			//check to ensure value isn't null
			if(data[i] != null)
			{
				double value = data[i]-avg;
				value = value*value;
				sum = sum + value;
				num++;
			}
		}
		variance = (double)Math.round(sum/(num-1)*ROUNDING)/ROUNDING;
		return variance;
	}
	public Double standardDeviation(Collection<Double> list)
	{
		double sd = 0;
		double var = 0;
		var = variance(list);
		sd = (double)Math.round(Math.sqrt(var)*ROUNDING)/ROUNDING;
		return sd;
	}
	//Advanced Statistics
	public Double regression(Collection<Double> listOne, Collection<Double> listTwo)
	{
		double regressionCoef = 0;
		Double[] dataOne = createArray(listOne);
		Double[] dataTwo = createArray(listTwo);
		Double meanOne = mean(listOne);
		Double meanTwo = mean(listTwo);
		
		double numerator = 0;
		double denominator = 0;
		for(int i = 0; i < dataOne.length; i++)
		{
			numerator = numerator + ((dataOne[i]-meanOne) * (dataTwo[i]-meanTwo));
		}
		double xDiff = 0;
		double yDiff = 0;
		for(int j = 0; j < dataOne.length; j++)
		{
			xDiff = xDiff + ((dataOne[j]-meanOne)*(dataOne[j]-meanOne));
			yDiff = yDiff + ((dataTwo[j]-meanTwo)*(dataTwo[j]-meanTwo));
		}
		denominator = xDiff * yDiff;
		denominator = Math.sqrt(denominator);
		if(denominator == 0)
		{
			//displays a -0 so viewer knows that regression value incapable of being determined
			return INVALIDNUM;
		}
		regressionCoef = (double)Math.round((numerator/denominator)*ROUNDING)/ROUNDING;
		return regressionCoef;
	}
	
	public Double rSquare(Collection<Double> listOne, Collection<Double> listTwo)
	{
		double rSquareVal = 0;
		Double regVal = regression(listOne, listTwo);
		rSquareVal = (double)Math.round((regVal*regVal)*ROUNDING)/ROUNDING;
		return rSquareVal;
	}	
}
