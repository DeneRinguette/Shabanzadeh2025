package shabanzadeh2025.util;

import java.io.File;

/**
 * @author Dene Ringuette
 */

public class Extension 
{
	public static String get(String arg)
	{
		int stop = arg.lastIndexOf('.');
		if(stop < 0)
			return null;
		return arg.substring(stop + 1, arg.length());
	}
	
	public static String get(File arg)
	{
		return Extension.get(arg.getName());
	}
	
	public static String remove(String arg)
	{
		int stop = arg.lastIndexOf('.');
		if(stop < 0)
			return arg;
		return arg.substring(0, stop);
	}
	
	public static String change(String arg, String ext)
	{
		return remove(arg) + "." + ext;
	}
	
	public static File change(File file, String ext)
	{
		if(file.isDirectory())
			throw new IllegalArgumentException("Extension change is only for files not directories.");
		return new File(file.getParent(), change(file.getName(), ext));
	}
	
	public static File remove(File file)
	{
		if(file.isDirectory())
			throw new IllegalArgumentException("Extension change is only for files not directories.");
		return new File(file.getParent(), remove(file.getName()));
	}
}
