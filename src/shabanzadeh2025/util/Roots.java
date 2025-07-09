package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Roots 
{
	public static double[] of(double c, double[] value)
	{
		int count = 0;
		for(int i = 1; i < value.length; i++)
			if(value[i-1] < c)
			{
				if(value[i] > c)
					count++;
			}
			else if(value[i-1] > c)
			{
				if(value[i] < c)
					count++;
			}
			else if(value[i-1] == c)
			{
				count++;
			}
		if(value[value.length-1] == c)
			count++;
		
		final double[] roots = new double[count];
		
		count = 0;
		for(int i = 1; i < value.length; i++)
			if(value[i-1] < c)
			{
				if(value[i] > c)
				{
					double a = value[i]-c;
					double b = c-value[i-1];
					double d = value[i]-value[i-1];
					roots[count++] = a/d*(i-1) + b/d*i; 
				}
			}
			else if(value[i-1] > c)
			{
				if(value[i] < c)
				{
					double a = c-value[i];
					double b = value[i-1]-c;
					double d = value[i-1]-value[i];
					roots[count++] = a/d*(i-1) + b/d*i; 
				}
			}
			else if(value[i-1] == c)
			{
				roots[count++] = i-1;
			}
		if(value[value.length-1] == c)
			roots[count++] = value.length-1;
		
		return roots;
	}
	
	public static double firstOf(double c, double[] value)
	{
		for(int i = 1; i < value.length; i++)
			if(value[i-1] < c)
			{
				if(value[i] > c)
				{
					double a = value[i]-c;
					double b = c-value[i-1];
					double d = value[i]-value[i-1];
					return a/d*(i-1) + b/d*i; 
				}
			}
			else if(value[i-1] > c)
			{
				if(value[i] < c)
				{
					double a = c-value[i];
					double b = value[i-1]-c;
					double d = value[i-1]-value[i];
					return a/d*(i-1) + b/d*i; 
				}
			}
			else if(value[i-1] == c)
			{
				return i-1;
			}
		if(value[value.length-1] == c)
			return value.length-1;
		
		return Double.NaN;
	}
}
