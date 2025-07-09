package shabanzadeh2025.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class Pixel implements Comparable<Pixel>
{
	public final int x, y;
	public float value;
	
	public Pixel(int arg0, int arg1, float val)
	{
		this.x = arg0;
		this.y = arg1;
		this.value = val;
	}
	
	public Pixel(int arg0, int arg1)
	{
		this(arg0, arg1, Float.NaN);
	}
	
	public Pixel(double arg0, double arg1, float val)
	{
		this.x = (int)(arg0+0.5);
		this.y = (int)(arg1+0.5);
		this.value = val;
	}
		
	@Override
	public int hashCode() 
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.x;
		result = PRIME * result + this.y;
		return result;
	}
	
	public String toString()
	{
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pixel other = (Pixel) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
	
	public static Comparator<Pixel> priorityDirection(final double a, final double b)
	{
		return new Comparator<Pixel>()
		{
			@Override
			public int compare(Pixel o1, Pixel o2)
			{
				return Double.compare(a*o1.x + b*o1.y, a*o2.x + b*o2.y);
			}	
		};
	}
	
	public static Comparator<Pixel> priorityValue()
	{
		return new Comparator<Pixel>()
		{
			@Override
			public int compare(Pixel o1, Pixel o2)
			{
				return Float.compare(o1.value, o2.value);
			}	
		};
	}
	
	@Override
	public int compareTo(Pixel that) 
	{
		if(this.y < that.y)
			return -1;
		if(that.y < this.y)
			return 1;
		if(this.x < that.x)
			return -1;
		if(that.x < this.x)
			return 1;
		return 0;
	}
	
	public Voxel toVoxel(final int z)
	{
		return new Voxel(this.x, this.y, z, this.value);
	}
	
	public boolean inside(ImageProcessor pro)
	{
		return 
				0 <= this.y && this.y < pro.getHeight() && 
				0 <= this.x && this.x < pro.getWidth(); 
	}
	
	public static ArrayList<Pixel> toList(ImageProcessor pro, Roi roi)
	{
		ArrayList<Pixel> list = new ArrayList<Pixel>(pro.getPixelCount());
		Iterator<Point> iter = roi.iterator();
		while(iter.hasNext())
		{
			Point p = iter.next();
			list.add(new Pixel(p.x, p.y, pro.getf(p.x, p.y)));
		}
		return list;
	}
	
	public static ArrayList<Pixel> toList(ImageProcessor pro)
	{
		ArrayList<Pixel> list = new ArrayList<Pixel>(pro.getPixelCount());
		final int w = pro.getWidth();
		final int h = pro.getHeight();
		
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				list.add(new Pixel(x, y, pro.getf(x, y)));

		return list;
	}
	
	public static void sortValue(List<Pixel> list)
	{
		Collections.sort(list, Pixel.priorityValue());
	}
	
	public static void delta(List<Pixel> list)
	{
		float last = 0f;
		for(Pixel pixel : list)
		{
			float current = pixel.value;
			pixel.value -= last;
			last = current;
		}			
	}
}
