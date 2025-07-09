package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Row 
{
	public static double[] max(double[][] a)
	{	
		final double[] rowMax = new double[a.length];
		for(int i = 0; i < a.length; i++)
			rowMax[i] = Stat.max(a[i]);
		return rowMax;
	}

	public static double[] absMax(double[][] a)
	{	
		final double[] rowMax = new double[a.length];
		for(int i = 0; i < a.length; i++)
			rowMax[i] = Stat.absMax(a[i]);
		return rowMax;
	}

	public static double[] clone(double[][] a, int n)
	{
		return a[n].clone();
	}

	public static double[] magnitudes(double[][] a)
	{
		final double[] rowMagnitude = new double[a.length];
		for(int i = 0; i < a.length; i++)
			rowMagnitude[i] = Stat.hypot(a[i]);
		return rowMagnitude;
	}

	public static double[] extreme(double[][] array, Optimization optimization)
	{	
		final double[] rowExtreme = new double[array.length];
		for(int i = 0; i < array.length; i++)
			rowExtreme[i] = Stat.extreme(array[i], optimization);
		return rowExtreme;
	}

	public static double[] min(double[][] a)
	{	
		final double[] rowMax = new double[a.length];
		for(int i = 0; i < a.length; i++)
			rowMax[i] = Stat.min(a[i]);
		return rowMax;
	}

	public static double[][] multiply(double[] a, double[][] b)
	{
		final int n = a.length;
		if(n != b.length)
			throw new IllegalArgumentException("Number of coefficients must equal number of rows.");
		double[][] c = new double[n][];
		for(int i = 0; i < n; i++)
			c[i] = Op.mult(a[i], b[i]);
		return c;
	}

	public static double[] sum(double[][] a)
	{
		final double[] rowSum = new double[a.length];
		for(int i = 0; i < a.length; i++)
			rowSum[i] = Stat.sum(a[i]);
		return rowSum;
	}
	
	public static int[] sum(int[][] a)
	{
		final int[] rowSum = new int[a.length];
		for(int i = 0; i < a.length; i++)
			rowSum[i] = Sum.sum(a[i]);
		return rowSum;
	}
	
	public static double[] apply(double[][] that, MultiVarFunc func)
	{
		final int n = that.length;
		final double[] thus = new double[n];
		for(int i = 0; i < n; i++)
			thus[i] = func.apply(that[i]);
		return thus;
	}

	public static double[][] apply(double[][][] that, MultiVarFunc func)
	{
		final int n = that.length;
		final double[][] thus = new double[n][];
		for(int i = 0; i < n; i++)
			thus[i] = apply(that[i], func);
		return thus;
	}
	
	public static double[][] append(double[][] a, double[][] b)
	{
		double[][] c = new double[a.length+b.length][];
		for(int i = 0; i < a.length; i++)
			c[i] = a[i].clone();
		for(int i = 0; i < b.length; i++)
			c[a.length+i] = b[i].clone();
		return c;
	}

	public static double[] get(int n, double[] mat, int rows, int cols)
	{
		final int size = cols;
		final double[] row = new double[size];
		int i = 0;
		int j = n*size;
		while(i < size)
			row[i++] = mat[j++];
		return row;
	}
}
