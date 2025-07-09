package shabanzadeh2025.util;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Dene Ringuette
 */

public class Sort 
{
	public static double[] ascending(double[] unordered)
	{
		double[] sorted = unordered.clone();
		Arrays.sort(sorted);
		return sorted;
	}
	
	public static double[] quickSortAssending(double[] a)
	{
		int n = a.length;
		boolean[] split = new boolean[n];
		double pivot;
		Random r = new Random();
		double[] value = Op.copy(a);
		double[] newValue;
		int length;
		int iStart, iEnd, iTop, iBottom;
		boolean done, stretch;
		do
		{
			done = true;
			newValue = new double[n];
			length = 0;
			stretch = false;
			for(int i = 0; i < n; i++)
			{
				if(split[i])
				{
					if(stretch)
					{
						done = false;
						iEnd = i - 1;
						iStart = i - length;
						iTop = iStart;
						iBottom = iEnd;
						pivot = value[iStart + r.nextInt(length)];
						for(int j = iStart; j <= iEnd; j++)
						{
							if(value[j] < pivot)
							{
								newValue[iTop] = value[j];
								iTop++;
							}
							else
							if(pivot < value[j])
							{
								newValue[iBottom] = value[j];
								iBottom--;
							}
						}
						for(int j = iTop; j <= iBottom; j++)
						{
							split[j] = true;
							newValue[j] = pivot;
						}
						stretch = false;
						length = 0;
					}
					newValue[i] = value[i];
				}
				else
				{
					stretch = true;
					length++;
				}
			}
			if(stretch)
			{
				done = false;
				iEnd = n - 1;
				iStart = n - length;
				iTop = iStart;
				iBottom = iEnd;
				pivot = value[iStart + r.nextInt(length)];
				for(int j = iStart; j <= iEnd; j++)
				{
					if(value[j] < pivot)
					{
						newValue[iTop] = value[j];
						iTop++;
					}
					else
					if(pivot < value[j])
					{
						newValue[iBottom] = value[j];
						iBottom--;
					}
				}
				for(int j = iTop; j <= iBottom; j++)
				{
					split[j] = true;
					newValue[j] = pivot;
				}
			}
			value = newValue;
		}
		while(!done);
		return value;
	}
}
