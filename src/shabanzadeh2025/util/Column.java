package shabanzadeh2025.util;

import java.lang.reflect.Array;

/**
 * @author Dene Ringuette
 */

public class Column
{
	public static double[] apply(double[][] that, MultiVarFunc func)
	{
		return Row.apply(Op.transpose(that), func);
	}
	
	public static double[] sum(double[][] a)
	{
		double[] columnSum = new double[Stat.width(a)];
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[i].length; j++)
				columnSum[j] += a[i][j];
		return columnSum;
	}
	
	public static double[] min(double[][] a)
	{	
		double[] columnMax = Gen.constant(Double.POSITIVE_INFINITY, Stat.width(a));
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[i].length; j++)
				if(columnMax[j] > a[i][j])
					columnMax[j] = a[i][j];
		return columnMax;
	}
	
	public static double[] extreme(double[][] array, Optimization optimization)
	{	
		double[] columnExtreme = Gen.constant(optimization.antithetical, Stat.width(array));
		for(int i = 0; i < array.length; i++)
			for(int j = 0; j < array[i].length; j++)
				if(optimization.fi(columnExtreme[j], array[i][j]))
					columnExtreme[j] = array[i][j];
		return columnExtreme;
	}
	
	public static double[] mean(double[][] a)
	{
		double[] meanSum = sum(a);
		Ip.div(meanSum, a.length);
		return meanSum;
	}

	public static double[] var(double[][] a)
	{
		double[] mean = mean(a);
		double[] colVar = new double[Stat.width(a)];
		for(double[] row : a)
			for(int j = 0; j < row.length; j++)
				colVar[j] += Pow.two(row[j]-mean[j]);
		Ip.div(colVar, a.length);
		return colVar;
	}

	public static double[] max(double[][] a)
	{	
		double[] columnMax = Gen.constant(Double.NEGATIVE_INFINITY, Stat.width(a));
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[i].length; j++)
				if(columnMax[j] < a[i][j])
					columnMax[j] = a[i][j];
		return columnMax;
	}
	
	public static double[] absMax(double[][] a)
	{	
		double[] columnMax = Gen.constant(0, Stat.width(a));
		double abs;
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[i].length; j++)
			{
				abs = Math.abs(a[i][j]);
				if(columnMax[j] < abs)
					columnMax[j] = abs;
			}
		return columnMax;
	}
	
	public static double[] get(double[][] a, int n)
	{
		double[] b = new double[a.length];
		if(0 <= n)
			for(int i = 0; i < a.length; i++)
				if(n < a[i].length)
					b[i] = a[i][n];
		return b;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] get(T[][] data, int column)
	{
		T[] values = //Arrays.copyOf(a[0], a.length);
				(T[])Array.newInstance(data[0][0].getClass(), data.length);
		for(int row = 0; row < data.length; row++)
			values[row] = data[row][column];
		return values;
	}
	
	public static double[] toDoubles(String[][] a, int n)
	{
		double[] b = new double[a.length];
		if(0 <= n)
			for(int i = 0; i < a.length; i++)
				if(n < a[i].length)
					b[i] = Double.parseDouble(a[i][n]);
		return b;
	}
	
	public static double[][] append(double[][] a, double[][] b)
	{
		int n = Math.min(a.length, b.length);
		double[][] c = new double[n][];
		for(int i = 0; i < n; i++)
			c[i] = Op.append(a[i], b[i]);
		return c;	
	}
	
	public static int[] sum(int[][] args)
	{
		int n = Stat.width(args);
		int[] thus = new int[n];
		long sum;
		for(int j = 0; j < n; j++)
		{	
			sum = 0L;
			for(int i = 0; i < args.length; i++)
			{
				if(j < args[i].length)
					sum += args[i][j];
			}
			thus[j] = (int)sum;
		}
		return thus;
	}

	public static double[] get(int n, double[] mat, int rows, int cols)
	{
		final int size = rows;
		double[] col = new double[size];
		final int step = cols;
		int i = 0;
		int j = n;
		while(i < size)
		{
			col[i++] = mat[j];
			j += step;
		}
		return col;
	}
	
	public static double[][] remove(double[][] that, int column)
	{
		double[][] thus = new double[that.length][];
		for(int i = 0; i < that.length; i++)
		{
			thus[i] = new double[that[i].length-1];
			for(int j = 0; j < column; j++)
				thus[i][j] = that[i][j];
			for(int j = column; j < thus[i].length ; j++)
				thus[i][j] = that[i][j+1];
		}
		return thus;
	}
}
