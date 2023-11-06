package uk.ac.babraham.Beatbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BeatBoxPreferences {

	private static BeatBoxPreferences instance = new BeatBoxPreferences();
	private File inputFile = null;
	private File outputFile = null;
	private float frameRate = 20;
	private boolean frameRateSet = false;
	private int smoothingFrames = 50;
	private boolean quiet = false;
	
	
	private BeatBoxPreferences() {}
	
	public static BeatBoxPreferences getInstance() {
		return (instance);
	}
	
	public File inputFile() {
		return inputFile;
	}
	
	public File outputFile() {
		return outputFile;
	}
	
	public float frameRate () {
		return frameRate;
	}
	
	public boolean frameRateSet () {
		return frameRateSet;
	}
	
	public int smoothingFrames() {
		return smoothingFrames;
	}
	
	public boolean quiet () {
		return quiet;
	}
	
	public void parsePreferences(String [] args) {
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("--help")) {
				showHelp();
			}
			else if (args[i].equals("--version")) {
				System.out.println(BeatBoxApplication.VERSION);
				System.exit(0);
			}
			else if (args[i].equals("--quiet")) {
				quiet=true;
			}
			else if (args[i].equals("--framerate")) {
				i++;
				frameRate = Float.parseFloat(args[i]);
				frameRateSet = true;
			}
			else if (args[i].equals("--smoothing")) {
				i++;
				smoothingFrames = Integer.parseInt(args[i]);
			}

			else if (inputFile == null) {
				inputFile = new File(args[i]);
			}
			else {
				outputFile  = new File(args[i]);
			}
		}
		
	}
	
	public void setFrameRate(float framerate) {
		this.frameRate = framerate;
		frameRateSet = true;
	}
	
	private static void showHelp() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("uk/ac/babraham/Beatbox/Help/help_text.txt")));
			
			String line;
			
			while ((line=br.readLine()) != null) {
				System.out.println(line);
			}
		
		
			br.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		System.exit(0);
		
	}
	
}
