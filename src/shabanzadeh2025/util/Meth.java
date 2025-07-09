package shabanzadeh2025.util;

import java.util.TreeSet;
import java.util.function.DoublePredicate;

/**
 * @author Dene Ringuette
 */

public final class Meth
{
	public static final double INSIGNIFICANCE = 1.0e-16;
	public static final double LOG_MAX = 709.782712893384;
	public static final double LN2 = 0.6931471805599453;
	
	public static double rcfc(double value)
	{
		return 0.5 / (Math.PI * value);
	}
	
	public static long[][] counts(DoublePredicate bound, double[]... groups)
	{
		long[][] counts = new long[groups.length][];
		
		for(int g = 0; g < groups.length; g++)
			counts[g] = counts(bound, groups[g]);
		
		return counts;
	}
	
	public static long[] counts(DoublePredicate bound, double... values)
	{
		long count = 0;
		for(double value : values)
			if(bound.test(value))
				count += 1L;
		
		return new long[] {values.length - count, count};
	}
	
	public static boolean isAscending(double... args)
	{
		for (int i = 1; i < args.length; i++)
			if(args[i] <= args[i - 1])
				return false;
		return true;
	}
	
	public static boolean isDescending(double... args)
	{
		for (int i = 1; i < args.length; i++)
			if(args[i - 1] <= args[i])
				return false;
		return true;
	}
	
	public static double mod2(double re, double im)
	{
		return re * re + im * im;
	}
	
	public static int[] divisors(int arg)
	{
		if(arg < 2)
			return null;
		TreeSet<Integer> divisors = new TreeSet<Integer>();
		for (int i = 2; i * i <= arg; i++)
			if(arg % i == 0)
			{
				divisors.add(i);
				divisors.add(arg / i);
			}
		return Primitives.toInt(divisors);
	}
	
	public static double poly(double arg, double... coeffs)
	{
		double sum = 0.0;
		double pow = 1.0;
		for(double coeff : coeffs)
		{
			sum += pow * coeff;
			pow *= arg;
		}
		return sum;
	}

	public static double log2(double arg)
	{
		return Math.log(arg) / Meth.LN2;
	}
}
