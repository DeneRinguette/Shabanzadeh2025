package shabanzadeh2025.util;

import java.io.File;

/**
 * @author Dene Ringuette
 */

public class Split
{
	public static void channels(final int no_channels, final File dir)
	{
		if(!dir.isDirectory())
			throw new IllegalArgumentException("Directory not file.");
		
		final File[] tiff_files = dir.listFiles(ImageFilter.TIFF);
		
		final int no_tiffs = tiff_files.length;
		
		if(no_tiffs % no_channels != 0)
			throw new IllegalArgumentException("Must be even number of tiffs.");
		
		final int tiffs_per_channel = no_tiffs / no_channels;
		
		for(int channel = 0; channel < no_channels; channel++)
		{
			File channel_dir = new File(dir, "c"+channel);
			channel_dir.mkdir();
			final int start = tiffs_per_channel * channel;
			final int end = start + tiffs_per_channel;
			for(int index = start; index < end; index++)
				tiff_files[index].renameTo(new File(channel_dir, tiff_files[index].getName()));
		}
	}
}
