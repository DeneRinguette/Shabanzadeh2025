package shabanzadeh2025.util;

import java.util.Comparator;

/**
 * @author Dene Ringuette
 */

public final class Indexed implements Comparable<Indexed>
{
	private final int index;
	private final double value;
	
	public int index()
	{
		return this.index;
	}
	
	public double value()
	{
		return this.value;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Indexed other = (Indexed) obj;
		if(Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		if(index != other.index)
			return false;
		return true;
	}
	
	public Indexed(int index, double value)
	{
		this.index = index;
		this.value = value;
	}

	@Override
	public int compareTo(Indexed that) 
	{	
		return Double.compare(this.value, that.value);
	}
	
	public static Comparator<Indexed> reverse()
	{
		return new Comparator<Indexed>() 
		{
			@Override
			public int compare(Indexed o1, Indexed o2) 
			{	
				return o2.compareTo(o1);
			}
			
		};
	}
	
	public String toString()
	{
		return this.index+ ":" +this.value;
	}
}
