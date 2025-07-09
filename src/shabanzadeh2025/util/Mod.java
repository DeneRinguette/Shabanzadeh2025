package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Mod 
{
	public static double[] get(double[] original, int first, int increment)
	{
		int n = original.length - first;
		int whole_periods = n / increment;
		int partial_end = n % increment == 0 ? 0 : 1;
		
		double[] partial = new double[whole_periods + partial_end];
		for(int i = 0; i < partial.length; i += 1)
			partial[i] = original[first + i * increment];
		return partial;
	}	
}
