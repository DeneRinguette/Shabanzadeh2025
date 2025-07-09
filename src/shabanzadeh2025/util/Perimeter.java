package shabanzadeh2025.util;

import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class Perimeter 
{
	public static double[] getAll8bit(ImageProcessor pro)
	{
		final double[] perimeter = new double[256];
		for(int i = 0; i < 256; i++)
			perimeter[i] = Perimeter.get(pro, (float)i);
		return perimeter;
	}
	
	public static double[] getAll16bit(ImageProcessor pro)
	{
		final double[] perimeter = new double[65536];
		for(int i = 0; i < 65536; i++)
			perimeter[i] = Perimeter.get(pro, (float)i);
		return perimeter;
	}

	
	public static double[] getAll(ImageProcessor pro, float[] threshold)
	{
		final int n = threshold.length;
		final double[] perimeter = new double[n];
		for(int i = 0; i < n; i++)
			perimeter[i] = Perimeter.get(pro, threshold[i]);
		return perimeter;
	}
	
	public static double get(ImageProcessor pro, float threshold)
	{
		final int w = pro.getWidth();
		final int h = pro.getHeight();
		final int p = w-1;
		final int q = h-1;
	
		double perimeter = 0.0;
		
		for(int y = 0; y < q; y++)
			for(int x = 0; x < p; x++)
			{
				int shift = 0;
				for(int b = 0; b < 2; b++)
					for(int a = 0; a < 2; a++)
						shift += pro.getf(x+a, y+b) > threshold ? +1 : -1;
				if(Math.abs(shift) < 4)
					perimeter += 1.0;
			}
		
		for(int y : new int[]{0, q})
			for(int x = 0; x < p; x++)
			{
				int shift = 0;
				for(int a = 0; a < 2; a++)
					shift += pro.getf(x+a, y) > threshold ? +1 : -1;
				if(Math.abs(shift) < 2)
					perimeter += 0.5;
			}
		
		for(int x : new int[]{0, p})
			for(int y = 0; y < q; y++)
			{
				int shift = 0;
				for(int b = 0; b < 2; b++)
					shift += pro.getf(x, y+b) > threshold ? +1 : -1;
				if(Math.abs(shift) < 2)
					perimeter += 0.5;
			}
		return perimeter;
	}
}