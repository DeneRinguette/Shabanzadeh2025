package shabanzadeh2025.util;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.FolderOpener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class Open 
{
	public static ImagePlus image(File file)
	{
		return IJ.openImage(file.getAbsolutePath());
	}
	
	public static ImageStack stack(File file)
	{
		return image(file).getStack();
	}
	
	public static ImageStack stackChannel(File file, int channel)
	{
		ImageStack color = Open.virtual(file);
		
		ImageProcessor test = color.getProcessor(1);
		if(!(test instanceof ColorProcessor))
			throw new IllegalArgumentException("Method is for color processors.");
		
		final int size = color.getSize();
		ImageStack thus = new ImageStack(color.getWidth(), color.getHeight(), size);
		for(int i = 1; i <= size; i++)
		{
			ColorProcessor cpro = (ColorProcessor)color.getProcessor(i);
			thus.setProcessor(cpro.getChannel(channel, null), i);	
		}
		return thus;
	}
	
	public static ImageStack virtual(File file)
	{	
		if(file.isDirectory())
		{
			FolderOpener opener = new FolderOpener();
			opener.openAsVirtualStack(true);
			opener.sortFileNames(true);
			return opener.openFolder(file.getAbsolutePath()).getStack();
		}
		else
			return IJ.openVirtual(file.getAbsolutePath()).getStack();
	}
}
