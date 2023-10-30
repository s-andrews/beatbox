package uk.ac.babraham.Beatbox;

import java.util.Vector;

public class PeridocityCalculator {

	private PixelMatrix pixeldata;
	
	private int smoothingWindow = 50;
	private PeriodicityValues[][] periodicityValues;
	
	
	public PeridocityCalculator(PixelMatrix pixeldata) {
		this.pixeldata = pixeldata;
	}
	
	public void calculatePeriodicity() {
		
		periodicityValues = new PeriodicityValues[pixeldata.rows()][pixeldata.cols()];
		
		// We're going to calculate the periodicity for each pixel
		for (int row=0;row<pixeldata.rows();row++) {
			for (int col=0;col<pixeldata.cols();col++) {
				periodicityValues[row][col] = calculatePeriodicity(pixeldata.getTimeSeries(row, col));
			}
		}
		
	}
	
	
	private PeriodicityValues calculatePeriodicity(int [] values) {
		
		// We start by calculating a running smoothed version of the data based on the smoothing window
		// and subtracting that from the real data.  Effecively centering the data around zero.
		
		int halfWindow = smoothingWindow/2;
		
		float [] smoothedDiff = new float[values.length-smoothingWindow];
		
		float runningValue = 0;
		
		for (int i=0;i<smoothingWindow;i++) {
			runningValue += (float)values[i];
		}
		
		for (int i=0;i<smoothedDiff.length;i++) {
			smoothedDiff[i] = values[smoothingWindow+halfWindow] - (runningValue/smoothingWindow);
			runningValue =- values[i];
			runningValue += values[smoothingWindow+i];
		}
		
		// Add the last one
		smoothedDiff[smoothedDiff.length-1] = values[smoothingWindow+halfWindow] - (runningValue/smoothingWindow);
		
		// Now we go through the data finding the crossing points.
		boolean aboveMean = smoothedDiff[0] > 0;
		int lastCross = 0;
		
		Vector<Float>crossTimes = new Vector<Float>();
		
		for (int i=0;i<smoothedDiff.length;i++) {
			boolean thisAboveMean = smoothedDiff[i] > 0;
			
			if (aboveMean != thisAboveMean) {
				// We've crossed.
				if (lastCross != 0) {
					// This is not the first cross
					crossTimes.add((float)(i-lastCross));
				}
				// This is the first cross
				lastCross = i;
				aboveMean = thisAboveMean;
			}
		}
		
		// We need to find the mean and stdev of the crossing times.
		
		
		return null;
	}
	
	
	public PeriodicityValues getPeriodicityValues(int row, int col) {
		return periodicityValues[row][col];
	}
}
