package shabanzadeh2025.lsm;

import java.io.File;

import ij.ImageStack;
import shabanzadeh2025.util.ColorTools;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Histogram;
import shabanzadeh2025.util.IJTools;
import shabanzadeh2025.util.ImageFilter;
import shabanzadeh2025.util.IntegerHistogram;
import shabanzadeh2025.util.Open;
import shabanzadeh2025.util.Roots;
import shabanzadeh2025.util.Save;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;

/**
 * Utility for initializing segmentation masks.
 * 
 * @author Dene Ringuette
 */

public class Masks 
{
	private static void mask(File mask_dir, File tif)
	{
		Syo.p(tif.getName() + " ");
		ImageStack data = Open.stack(tif);
		Histogram histo = IntegerHistogram.uint16();
		histo.count(data);
		double[] pdf = histo.pdf();
		int low = Stat.argMax(pdf);
		double[] cdf = histo.cdf();
		int high = (int)Roots.of(0.99, cdf)[0];
		
		Syo.pl(low + " - "+ high);
		ImageStack base = IJTools.toByte(low, high, data);
		ImageStack mask = ColorTools.merge(null, base, null, null, 1);
		Save.tiff(mask_dir, Extension.remove(tif.getName()), mask);
	}
	
	public static void masks(File dir)
	{	
		File mask_dir = new File(dir, "mask");
		for(File tif : dir.listFiles(ImageFilter.TIFF))
			mask(mask_dir, tif);
	}
}
