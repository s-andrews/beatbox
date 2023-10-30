package uk.ac.babraham.Beatbox;

public class PixelMatrix {

	private int [][][] pixeldata;
	
	public PixelMatrix(int rows, int cols, int frames) {
		pixeldata = new int[rows][cols][frames];
	}
	
	public void addValue(int row, int col, int frame, int value) {
		pixeldata[row][col][frame] = value;
	}
	
	public int rows() {
		return (pixeldata.length);
	}
	
	public int cols() {
		return (pixeldata[0].length);
	}
	
	public int frames () {
		return (pixeldata[0][0].length);
	}
	
	public int getValue(int row,int col,int frame) {
		return (pixeldata[row][col][frame]);
	}
	
	public int [] getTimeSeries (int row, int col) {
		return (pixeldata[row][col]);
	}
}
