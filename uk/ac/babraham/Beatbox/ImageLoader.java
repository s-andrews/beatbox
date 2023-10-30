package uk.ac.babraham.Beatbox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import loci.common.DebugTools;
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
	
	public PixelMatrix pixeldata () {
		return pixeldata;
	}
	
	private void LoadImage(File file)  {
		
		try {
			// Turn off logging
			DebugTools.setRootLevel("OFF");
			
			// Read the base image
			ImageReader imageReader = new ImageReader();
			imageReader.setId(file.toString());
			System.out.println("Images "+imageReader.getImageCount());
			System.out.println("Pixeltype "+imageReader.getPixelType());
			int bytesPerPixel = FormatTools.getBytesPerPixel(imageReader.getPixelType());
			System.out.println("BytesPerPixel "+bytesPerPixel);
			System.out.println("X-Size "+imageReader.getSizeX());
			System.out.println("Y-Size "+imageReader.getSizeY());
			System.out.println("Byte length:"+imageReader.openBytes(0).length);
		
			
			pixeldata = new PixelMatrix(imageReader.getSizeX(), imageReader.getSizeY(), imageReader.getImageCount());
			
			// Populate the data
			ByteBuffer bb = ByteBuffer.allocate(2);
			if (imageReader.isLittleEndian()) {
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}
			else {
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			
			for (int frame=0;frame<imageReader.getImageCount();frame++) {
				System.err.println("Parsing frame "+frame);
				byte [] frameBytes = imageReader.openBytes(frame);
				int byte_position = 0;
				
				for (int x=0;x<imageReader.getSizeX();x++) {
					for (int y=0;y<imageReader.getSizeY();y++) {
						for (int pb=0;pb<bytesPerPixel;pb++) {
							bb.put(frameBytes[byte_position]);
							byte_position++;
						}
						
						pixeldata.addValue(y, x, frame, (int)bb.getShort(0));
						bb.clear();
					}
				}
			}

			
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
