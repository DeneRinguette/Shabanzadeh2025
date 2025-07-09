package shabanzadeh2025.util;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class Filter 
{
	public static void bandpass(ImagePlus img, int large, int small)
	{
		IJ.run(img, "Bandpass Filter...",
				"filter_large=" + large + " " +
				"filter_small=" + small + " " +
				"suppress=None tolerance=5 process"
			);
	}
		
	public static void bandpass(ImageStack stk, int large, int small)
	{
		bandpass(new ImagePlus("", stk), large, small);
	}

	public static void bandpass(ImageProcessor pro, int large, int small)
	{
		bandpass(new ImagePlus("", pro), large, small);
	}
	
	public static void lowpass(ImagePlus img, int pix)
	{
		bandpass(img, Integer.MAX_VALUE, pix);
	}

	public static void lowpass(ImageStack stk, int pix)
	{
		lowpass(new ImagePlus("", stk), pix);
	}
	
	public static ImageProcessor lowCopy(ImageProcessor pro, int pix)
	{
		ImageProcessor copy = pro.duplicate();
		Filter.lowpass(copy, pix);
		return copy;
	}

	public static void lowpass(ImageProcessor pro, int pix)
	{
		lowpass(new ImagePlus("", pro), pix);
	}
	
	public static void highpass(ImagePlus img, int pix)
	{
		bandpass(img, pix, 0);
	}

	public static void highpass(ImageStack stk, int pix)
	{
		highpass(new ImagePlus("", stk), pix);
	}

	public static void highpass(ImageProcessor pro, int pix)
	{
		highpass(new ImagePlus("", pro), pix);
	}
}
