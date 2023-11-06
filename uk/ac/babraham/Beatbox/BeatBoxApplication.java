package uk.ac.babraham.Beatbox;

import java.io.IOException;

public class BeatBoxApplication {
	
	public static String VERSION = "BeatBox v0.3";

	public static void main(String[] args) {

		BeatBoxPreferences prefs = BeatBoxPreferences.getInstance();
		prefs.parsePreferences(args);		
		
		ImageLoader loader =  new ImageLoader(prefs.inputFile());
		if (!prefs.quiet()) {
			System.err.println("Calculating periodicity for "+(loader.pixeldata().cols()*loader.pixeldata().rows())+" pixels");
		}
		PeriodicityCalculator calculator = new PeriodicityCalculator(loader.pixeldata());
		calculator.calculatePeriodicity();
		
		if (!prefs.quiet()) {
			System.err.println("Saving data to "+prefs.outputFile());
		}
		
		
		try {
			PeriodicityWriter.savePeriodicity(calculator, prefs.outputFile());
		} 
		catch (IOException e) {	
			e.printStackTrace();
		}

		
	}

}
