package uk.ac.babraham.Beatbox;

import uk.ac.babraham.Beatbox.Stats.SimpleStats;
import uk.ac.babraham.Beatbox.Utilities.FloatVector;

public class PeriodicityCalculator {

	private PixelMatrix pixeldata;
	
	private int smoothingWindow = BeatBoxPreferences.getInstance().smoothingFrames();
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

		// Get the overall intensity mean for this pixel
		float meanIntensity = 0;
		for (int i=0;i<values.length;i++) {
			meanIntensity += values[i];
		}
		meanIntensity /= values.length;
		
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
						
		float mean = SimpleStats.mean(crossTimes.toArray())*2;
		float median = SimpleStats.median(crossTimes.toArray())*2;
		float mode = SimpleStats.mode(crossTimes.toArray())*2;

		float stdev = SimpleStats.stdev(crossTimes.toArray())*2;

		// Our values are in frames, and we need them in Hz.  To do this we need the
		// framerate (in frames per second)
		float frameRate = BeatBoxPreferences.getInstance().frameRate();
		
		// The frame duration (in seconds) is 1/frameRate
		
		float frameDuration = 1/frameRate;
		
		
		// The conversion from the number of frames per cycle to Hz is 1 / (frames * duration)
		
		float meanHz = 1 / (mean*frameDuration);
		float medianHz = 1 / (median*frameDuration);
		float modeHz = 1 / (mode*frameDuration);
		
		float stdevHz = 1 / (stdev*frameDuration);
				
		return new PeriodicityValues(meanHz, medianHz, modeHz, meanIntensity, absMean, stdevHz);
	}
	
	
	public PeriodicityValues getPeriodicityValues(int row, int col) {
		return periodicityValues[row][col];
	}
}
