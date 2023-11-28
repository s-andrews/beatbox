package uk.ac.babraham.Beatbox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

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
			// Turn off bioformats logging
			DebugTools.setRootLevel("OFF");
			
			// Read the base image
			ImageReader imageReader = new ImageReader();
			imageReader.setId(file.toString());
			
			// Set the frame rate from the image if it's not set already
			if (!BeatBoxPreferences.getInstance().frameRateSet()) {
				
				// We have to work the framerate out from the timestamps on the
				// frames.  We're going to assume that all frames are equally spaced.
				// The timestamp metadata appears to look like "timestamp #001". But
				// it might be that the number of digits in the end is different in
				// different images.  The timestamps are in a hash so do not appear
				// in order.
				
				// We'll get the full list of timestamps, and then sort them.
				Hashtable<String,Object>metadata = imageReader.getSeriesMetadata();
				
				Enumeration<String> en = metadata.keys();

				ArrayList<String> timestamps = new ArrayList<String>();
				
				while (en.hasMoreElements()) {
					String key = en.nextElement();
					if (key.startsWith("timestamp")) {
						timestamps.add(key);
					}
				}

				
				// Sort the timestamp text.
				Collections.sort(timestamps);
				
				// Now get the first two values out and calculate the difference
				double ts1 = (Double)(metadata.get(timestamps.get(0)));
				double ts2 = (Double)(metadata.get(timestamps.get(1)));
				double diff = ts2-ts1;
				float fps = 1f/(float)diff;
				
				if (!BeatBoxPreferences.getInstance().quiet()) {
					System.err.println("Calculated FPS of "+fps+" from "+timestamps.get(0)+" and "+timestamps.get(1));
				}
				
				BeatBoxPreferences.getInstance().setFrameRate(fps);
			}

		
			
			int bytesPerPixel = FormatTools.getBytesPerPixel(imageReader.getPixelType());

			if (!BeatBoxPreferences.getInstance().quiet()) {
				System.err.println("Loading "+file.getName());
				System.err.println("Images "+imageReader.getImageCount());
				System.err.println("BytesPerPixel "+bytesPerPixel);
				System.err.println("X-Size "+imageReader.getSizeX());
				System.err.println("Y-Size "+imageReader.getSizeY());
				System.err.println("Byte length:"+imageReader.openBytes(0).length);
			}
		
			
			pixeldata = new PixelMatrix(imageReader.getSizeY(), imageReader.getSizeX(), imageReader.getImageCount());
			
			// Populate the data
			ByteBuffer bb = ByteBuffer.allocate(2);
			if (imageReader.isLittleEndian()) {
				bb.order(ByteOrder.LITTLE_ENDIAN);
			}
			else {
				bb.order(ByteOrder.BIG_ENDIAN);
			}
			
			for (int frame=0;frame<imageReader.getImageCount();frame++) {
				if (!BeatBoxPreferences.getInstance().quiet()) {
					if (frame % 100 == 0) {
						System.err.println("Parsing frame "+frame);
					}
				}
				byte [] frameBytes = imageReader.openBytes(frame);
				int byte_position = 0;
				
				for (int y=0;y<imageReader.getSizeY();y++) {
					for (int x=0;x<imageReader.getSizeX();x++) {
						for (int pb=0;pb<bytesPerPixel;pb++) {
							bb.put(frameBytes[byte_position]);
							byte_position++;
						}
						
						// The values coming from the image are unsigned shorts.  Since
						// java doesn't support these we need to convert them to ints
						// and not allowing them to be interpreted as negative values.
						// 
						// This solution came from https://stackoverflow.com/questions/9883472/is-it-possible-to-have-an-unsigned-bytebuffer-in-java
						pixeldata.addValue(y, x, frame, bb.getShort(0) & 0xffff);
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
	
}
