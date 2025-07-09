package shabanzadeh2025.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Dene Ringuette
 */

public enum ImageFilter implements FileFilter
{
	TIFF("tif"),
	BMP("bmp"),
	PNG("png"),
	GIF("gif"),
	JPEG("jpg"),
	CZI("czi");

	@Override
	public boolean accept(File file) 
	{
		return file.getName().endsWith(this.extension);
	}
	
	private final String extension;
	
	private ImageFilter(String ext)
	{
		this.extension = "." + ext;
	}
}
