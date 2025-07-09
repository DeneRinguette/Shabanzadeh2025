package shabanzadeh2025.util;

import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class AreaAbove 
{
	public static double[] getAll8bit(ImageProcessor pro)
	{
		final double[] perimeter = new double[256];
		for(int i = 0; i < 256; i++)
			perimeter[i] = AreaAbove.get(pro, (float)i);
		return perimeter;
	}

	public static double get(ImageProcessor pro, float threshold)
	{
		final int pixels = pro.getPixelCount();
	
		double area = 0.0;
		
		for(int pixel = 0; pixel < pixels; pixel++)
			area += pro.getf(pixel) > threshold ? 1 : 0;
		
		return area;
	}

	public static double[] getAll(ImageProcessor pro, float[] threshold)
	{
		final int n = threshold.length;
		final double[] area = new double[n];
		for(int i = 0; i < n; i++)
			area[i] = get(pro, threshold[i]);
		return area;
	}
}
