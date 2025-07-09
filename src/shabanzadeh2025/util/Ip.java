package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

/**
 * In-place array operations.
 * @author Dene Ringuette
 */

public class Ip
{
	public static double det(int n, double[] mat)
	{
		if(mat.length/n != n)
			throw new IllegalArgumentException();
		double det = 1.0;
		
		for(int d = 0; d < n; d++)
		{
			// determine if row switch need for numerical stability
			double abs;
			double maxAbs = Math.abs(mat[d*n+d]);
			int maxIdx = d;
			
			for(int i = d+1; i < n; i += 1)
				if(maxAbs < (abs = Math.abs(mat[i*n+d])))
				{
					maxAbs = abs;
					maxIdx = i;
				}
			
			if(maxAbs == 0.0)
				return 0.0; 
			
			if(maxIdx != d)
			{
				Ip.diagSwap(n, mat, d, maxIdx);
				det *= - 1.0;
			}
			
			det *= mat[d*n+d];
			
			// row reduce
			for(int i = d+1; i < n; i += 1)
			{
				final int r = i * n;
				double fact = mat[r+d] / mat[d*n+d];
				for(int j = d+1; j < n; j++)
					mat[r+j] -= fact * mat[d*n+j];
			}
		}
		return det;	
	}
	
	public static void diagSwap(int cols, double[] mat, int d, int s)
	{
		Ip.rowSwap(cols, mat, d, s, d);
	}
	
	public static void rowSwap(int cols, double[] mat, int i0, int i1)
	{
		Ip.rowSwap(cols, mat, i0, i1, 0);
	}
	
	public static void rowSwap(int cols, double[] mat, int i0, int i1, int offset)
	{
		final int r0 = i0 * cols;
		final int r1 = i1 * cols;
		for(int j = offset; j < cols; j++)
		{
			final double t = mat[r0+j];
			mat[r0+j] = mat[r1+j];
			mat[r1+j] = t;
		}
	}
	
	public static void add(double[][] that, double[][] plus)
	{
		final int n = Stat.n(that, plus);
		for(int i = 0; i < n; i++)
			Ip.add(that[i], plus[i]);
	}
	
	public static void add(double[] that, double plus)
	{
		for(int i = 0; i < that.length; i++)
			that[i] += plus;
	}
	
	public static void add(double[] that, double[] plus)
	{
		final int n = Stat.n(that, plus);
		for(int i = 0; i < n; i++)
			that[i] += plus[i];
	}
	
	public static void compDiv(double[] a, double[] b)
	{
		final int n = Stat.n(a, b);
		for(int i = 0; i < n; i++)
			a[i] /= b[i];
	}
	
	public static void mult(double[] that, double factor)
	{
		final int n = that.length;
		for(int i = 0; i < n; i++)
			that[i] *= factor;
	}
	
	public static void mult(double[][] that, double factor)
	{
		for(double[] i : that)
			Ip.mult(i, factor);
	}
	
	public static double[] div(double[] that, double divisor)
	{
		final int n = that.length;
		final double c = 1.0/divisor;
		for(int i = 0; i < n; i++)
			that[i] *= c;
		return that;
	}
	
	public static void div(double[][] that, double divisor)
	{
		double c = 1.0/divisor;
		for(double[] i : that)
			Ip.mult(i, c);
	}
	
	public static void sub(double[] that, double plus)
	{
		for(int i = 0; i < that.length; i++)
			that[i] -= plus;
	}
	
	public static void cumulative(double[] density)
	{
		final int n = density.length;
		for(int i = 1; i < n; i++)
			density[i] += density[i-1];
	}
	
	public static void decumulative(double[] density)
	{
		final int n = density.length;
		for(int i = n-1; 0 < i; i--)
			density[i-1] += density[i];
	}
	
	public static void apply(double[][] that, DoubleUnaryOperator func)
	{
		for(double[] i : that)
			Ip.apply(i, func);	
	}
	
	public static void apply(double[] that, DoubleUnaryOperator func)
	{
		for(int i = 0; i < that.length; i++)
			that[i] = func.applyAsDouble(that[i]);	
	}
	
	public static double[] dwt(double[] that)
	{
		final int n = that.length;
		double[] output = new double[n];
	    for(int length = n >> 1; ; length >>= 1) 
	    {
	        for(int i = 0; i < length; i++) 
	        {
	            output[i] = that[i*2] + that[i*2+1];
	            output[length+i] = that[i*2] - that[i*2+1];
	        }
	        if (length == 1) 
	            return output;
	        System.arraycopy(output, 0, that, 0, length<<1);
	    }
	}
	
    public static void fft(double[] x, double[] y)
    {
    	final int n = x.length;
    	if(n != y.length)
    		throw new RuntimeException("Real and Imaginary arrays are of different length");;
        if (Integer.highestOneBit(n) != n) 
            throw new RuntimeException("N is not a power of 2");
        final int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) 
        {
            final int j = Integer.reverse(k) >>> shift;
            if (j > k) 
            {
                Ip.swap(x, j, k);
                Ip.swap(y, j, k);
            }
        }
        final double negTwoPI = - 2 * Math.PI;
        for (int L = 2; L <= n; L = L+L)
        {
        	final double fact = negTwoPI/L;
        	final int Lo2 = L/2;
            for (int k = 0; k < Lo2; k++) 
            {
                final double kth = fact * k;
                final double wx = Math.cos(kth);
                final double wy = Math.sin(kth);
                for (int jL = 0; jL < n; jL += L) 
                {
                	final int b = jL + k;
                	final int a = b + Lo2;
                    final double tx = wx * x[a] - wy * y[a];
                    final double ty = wx * y[a] + wy * x[a];	
                    x[a] = x[b] - tx; 
                    y[a] = y[b] - ty;
                    x[b] += tx; 
                    y[b] += ty; 
                }
            }
        }
    }
    
	public static void swap(double[] that, int i, int j)
	{
		double temp = that[i];
		that[i] = that[j];
		that[j] = temp;
	}
	
    public static void ifft(double[] x, double[] y) 
    {
    	final int n = x.length;
    	if(n != y.length)
    		throw new RuntimeException("Real and Imaginary arrays are of different length");
        Ip.neg(y);
        Ip.fft(x, y);
        Ip.neg(y);
        Ip.div(x, n);
        Ip.div(y, n);       
    }
    
	public static void neg(double[] that)
	{
		for(int i = 0; i < that.length; i++)
			that[i] = - that[i];
	}
	
	public static void normMax(double[] that)
	{
		Ip.div(that, Stat.absMax(that));
	}
		
	public static void normHyp(double[] that)
	{
		Ip.div(that, Stat.hypot(that));
	}
	
	public static void colSwap(int cols, double[] mat, int c0, int c1)
	{
		final int size = mat.length;
		for(int r = 0; r < size; r += cols)
		{
			final double t = mat[r+c0];
			mat[r+c0] = mat[r+c1];
			mat[r+c1] = t;
		}
	}
	
	public static void zero(double[] that)
	{
		for(int i = 0; i < that.length; i++)
			that[i] = 0;
	}
	
	public static void compMult(double[] a, double[] b)
	{
		final int n = Stat.n(a, b);
		for(int i = 0; i < n; i++)
			a[i] *= b[i];
	}
	
	public static void interflip(int cols, double[] mat)
	{
		final int rows = mat.length/cols;
		for(int i = 0; i < rows; i++)
		{
			final int r = i * cols;
			for(int j = (i % 2 == 0) ? 1 : 0; j < cols; j += 2)
			{
				final int index = r + j;
				mat[index] = -mat[index];
			}
		}
	}
	
	public static void normSum(double[] that)
	{
		Ip.div(that, Stat.sum(that));
	}
	
	public static void softMax(double[] that)
	{
		double lse = Stat.lse(that);
		for(int i = 0; i < that.length; i++)
			that[i] = Math.exp(that[i]-lse);
	}
	
	public static void sub(double[] that, double[] minus)
	{
		final int n = Stat.n(that, minus);
		for(int i = 0; i < n; i++)
			that[i] -= minus[i];
	}
	
	public static void multRows(int cols, double[] mat, double[] that)
	{
		for(int i = 0; i < mat.length; i++)
			mat[i] *= that[i/cols];
	}
	
	public static void set(double[] dst, double[] src)
	{
		System.arraycopy(src, 0, dst, 0, Stat.n(dst, src));
	}
}
