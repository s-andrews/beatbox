package uk.ac.babraham.Beatbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PeriodicityWriter {

	
	public static void savePeriodicity(PeriodicityCalculator periodicity, File outfile) throws IOException {
		
		BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
		
		String [] headers = new String[] {"X","Y","Periodicity","Signal","StDev"};
		
		out.write(String.join("\t", headers));
		out.write("\n");
		
		for (int x=0; x<periodicity.cols();x++) {
			for (int y=0; y<periodicity.rows();y++) {
				PeriodicityValues pv = periodicity.getPeriodicityValues(y, x);
				
				out.write(Integer.toString(x));
				out.write("\t");

				out.write(Integer.toString(y));
				out.write("\t");
				
				out.write(Float.toString(pv.periodicity));
				out.write("\t");

				out.write(Float.toString(pv.meanIntensityDiff));
				out.write("\t");

				out.write(Float.toString(pv.stdev));
				
				out.write("\n");

			}
		}
		
		out.close();
	}
	
	
}
