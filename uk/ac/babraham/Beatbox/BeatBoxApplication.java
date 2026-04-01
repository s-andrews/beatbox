package uk.ac.babraham.Beatbox;

import java.io.IOException;

public class BeatBoxApplication {
	
	public static String VERSION = "BeatBox v0.5";

	public static void main(String[] args) {

		BeatBoxPreferences prefs = BeatBoxPreferences.getInstance();
		prefs.parsePreferences(args);		
		
		ImageLoader loader =  new ImageLoader(prefs.inputFile());
		if (!prefs.quiet()) {
			System.err.println("Calculating periodicity for "+(loader.pixeldata().cols()*loader.pixeldata().rows())+" pixels");
		}		
		
		try {

			PeriodicityCalculator calculator = new PeriodicityCalculator(loader.pixeldata(), prefs);
			calculator.calculatePeriodicity();
			
			if (!prefs.quiet()) {
				System.err.println("Saving data to "+prefs.outputFile());
			}
		
		
			PeriodicityWriter.savePeriodicity(calculator, prefs.outputFile());
		} 
		catch (IOException e) {	
			e.printStackTrace();
		}
		
		if (prefs.debug()) {
			
		}

		
	}

}
