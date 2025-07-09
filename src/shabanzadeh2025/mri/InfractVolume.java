package shabanzadeh2025.mri;

import java.awt.Point;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * Computes infarct volume from overlays.
 * 
 * @author Dene Ringuette
 */

public class InfractVolume 
{
	
	public static int make(ImagePlus image)
	{
		Overlay over = image.getOverlay();
		
		int points = 0;
		if(over != null)
			for(Roi roi : over.toArray())
			{
				for(@SuppressWarnings("unused") Point point : roi)
					points += 1;
			}
		return points;
	}

}
