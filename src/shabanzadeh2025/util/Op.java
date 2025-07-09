package shabanzadeh2025.util;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;

/**
 * Operators for arrays.
 * @author Dene Ringuette
 */ 

public final class Op
{
	public static double[] soln(int revCols, double[] rev, int invCols, double[] inv) throws IllegalArgumentException
	{
		final int rows0 = rev.length/revCols;	
		final double[] recDiag = new double[rows0];
		final int w = invCols;
		for(int s = 0; s < w; s++)
		{
			double maxAbs = Math.abs(rev[w*s+s]);
			int maxIndex = s;
			for(int i = s + 1; i < w; i++)
			{
				double abs = Math.abs(rev[w*i+s]);
				if(maxAbs < abs)
				{
					maxAbs = abs;
					maxIndex = i;
				}
			}
			if(maxAbs == 0.0)
				return null;
			if(maxIndex != s)
			{
				Ip.diagSwap(revCols, rev, s, maxIndex);
				Ip.rowSwap(invCols, inv, s, maxIndex);
			}
			recDiag[s] = 1.0 / rev[w*s+s];
			for(int i = s+1; i < w; i++)
				if(rev[w*i+s] != 0.0)
				{
					double fact = - rev[w*i+s] / rev[w*s+s];
					for(int j = s+1; j < w; j++)
						rev[w*i+j] += fact * rev[w*s+j];
					for(int j = 0; j < w; j++)
						inv[w*i+j] += fact * inv[w*s+j];
				}
		}
		for(int d = rows0 - 1; 0 <= d; d--)
			for(int i = 0; i < d; i++)
				if(rev[w*i+d] != 0.0)
				{
					double fact = - recDiag[d] * rev[w*i+d];
					for(int j = 0; j < rows0; j++)
						inv[w*i+j] += fact * inv[w*d+j];
				}
		Ip.multRows(invCols, inv, recDiag);
		return inv;
	}
	
	public static double[][] transpose(double[][] that)
	{
		final int n = that.length;
		final int m = that[0].length;
		
		for(double[] row : that)
			if(m != row.length)
				throw new IllegalArgumentException("Can only transpose a rectangular matrix.");
			
				
		double[][] thus = new double[m][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				thus[j][i] = that[i][j];
		return thus;
	}
	
	public static double[][] subset(double[][] a, int... b)
	{
		// all values of int[] must be in range
		double[][] c = new double[b.length][];
		for(int i = 0; i < c.length; i++)
			c[i] = a[b[i]];
		return c;	
	}
	
	public static double[] sum(double[] a, double[] b)
	{
		double[] c = new double[Math.min(a.length, b.length)];
		for(int i = 0; i < c.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}
	
	public static double[] diff(double[] a, double[] b)
	{
		double[] c = new double[Math.min(a.length, b.length)];
		for(int i = 0; i < c.length; i++)
			c[i] = a[i] - b[i];
		return c;
	}
	
	public static double[][] sum(double[][] a, double[][] b)
	{
		double[][] c = new double[Math.min(a.length, b.length)][];
		for(int i = 0; i < c.length; i++)
			c[i] = new double[Math.min(a[i].length, b[i].length)];
		for(int i = 0; i < c.length; i++)
			for(int j = 0; j < c[i].length; j++)
				c[i][j] = a[i][j] + b[i][j];
		return c;
	}
	
	public static double[][] div(double[][] a, double b)
	{
		double[][] c = new double[a.length][];
		for(int i = 0; i < c.length; i++)
			c[i] = Op.div(a[i], b);
		return c;
	}
	
	public static double[] div(double[] a, double b)
	{
		double[] c = new double[a.length];
		for(int i = 0; i < c.length; i++)
			c[i] = a[i] / b;
		return c;
	}
	
	public static double[][] mult(double a, double[][] b)
	{
		double[][] c = new double[b.length][];
		for(int i = 0; i < b.length; i++)
			c[i] = Op.mult(a, b[i]);
		return c;
	}
	
	public static double[] mult(double a, double[] b)
	{
		final double[] c = new double[b.length];
		for(int i = 0; i < b.length; i++)
			c[i] = a * b[i];
		return c;
	}
	
	public static double[] compDiv(double[] a, double[] b)
	{
		double[] c = new double[Stat.n(a, b)];
		for(int i = 0; i < c.length; i++)
			c[i] = a[i] / b[i];
		return c;
	}
	
	public static double[] rollingMean(double[] that, int bin)
	{
		final double[] thus = new double[that.length-bin+1];
		double sum = 0;
		for(int i = 0; i < bin; i++)
			sum += that[i];
		thus[0] = sum/bin;
		for(int i = bin; i < that.length; i++)
		{
			final int j = i-bin;
			sum -= that[j];
			sum += that[i];
			thus[j+1] = sum/bin;
		}	
		return thus;	
	}
	
	public static double[] downsample(double[] t0, double[] v0, double[] t1)
	{
		final int m = t0.length;
		if(v0.length != m)
			throw new IllegalArgumentException("array length mismatch");
		final int n = t1.length;
		final double[] v1 = new double[n];
		final int[] s1 = new int[n];
		
		TreeMap<Double, Integer> quick = new TreeMap<Double, Integer>();
		for(int i = 0; i < n; i++)
			quick.put(t1[i], i);
		
		for(int j = 0; j < m; j++)
		{	
			Map.Entry<Double, Integer> e0 = quick.floorEntry(t0[j]);
			Map.Entry<Double, Integer> e1 = quick.ceilingEntry(t0[j]);
			int i = -1;
			if(e0 != null && e1 != null)
			{	
				double d0 = t0[j] - e0.getKey();
				double d1 = e1.getKey() - t0[j];
				i = d0 < d1 ? e0.getValue() : e1.getValue();
			}
			else if(e0 == null && e1 != null)
			{
				i = e1.getValue();
			}
			else if(e0 != null && e1 == null)
			{
				i = e0.getValue();
			}
			
			v1[i] += v0[j];
			s1[i] += 1;
		}
		
		for(int i = 0; i < n; i++)
			if(s1[i] != 0)
				v1[i] /= s1[i];
		
		for(int i = 1; i < n-1; i++)
		{
			if(s1[i] == 0)
			{
				v1[i] = 0.5*(v1[i-1] + v1[i+1]);
			}
				
		}
		return v1;
	}
	
	public static double[] decimate(double[] data, int fact)
	{
		final double[] decimated = 
				new double[(data.length / fact) + ((data.length % fact == 0) ? 0 : 1)];
		
		int i = 0;
		int j = 0;
		while(i < decimated.length)
		{
			decimated[i] = data[j];
			i += 1;
			j += fact;
		}
		return decimated;
	}
	
	public static double[][] minor(int i1, int i2, int j1, int j2, double[][] a)
	{
		double[][] c = new double[i2-i1][j2-j1];
		for(int i = i1; i < i2; i++)
			for(int j = j1; j < j2; j++)
				c[i-i1][j-j1] = a[i][j];
		return c;
	}
	
	public static String flip(String a)
	{
		int n = a.length();
		StringBuffer b = new StringBuffer();
		for(int i = 0; i < n; i++)
			b.append(a.charAt(n-1-i));
		return b.toString();
	}
	
	public static double[] append(double[]... arrays)
	{
		int n = 0;
		for(double[] array : arrays)
			n += array.length;
		final double[] c = new double[n];
		n = 0;
		for(double[] array : arrays)
			for(double value : array)
				c[n++] = value;
		return c;
	}
	
	public static double[][] append(double[][]... arrays)
	{
		int n = 0;
		for(double[][] array : arrays)
			if(array != null)
				n += array.length;
		final double[][] c = new double[n][];
		n = 0;
		for(double[][] array : arrays)
			if(array != null)
				for(double[] value : array)
					c[n++] = value;
		return c;
	}
	
	public static String[] append(String[] a, String b)
	{
		String[] c = new String[a.length+1];
		for(int i = 0; i < a.length; i++)
			c[i] = a[i];
		c[a.length] = b;
		return c;
	}
	
	public static String[] append(String[]... arrays)
	{
		int n = 0;
		for(String[] array : arrays)
			n += array.length;
		final String[] c = new String[n];
		n = 0;
		for(String[] array : arrays)
			for(String value : array)
				c[n++] = value;
		return c;
	}
	
	public static double[][] to2D(double[] vec, int w)
	{
		final int n = vec.length;
		if(n%w != 0)
			throw new IllegalArgumentException();
		final int height = n/w;
		double[][] matrix = new double[height][w];
		for(int i = 0; i < n; i++)
			matrix[i/w][i%w] = vec[i];
		return matrix;
	}
	
	public static double[] subtract(double[] a, double b)
	{
		return Op.add(a, -b);
	}
	
	public static double[] add(double[] a, double b)
	{
		double[] c = Op.copy(a);
		Ip.add(c, b);
		return c;
	}
	
	public static double[] copy(double[] a)
	{
		final int n = a.length;
		double[] b = new double[n];
		for(int i = 0; i < n; i++)
			b[i] = a[i];
		return b;
	}
	
	public static int count(double[] args, DoublePredicate cond)
	{
		int count = 0;
		for(double arg : args)
			if(cond.test(arg))
				count += 1;	
		return count;
	}
	
	public static double[] redact(double[] args, DoublePredicate cond)
	{
		final int removed = count(args, cond);
		final double[] thus = new double[args.length-removed];
		int offset = 0;
		for(int i = 0; i < args.length; i++)
			if(cond.test(args[i]))
				offset += 1;
			else
				thus[i-offset] = args[i];
		return thus;
	}
	
	public static double[][] redact(double[][] args, DoublePredicate cond)
	{
		final double[][] thus = new double[args.length][];
		for(int i = 0; i < thus.length; i++)
			thus[i] = redact(args[i], cond);
		return thus;
	}
	
	public static double[] flip(double[] a)
	{
		int n = a.length;
		double[] b = new double[n];
		for(int i = 0; i < n; i++)
			b[i] = a[n-1-i];
		return b;
	}
	
	public static double[] evens(double[] that)
	{
		return Mod.get(that, 0, 2);
	}
	
	public static double[] odds(double[] that)
	{
		return Mod.get(that, 1, 2);
	}
		
	public static double[] apply(double[] that, DoubleUnaryOperator func)
	{
		double[] thus = Op.str(that);
		for(int i = 0; i < Stat.size(that); i++)
			thus[i] = func.applyAsDouble(that[i]);
		return thus;
	}
	
	public static double[] str(double[] a)
	{
		return new double[a.length];
	}
	
	public static double[] dwt(double[] that)
	{
		return Ip.dwt(Op.copy(that));
	}
	
	public static double dot(double[] a, double[] b)
	{
		double dot = 0.0;
		final int n = Stat.n(a, b);
		for(int i = 0; i < n; i++)
			dot += a[i] * b[i];
		return dot;
	}
	
	public static double[][] remove(double[][] that, int... indices)
	{
		if(indices == null)
			return Op.copy(that);
		
		if(!Stat.indices(that, indices))
			throw new IllegalArgumentException("cannot remove array index outside range: [0 , " + that.length + ")");
		
		final int n = that.length-indices.length;
		final double[][] sub = new double[n][];
		final int[] k = Op.copy(indices);
		Arrays.sort(k);
		
		int i = 0;
		int j = 0;
		int ii = 0;
		while(ii < n)
			if(j < k.length && k[j] == i)
			{
				i++;
				j++;
			}
			else
				sub[ii++] = that[i++];
		return sub;
	}
	
	public static double[][] copy(double[][] a)
	{
		double[][] b = new double[a.length][];
		for(int i = 0; i < a.length; i++)
			b[i] = Op.copy(a[i]);
		return b;
	}
	
	public static int[] copy(int[] a)
	{
		final int n = a.length;
		int[] b = new int[n];
		for(int i = 0; i < n; i++)
			b[i] = a[i];
		return b;
	}
	
	public static double[] neg(double[] a)
	{
		double[] b = new double[a.length];
		for(int i = 0; i < a.length; i++)
			b[i] = - a[i];
		return b;
	}
	
	public static double[] removeHead(int a, double[] vector)
	{
		final int n = vector.length-a;
		double[] sub = new double[n];
		for(int i = 0; i < n; i++)
			sub[i] = vector[i+a];
		return sub;
	}
	
	public static double[] removeTail(int a, double[] vector)
	{
		final int n = vector.length-a;
		double[] sub = new double[n];
		for(int i = 0; i < n; i++)
			sub[i] = vector[i];
		return sub;
	}
	
	public static double[] sq(double[] a)
	{
		final int n = a.length;
		double[] b = new double[n];
		for(int i = 0; i < n; i++)
			b[i] = a[i] * a[i];
		return b;
	}
}