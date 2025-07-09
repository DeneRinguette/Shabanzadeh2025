package shabanzadeh2025.util;

import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class IStat 
{
	public static double[] maskMean(ImageStack data, ImageStack mask, double threshold)
	{
		IJTools.checkDim(data, mask);
		final int stack_size = data.getSize();
		final int pixels_per_slice = data.getWidth() * data.getHeight();
		double[] means = new double[2];
		int[] counts = new int[2];
		for(int i = 1; i <= stack_size; i++)
		{
			ImageProcessor data_slice = data.getProcessor(i);
			ImageProcessor mask_slice = mask.getProcessor(i);
			for(int k = 0; k < pixels_per_slice; k++)
			{
				final int j = (mask_slice.getf(k) > threshold) ? 1 : 0;
				means[j] += data_slice.getf(k);
				counts[j] += 1;
			}
		}
		means[0] /= counts[0];
		means[1] /= counts[1];
		return means;
	}
	
	public static float min(ImageProcessor ip)
	{
		float min = Float.POSITIVE_INFINITY;
		int n = ip.getWidth() * ip.getHeight();
		for(int i = 0 ; i < n; i++)
			min = Math.min(min, ip.getf(i));
		return min;
	}
	
	public static float max(ImageProcessor ip)
	{
		float max = Float.NEGATIVE_INFINITY;
		int n = ip.getWidth() * ip.getHeight();
		for(int i = 0 ; i < n; i++)
			max = Math.max(max, ip.getf(i));
		return max;
	}
	
	public static float min(ImageStack stk)
	{
		final int size = stk.getSize();
		float min = Float.POSITIVE_INFINITY;
		for(int i = 1 ; i <= size; i++)
			min = Math.min(min, min(stk.getProcessor(i)));
		return min;
	}
	
	public static float max(ImageStack stk)
	{
		final int size = stk.getSize();
		float max = Float.NEGATIVE_INFINITY;
		for(int i = 1 ; i <= size; i++)
			max = Math.max(max, max(stk.getProcessor(i)));
		return max;
	}

	public static boolean indices(int length, int... indices)
	{
		if(indices == null)
			return true; // the empty set is a subset of every set
		return 0 <= Stat.min(indices) && Stat.max(indices) < length;
	}
	
	public static int min(int[] a)
	{
		int min = Integer.MAX_VALUE;
		for(int i : a)
			if(i < min)
				min = i;
		return min;
	}
	
	public static int max(int[] a)
	{
		int max = Integer.MIN_VALUE;
		for(int i : a)
			if(max < i)
				max = i;
		return max;
	}
}
