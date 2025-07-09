package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Sum 
{	
	public static int sum(int[] args)
	{
		long total = 0L;
		for(int i : args)
			total += i;
		return (int)total;
	}
	
	public static double neumaierSum(double... values)
	{
		final int size = values.length;
	    double sum = values[0];
	    double c = 0.0; // A running compensation for lost low-order bits.
	    for(int index = 1; index < size; index++)
	    {
	    	double value = values[index];
	        double t = sum + value;
	        if(Math.abs(sum) >= Math.abs(value))
	            c += (sum - t) + value; // If sum is bigger, low-order digits of input[i] are lost.
	        else
	            c += (value - t) + sum; // Else low-order digits of sum are lost
	        sum = t;
	    }
	    return sum + c; // Correction only applied once in the very end
	}
	
	public static double sumOfAverageSumSquared(double[][] a)
	{
		double sum = 0;
		for(double[] i : a)
			sum += Stat.sum2(i)/Stat.size(i);
		return sum;
	}

	public static double sum2(double[] a)
	{
		return Pow.two(Stat.sum(a));
	}
	
	public static double sum(double[][][] a)
	{
		double sum = 0.0;
		for(double[][] i : a)
			sum += Sum.sum(i);
		return sum;
	}
	
	public static double sum(double[][] a)
	{
		double sum = 0.0;
		for(double[] i : a)
			sum += Stat.sum(i);
		return sum;
	}

	public static double sum(double[] a)
	{
		double sum = 0.0;
		for(double i : a)
			sum += i;
		return sum;
	}
}
