package shabanzadeh2025.util;

import java.util.List;

/**
 * @author Dene Ringuette
 */

public class Numbers 
{
	public static double[] toDouble(List<? extends Number> args)
	{
		double[] copy = new double[args.size()];
		int i = 0;
		for(Number value : args)
			copy[i++] = value.doubleValue();
		return copy;
	}
}
