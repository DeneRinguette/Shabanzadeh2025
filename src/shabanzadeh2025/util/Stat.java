package shabanzadeh2025.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.ToDoubleFunction;

/**
 * @author Dene Ringuette
 */ 

public class Stat
{
	public static double sos(double... a)
	{
		double sumOfSquares = 0.0;
		for(double i : a)
			sumOfSquares += i * i;
		return sumOfSquares;
	}
	
	public static double sos(double[][] a)
	{
		double sumOfSquares = 0.0;
		for(double[] i : a)
			sumOfSquares += Stat.sos(i);
		return sumOfSquares;
	}
	
	public static int size(double[][] a)
	{
		int size = 0;
		for(double[] i : a)
			size += Stat.size(i);
		return size;
	}
	
	public static double cos2(double[] a, double[] b)
	{
		// might overflow or underflow
		double aTb = Op.dot(a, b);
		double aTa = Op.dot(a, a);
		double bTb = Op.dot(b, b);
		return (aTb / aTa) * (aTb / bTb);
	}
	
	public static boolean finite(double... a)
	{
		final double nan = Double.NaN;
		final double pos = Double.POSITIVE_INFINITY;
		final double neg = Double.NEGATIVE_INFINITY;
		for(double d : a)
			if(d == nan || d == pos || d == neg)
				return false;
		return true;
	}
	
	public static double max(double[][] that)
	{
		double max = Double.NEGATIVE_INFINITY;
		for(double[] row : that)
				max = Math.max(max, Stat.max(row));
		return max;
	}
	
	public static boolean rect(double[][] a)
	{
		for(double[] row : a)
			if(a[0].length != row.length)
				return false;
		return true;
	}
	
	public static double min(double[][] that)
	{
		double min = Double.POSITIVE_INFINITY;
		for(double[] row : that)
				min = Math.min(min, Stat.min(row));
		return min;
	}
	
	public static double min(double... a)
	{
		double min = Double.POSITIVE_INFINITY;
		for(double i : a)
			if(i < min)
				min = i;
		return min;
	}
	
	public static int size(double[] a)
	{
		return a.length;
	}
	
	public static int df(double[] a)
	{
		return a.length - 1;
	}
	
	public static double se(double[] a)
	{
		return Math.sqrt(Stat.var(a)/Stat.size(a));
	}
	
	public static double var(double... a)
	{
		return Stat.sse(a)/Stat.df(a);
	}
	
	public static double sse(double... a)
	{
		return Stat.sse(a, Stat.mean(a));
	}
	
	public static double mean(double... a)
	{
		return Stat.sum(a)/Stat.size(a);
	}
	
	public static double sum(double[] a)
	{
		double sum = 0.0;
		for(double i : a)
			sum += i;
		return sum;
	}
	
	public static double[] sums(double[][] that)
	{
		final int n = that.length;
		final double[] thus = new double[n];
		for(int i = 0; i < n; i++)
			thus[i] = Stat.sum(that[i]);
		return thus;
	}
	
	public static double sse(double[] a, double mu)
	{
		double sum = 0.0;
		for(double v : a)
			sum += Stat.pow2(v-mu);
		return sum;
	}
	
	public static double pow2(double arg)
	{
		return arg * arg;
	}
	
	public static int argMax(double[] a)
	{
		double max = Double.NEGATIVE_INFINITY;
		int argMax = -1;
		for(int i = 0; i < a.length; i++)
			if(max < a[i])
			{
				max = a[i];
				argMax = i;
			}
		return argMax;
	}
	
	public static int argMin(double[] a)
	{
		double min = Double.POSITIVE_INFINITY;
		int argMin = -1; 
		for(int i = 0; i < a.length; i++)
			if(a[i] < min)
			{
				min = a[i];
				argMin = i;
			}
		return argMin;
	}
	
	static int n(double[] a, double[] b)
	{
		final int n = a.length;
		if(n != b.length)
			throw new IllegalArgumentException("Inconsistent Length.");
		return n;
	}
	
	public static int width(double[][] a)
	{
		int width = 0;
		for(double[] row : a)
			if(width < row.length)
				width = row.length;
		return width;
	}
	
	public static int numberOfFullColumns(double[][] a)
	{
		int width = Integer.MAX_VALUE;
		for(double[] row : a)
			width = Math.min(width, row.length);
		return width;
	}
	
	public static double max(double[] a)
	{
		double max = Double.NEGATIVE_INFINITY;
		for(double i : a)
			if(max < i)
				max = i;
		return max;
	}
	
	public static double std(double[] a)
	{
		return Math.sqrt(Stat.var(a));
	}
	
	public static int width(int[][] a)
	{
		int width = 0;
		for(int[] row : a)
			if(width < row.length)
				width = row.length;
		return width;
	}
	
	public static double[] quartiles(double[] args)
	{
		final double[] sorted = Sort.quickSortAssending(args);
		final int n = sorted.length;
		final int m = n/2;
		final int eo = n%2 == 0 ? 0 : 1;
		
		final double[] bottom = Arrays.copyOfRange(sorted, 0, m);
		final double[] top = Arrays.copyOfRange(sorted, m+eo, n);
		
		final double min = sorted[0];
		final double q1 = medianOfSorted(bottom);
		final double q2 = medianOfSorted(sorted);
		final double q3 = medianOfSorted(top);
		final double max = sorted[n-1];
		
		return new double[]{min, q1, q2, q3, max};
	}
	
	private static double medianOfSorted(double[] a)
	{
		final int n = a.length;
		final int m = n/2;
		return n%2 == 1 ? a[m] : (a[m]+a[m-1])/2;
	}
	
	static int n(double[][] a, double[][] b)
	{
		final int n = a.length;
		if(n != b.length)
			throw new IllegalArgumentException("Inconsistent Length.");
		return n;
	}
	
	public static double absMax(double... a)
	{
		double abs;
		double absMax = 0.0;
		for(double i : a)
			if(absMax < (abs = Math.abs(i)))
				absMax = abs;
		return absMax;
	}
	
	public static double hypot(double... that)
	{
		double absMax = Stat.absMax(that);
		double factor = 1.0/absMax;
		double sum = 0.0;
		double term;
		for(double i : that)
		{
			term = factor * i;
			sum += term * term;
		}
		return absMax * Math.sqrt(sum);
	}
	
	public static double extreme(double[] array, Optimization optimization)
	{
		double extreme = optimization.antithetical;
		for(double value : array)
			if(optimization.fi(extreme, value))
				extreme = value;
		return extreme;
	}
	
	public static double lse(double... args)
	{
		double logLength = Math.log(args.length);
		double maxArgs = Stat.max(args);
		if(maxArgs + logLength == maxArgs)
			return maxArgs;
		else
		{
			double B = Meth.LOG_MAX - logLength - maxArgs - 1; 
			double sum = 0.0;
			for(double i : args)
				sum += Math.exp(i+B);
			return Math.log(sum)-B;
		}
	}
	
	public static double sum2(double[] a)
	{
		return Pow.two(Stat.sum(a));
	}
	
	public static double sum2(double[][] a)
	{
		return Pow.two(Sum.sum(a));
	}
	
	public static double mean(double[][][] a)
	{
		return Sum.sum(a)/Stat.size(a);
	}
	
	public static int size(double[][][] a)
	{
		int size = 0;
		for(double[][] i : a)
			size += Stat.size(i);
		return size;
	}
	
	public static double sse(double[][][] a, double mu)
	{
		double sum = 0.0;
		for(double[][] b : a)
			sum += Stat.sse(b, mu);
		return sum;
	}
	
	public static double sse(double[][] a, double mu)
	{
		double sum = 0.0;
		for(double[] b : a)
			sum += Stat.sse(b, mu);
		return sum;
	}
	
	public static double sse(double[] w, double[] a, double mu)
	{
		final int n = w.length;
		if(n != a.length)
			throw new IllegalArgumentException("Arrays not same length.");
		double sum = 0.0;
		for(int i = 0; i < n; i++)
			sum += w[i] * Stat.pow2(a[i]-mu);
		return sum;
	}
	
	public static double sse(double[][] w, double[][] a, double mu)
	{
		final int n = w.length;
		if(n != a.length)
			throw new IllegalArgumentException("Arrays not same length.");
		double sum = 0.0;
		for(int i = 0; i < n; i++)
			sum += Stat.sse(w[i], a[i], mu);
		return sum;
	}
	
	public static boolean descending(double[] args)
	{
		for(int i = 1; i < args.length; i++)
			if(args[i-1] < args[i])
				return false;
		return true;
	}
	
	public static <T> T argMin(Map<T, ? extends Number> map)
	{
		T min_key = null;
		double min = Double.POSITIVE_INFINITY;
		for(Entry<T, ? extends Number> e : map.entrySet())
		{
			final double value = e.getValue().doubleValue();
			if(value < min)
			{
				min = value;
				min_key = e.getKey();
			}
		}
		return min_key;
	}
	
	public static <K, V> K argMin(Map<K, V> map, ToDoubleFunction<V> conversion)
	{
		K min_key = null;
		double min = Double.POSITIVE_INFINITY;
		for(Entry<K, V> e : map.entrySet())
		{
			final double value = conversion.applyAsDouble(e.getValue());
			if(value < min)
			{
				min = value;
				min_key = e.getKey();
			}
		}
		return min_key;
	}
	
	public static <T> boolean indices(T[] that, int... indices)
	{
		return Stat.indices(that.length, indices);
	}
	
	public static boolean indices(int length, int... indices)
	{
		if(indices == null)
			return true; // the empty set is a subset of every set
		return 0 <= Stat.min(indices) && Stat.max(indices) < length;
	}
	
	public static int min(int[] a)
	{
		int min = Integer.MAX_VALUE;
		for(int i : a)
			if(i < min)
				min = i;
		return min;
	}
	
	public static int max(int[] a)
	{
		int max = Integer.MIN_VALUE;
		for(int i : a)
			if(max < i)
				max = i;
		return max;
	}
	
	public static boolean isFinite(double... args)
	{
		for(double arg : args)
			if(Double.isInfinite(arg) || Double.isNaN(arg))
				return false;
		return true;
	}	
}
