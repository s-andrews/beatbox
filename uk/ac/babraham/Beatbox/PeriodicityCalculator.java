package uk.ac.babraham.Beatbox;

import uk.ac.babraham.Beatbox.Stats.SimpleStats;
import uk.ac.babraham.Beatbox.Utilities.FloatVector;

public class PeriodicityCalculator {

	private PixelMatrix pixeldata;
	
	private int smoothingWindow = 50;
	private PeriodicityValues[][] periodicityValues;
	
	
	public PeriodicityCalculator(PixelMatrix pixeldata) {
		this.pixeldata = pixeldata;
	}

	public int rows() {
		return (periodicityValues.length);
	}
	
	public int cols() {
		return (periodicityValues[0].length);
	}
	
	public PeriodicityValues getValues(int row, int col) {
		return (periodicityValues[row][col]);
	}
	
	
	public void calculatePeriodicity() {
		
		periodicityValues = new PeriodicityValues[pixeldata.rows()][pixeldata.cols()];
		
		// We're going to calculate the periodicity for each pixel
		int count = 0;
		for (int row=0;row<pixeldata.rows();row++) {
			for (int col=0;col<pixeldata.cols();col++) {
				count++;
				if (count % 100000 == 0) {
					if (!BeatBoxPreferences.getInstance().quiet()) {
						System.err.println("Calculated "+count+" pixels");
					}
				}
				periodicityValues[row][col] = calculatePeriodicity(pixeldata.getTimeSeries(row, col));
			}
		}
		
	}
	
	
	private PeriodicityValues calculatePeriodicity(int [] values) {
		
		// We start by calculating a running smoothed version of the data based on the smoothing window
		// and subtracting that from the real data.  Effectively centering the data around zero.
		
		int halfWindow = smoothingWindow/2;
//		System.out.println("Half window is "+halfWindow);
		
		float [] smoothedDiff = new float[values.length-smoothingWindow];
		
		float runningValue = 0;
		
		for (int i=0;i<smoothingWindow;i++) {
			runningValue += (float)values[i];
		}
		
		for (int i=0;i<smoothedDiff.length;i++) {
			smoothedDiff[i] = (float)values[i+halfWindow] - (runningValue/smoothingWindow);
//			System.out.println("i "+i+" Value "+values[i+halfWindow]+" Running "+(runningValue/smoothingWindow)+" Smoothed "+smoothedDiff[i]);
			runningValue -= values[i];
			runningValue += values[smoothingWindow+i];
		}
		
		// Add the last one
		smoothedDiff[smoothedDiff.length-1] = values[smoothingWindow+halfWindow] - (runningValue/smoothingWindow);
		
		// Now we go through the data finding the crossing points.
		boolean aboveMean = smoothedDiff[0] > 0;
		int lastCross = 0;
		
		FloatVector crossTimes = new FloatVector();

		
		float absMean = 0;
		
		for (int i=0;i<smoothedDiff.length;i++) {
			
			if (smoothedDiff[i]<0) {
				absMean -= smoothedDiff[i];
			}
			else {
				absMean += smoothedDiff[i];
			}
			
			boolean thisAboveMean = smoothedDiff[i] > 0;
			
			if (aboveMean != thisAboveMean) {
				// We've crossed.
				if (lastCross != 0) {
					// This is not the first cross
					crossTimes.add((float)(i-lastCross));
//					System.out.println("Crossed at "+(i-lastCross));
				}

				lastCross = i;
				aboveMean = thisAboveMean;
			}
		}
		
		
		absMean /= smoothedDiff.length;
		
		// We need to find the median and stdev of the crossing times.

		// For the crossing time we double the measured value to get a complete cycle
		// and we multiply by the frame time to get it in the units they specified.
		float median = SimpleStats.median(crossTimes.toArray())*2*BeatBoxPreferences.getInstance().frameTime();
		
		// We also put the stdev into the same units.
		float stdev = SimpleStats.stdev(crossTimes.toArray())*2*BeatBoxPreferences.getInstance().frameTime();
		
		
		
		return new PeriodicityValues(median, absMean, stdev);
	}
	
	
	public PeriodicityValues getPeriodicityValues(int row, int col) {
		return periodicityValues[row][col];
	}
}
