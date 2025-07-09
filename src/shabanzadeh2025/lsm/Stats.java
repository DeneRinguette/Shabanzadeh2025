package shabanzadeh2025.lsm;

import java.io.File;

import ij.ImageStack;
import shabanzadeh2025.util.Format;
import shabanzadeh2025.util.IJTools;
import shabanzadeh2025.util.IStat;
import shabanzadeh2025.util.ImageFilter;
import shabanzadeh2025.util.Open;
import shabanzadeh2025.util.Syo;

/**
 * Computes lateral leakage statistics based on data segmentation.
 * 
 * @author Dene Ringuette
 */

public class Stats 
{
	
	public static void compute(File... group)
	{	
		for(File dir : group)
			run(dir);
	}
	
	private static void run(File dir)
	{
		File mask_dir = new File(dir, "mask-edit");
		for(File tif : dir.listFiles(ImageFilter.TIFF))
		{
			ImageStack data = Open.stack(tif);
			data = IJTools.toFloat(data);
			IJTools.apply(data, Math::log);
			ImageStack stroke = Open.stackChannel(new File(mask_dir, tif.getName()), 1);
			ImageStack contra = Open.stackChannel(new File(mask_dir, tif.getName()), 3);
			
			Syo.pl( 
					Format.decimals(IStat.maskMean(data, stroke, 128)[1]-IStat.maskMean(data, contra, 128)[1], 3)
					+ "\t" + tif.getAbsolutePath() 
				);
		}
	}

}
