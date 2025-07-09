package shabanzadeh2025.util;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

/**
 * @author Dene Ringuette
 */

public class Save 
{
	public static void tiff(File tiff, ImagePlus img)
	{
		IJ.saveAsTiff(img, tiff.getAbsolutePath());
	}
	
	public static void tiff(File dir, String name, ImageStack stk)
	{
		if(name == null)
			name = "name";
		if(!dir.isDirectory())
			throw new IllegalArgumentException("File must specify destination directory not file.");
		tiff(new File(dir, name + ".tif"), new ImagePlus(name, stk));
	}
}
