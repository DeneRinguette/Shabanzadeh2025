package shabanzadeh2025.util;

import java.util.Comparator;

import ij.ImageStack;

/**
 * @author Dene Ringuette
 */

public class Voxel implements Comparable<Voxel>
{
	public final int x, y, z;
	public float value;
	
	public Voxel(int x, int y, int z)
	{
		this(x, y, z, Float.NaN);
	}
	
	public Voxel(int x, int y, int z, float v)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.value = v;
	}
	
	public Voxel(double x, double y, double z)
	{
		this(x, y, z, Float.NaN);
	}
	
	public Voxel(double x, double y, double z, float v)
	{
		this((int)(x+0.5), (int)(y+0.5), (int)(z+0.5), v);
	}
		
	@Override
	public int hashCode() 
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.x;
		result = PRIME * result + this.y;
		result = PRIME * result + this.z;
		return result;
	}
	
	public String toString()
	{
		return "[" + this.x + ", " + this.y + ", " + this.z + "] = " + this.value;
	}
	
	public Voxel translate(int x, int y, int z)
	{
		return new Voxel(this.x + x, this.y + y, this.z + z, this.value);
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final Voxel that = (Voxel) obj;
		if (this.x != that.x)
			return false;
		if (this.y != that.y)
			return false;
		if (this.z != that.z)
			return false;
		return true;
	}
	
	public static Comparator<Voxel> priority(final double a, final double b, final double c)
	{
		return new Comparator<Voxel>()
		{
			@Override
			public int compare(Voxel o1, Voxel o2)
			{
				final double d1 = a*o1.x+b*o1.y+c*o1.z;
				final double d2 = a*o2.x+b*o2.y+c*o2.z; 
				if(d1 < d2)
					return -1;
				if(d2 < d1)
					return 1;
				return 0;
			}	
		};
	}
	
	public void insert(ImageStack stk)
	{
		stk.getProcessor(this.z).setf(this.x, this.y, this.value);
	}
	
	@Override
	public int compareTo(Voxel that) 
	{
		if(this.z < that.z)
			return -1;
		if(that.z < this.z)
			return 1;
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
	
	public boolean inside(ImageStack stk)
	{
		return 
				1 <= this.z && this.z <= stk.getSize() && 
				0 <= this.y && this.y < stk.getHeight() && 
				0 <= this.x && this.x < stk.getWidth(); 
	}
}