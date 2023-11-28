package uk.ac.babraham.Beatbox;

public class PeriodicityValues {

	public float periodicityMean;
	public float periodicityMedian;
	public float periodicityMode;
	public float meanIntensity;
	public float meanIntensityDiff;
	public float stdev;
	
	
	public PeriodicityValues(float periodicityMean, float periodicityMedian, float periodicityMode, float meanIntensity, float meanIntensityDiff, float stdev) {
		this.periodicityMean = periodicityMean;
		this.periodicityMedian = periodicityMedian;
		this.periodicityMode = periodicityMode;
		this.meanIntensity = meanIntensity;
		this.meanIntensityDiff = meanIntensityDiff;
		this.stdev = stdev;
	}
}
