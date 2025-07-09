package shabanzadeh2025.util;

import java.awt.Rectangle;

import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public abstract class Scatter implements Moments 
{
	public abstract double[] moments(double x0, double y0);
	
	public abstract void add(double w, double x, double y);
	
	public abstract void sub(double w, double x, double y);
	
	public abstract void add(double x, double y);
	
	public abstract void sub(double x, double y);
	
	public void add(ImageProcessor pro) 
	{
		final int w = pro.getWidth();
		final int h = pro.getHeight();
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				this.add(pro.getf(x, y), x, y);
	}
	
	public void add(ImageProcessor pro, Roi roi)
	{
		final int width = pro.getWidth();
		final int height = pro.getHeight();
		
		if(roi == null)
			roi = new Roi(0, 0, width, height);
		
		Rectangle bound = roi.getBounds();
		
		final int x0 = Math.max(0, bound.x);
		final int y0 = Math.max(0, bound.y);
		final int x1 = Math.min(width, bound.x + bound.width);
		final int y1 = Math.min(height, bound.y + bound.height);
		
		for(int y = y0; y < y1; y++)
			for(int x = x0; x < x1; x++)
				if(roi.contains(x, y))
					this.add(pro.getf(x, y), x, y);
	}
	
	public void add(ImageProcessor pro, GaplessPath roi)
	{
		do
		{
			int x = roi.x();
			int y = roi.y();
			this.add(pro.getf(x,y), x, y);
		}
		while(roi.next());
	}
	
	public void sub(ImageProcessor pro, GaplessPath roi)
	{
		do
		{
			int x = roi.x();
			int y = roi.y();
			this.add(-pro.getf(x,y), x, y);
		}
		while(roi.next());
	}
}