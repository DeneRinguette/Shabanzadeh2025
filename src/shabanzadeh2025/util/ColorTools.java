package shabanzadeh2025.util;

import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class ColorTools 
{
	public static ImageStack merge(ImageStack r, ImageStack g, ImageStack b, ImageStack a, int labels)
	{
		int w, h, n;
		
		if(r != null)
		{
			w = r.getWidth();
			h = r.getHeight();
			n = r.getSize();
		}
		else if(g != null)
		{
			w = g.getWidth();
			h = g.getHeight();
			n = g.getSize();
		}
		else if(b != null)
		{
			w = b.getWidth();
			h = b.getHeight();
			n = b.getSize();
		}
		else
		{
			w = a.getWidth();
			h = a.getHeight();
			n = a.getSize();
		}	
			
		ImageStack thus = new ImageStack(w, h, n);
		
		ImageStack l;
		if(labels == 0)
			l = r;
		else if(labels == 1)
			l = g;
		else if(labels == 2)
			l = b;
		else
			l = a;
		
		for(int i = 1; i <= n; i++)
		{
			thus.setSliceLabel(l.getSliceLabel(i), i);
			thus.setProcessor(
					merge(
							(r == null) ? null : r.getProcessor(i), 
							(g == null) ? null : g.getProcessor(i), 
							(b == null) ? null : b.getProcessor(i), 
							(a == null) ? null : a.getProcessor(i)), 
					i);
		}
		return thus;
	}
	
	public static ColorProcessor merge(ImageProcessor r, ImageProcessor g, ImageProcessor b, ImageProcessor a)
	{
		ByteProcessor r_ = (r == null) ? null : IJTools.toByte(r);
		ByteProcessor g_ = (g == null) ? null : IJTools.toByte(g);
		ByteProcessor b_ = (b == null) ? null : IJTools.toByte(b);
		ByteProcessor a_ = (a == null) ? null : IJTools.toByte(a);
		return ColorTools.merge(r_, g_, b_, a_);
	}
}
