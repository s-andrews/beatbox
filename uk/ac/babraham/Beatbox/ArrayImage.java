package uk.ac.babraham.Beatbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ArrayImage implements Comparable<ArrayImage>{

	private int frame;
	private File file;
	
	public ArrayImage(File file) {
		this.file = file;
		
		String [] sections = file.getName().split("_");
		String last = sections[sections.length-1];
		last = last.replaceAll(".txt", "");
		frame = Integer.parseInt(last);
	}
	
	public int frame() {
		return (frame);
	}
	
	public File file() {
		return(file);
	}
	
	public int compareTo (ArrayImage image2) {
		return (this.frame - image2.frame);
	}

	
	public int[] getDimensions() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line;
		
		int rows = 0;
		int cols = 0;
		while ((line = br.readLine()) != null) {
			if (cols==0) {
				cols = line.split("\t").length;
			}
			rows++;
		}
		
		br.close();
		
		return (new int[]{rows,cols});
	}
	
	
	public void populatePixelData (PixelMatrix pm) throws IOException {
		
		int row = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line;
		
		while((line=br.readLine()) != null) {
			String [] sections = line.split("\t");
			for (int col=0;col<sections.length;col++) {
				pm.addValue(row, col, frame-1, Integer.parseInt(sections[col]) );
			}
			row++;
		}
		
		br.close();
	}
	


}
