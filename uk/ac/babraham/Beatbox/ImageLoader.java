package uk.ac.babraham.Beatbox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import loci.formats.FormatTools;
import loci.formats.ImageReader;


public class ImageLoader {
	
	private PixelMatrix pixeldata;

	public ImageLoader(File input) {
		if (input.isDirectory()) {
			LoadTextFiles(input);
		}
		else {
			LoadImage(input);
		}
		
	}
	
	private void LoadImage(File file)  {
		
		try {
			ImageReader imageReader = new ImageReader();
			imageReader.setId(file.toString());
			System.out.println("Images "+imageReader.getImageCount());
			System.out.println("Pixeltype "+imageReader.getPixelType());
			System.out.println("BytesPerPixel "+FormatTools.getBytesPerPixel(imageReader.getPixelType()));
			
			
			imageReader.close();
		}
		catch (Exception ioe) {
			ioe.printStackTrace();
		}
		
		
	}
		
	private void LoadTextFiles (File baseFolder) {
		File [] files = baseFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.toString().contains("ROI") & pathname.toString().endsWith(".txt")) {
					return true;
				}
				return false;
			}
		});
		

		ArrayImage [] arrayImages = new ArrayImage[files.length];
		
		for (int i=0;i<files.length;i++) {
			arrayImages[i] = new ArrayImage(files[i]);
		}
		
		Arrays.sort(arrayImages);
		
		for (int i=0;i<arrayImages.length;i++) {
			System.out.println(arrayImages[i].frame()+" "+arrayImages[i].file().getName());
		}
		
		int [] dimensions = new int[2];
		try {
			dimensions = arrayImages[0].getDimensions();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		
		pixeldata = new PixelMatrix(dimensions[0], dimensions[1], arrayImages.length);

		try {
			for (int i=0;i<arrayImages.length;i++) {
				System.out.println("Processing "+arrayImages[i].file().getName());
				arrayImages[i].populatePixelData(pixeldata);
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		
		
	}
	
	
	public static void main(String [] args) {
//		new ImageLoader(new File("E:/Illumina Analysis/Stephen Cranwell/Periodicity Analysis/Data Parsing Test/text_image_analysis_test/"));
		new ImageLoader(new File("E:/Illumina Analysis/Stephen Cranwell/Periodicity Analysis/Data Parsing Test/C4_ROI2_IF.nd2"));
	}
}
