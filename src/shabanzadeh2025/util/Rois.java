package shabanzadeh2025.util;


import java.util.TreeMap;

import ij.gui.Overlay;
import ij.gui.Roi;

/**
 * @author Dene Ringuette
 */

public class Rois 
{
	@SuppressWarnings("unchecked")
	public static <T extends Roi> TreeMap<Integer, T> byPosition(Overlay overlay, Class<T> c)
	{
		TreeMap<Integer, T> map = new TreeMap<Integer, T>();		
		for(Roi roi : overlay.toArray())
			if(c.isInstance(roi))
				map.put(roi.getPosition(), (T)roi);
		return map;
	}	
}
