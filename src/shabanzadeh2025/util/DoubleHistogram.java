package shabanzadeh2025.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class DoubleHistogram extends Histogram implements Iterable<DoubleHistogram.Gap>, DoubleUnaryOperator, Addable<DoubleHistogram>, Updatable
{
	public static class Gap
	{		
		private final double start, stop;
		private final long count;
		private final long total;
		
		public Gap(double start, double stop, long count, long total)
		{
			this.start = start;
			this.stop = stop;
			this.count = count;
			this.total = total;
		}

		public long count() 
		{
			return this.count;
		}

		public double start() 
		{
			return this.start;
		}

		public double stop() 
		{
			return this.stop;
		}
		
		public long total()
		{
			return this.total;
		}
		
		public double probability()
		{
			return (double)this.count/this.total;
		}
		
		public String toString()
		{
			return "[" + this.start + "," + this.stop + ") -> " + this.count;
		}		
	}
	
	private final double start, stop, gap;

	private final boolean fine;
	private final double[] bin_sum;
	private double down_sum, up_sum;
		
	public DoubleHistogram(double start, double stop, int groups, boolean sub_means)
	{
		super(groups);
		this.start = start;
		this.stop = stop;
		this.gap = (stop - start)/groups;
		this.bin_sum = sub_means ? new double[groups] : null;
		this.fine = sub_means;
	}
	
	@Override
	public DoubleHistogram get() 
	{
		return new DoubleHistogram(this.start, this.stop, this.counts.length, this.bin_sum != null);
	}
	
	public DoubleHistogram(ImageProcessor pro, int groups)
	{
		this(IStat.min(pro), 1.0 + IStat.max(pro), groups, false);
		this.count(pro);
	}
	
	public DoubleHistogram(ImageStack stk, int groups)
	{
		this(IStat.min(stk), 1.0 + IStat.max(stk), groups, false);
		this.count(stk);
	}
	
	public double[] rightTailedThresholds(double[] fractions)
	{
		if(!this.bounded())
			throw new IllegalStateException("Method only for bounded histograms.");
		
		probCheck(fractions);
		
		final int n = this.counts.length;
		final int k = fractions.length;
		
		double[] rcdf = this.rcdf();
		double[] threshold = new double[k];
		
		int i = 0;
		int j = 0;
		while(i < n && j < k)
		{
			if(rcdf[i] < fractions[j])
				threshold[j++] = this.binLeft(i);
			i++;
		}
		while(j < k)
			threshold[j++] = this.stop;
		
		return threshold;
	}
	
	public double[] leftTailedThresholds(double[] fractions)
	{
		if(!this.bounded())
			throw new IllegalStateException("Method only for bounded histograms.");
		
		probCheck(fractions);
		
		final int n = this.counts.length;
		final int k = fractions.length;
		
		double[] cdf = this.cdf();
		double[] threshold = new double[k];
		
		int j = 0;
		for(int i = n-1; -1 < i; i--)
			if(cdf[i] < fractions[j])
				threshold[j++] = this.binRight(i);
		
		while(j < k)
			threshold[j++] = this.start;
		
		return threshold;
	}
	
	public static void probCheck(double[] fractions)
	{
		if(!Stat.descending(fractions))
			throw new IllegalArgumentException("dimension mismatch");
		
		if(1 < Stat.max(fractions))
			throw new IllegalArgumentException("prob > 1");
		
		if(Stat.min(fractions) <= 0)
			throw new IllegalArgumentException("prob <= 0");
	}
	

	public double rightTailedThreshold(double fraction)
	{
		if(!this.bounded())
			throw new IllegalStateException("Method only for bounded histograms.");
		
		probCheck(fraction);
		
		final int n = this.counts.length;
		
		double[] rcdf = this.rcdf();
		
		for(int i = 0; i < n; i++)
			if(rcdf[i] < fraction)
				return this.binLeft(i);
		
		return this.stop;
	}
	
	public double leftTailedThreshold(double fraction)
	{
		if(!this.bounded())
			throw new IllegalStateException("Method only for bounded histograms.");
		
		
		probCheck(fraction);
		
		final int n = this.counts.length;
		
		double[] cdf = this.cdf();
		
		for(int i = n-1; -1 < i; i--)
			if(cdf[i] < fraction)
				return this.binRight(i);
		
		return this.start;
	}
	
	public double binLeft(int i)
	{
		return this.start + i * this.gap;
	}
	
	public double binMiddle(int i)
	{
		return this.start + (i+0.5) * this.gap;
	}
	
	public double binRight(int i)
	{
		return this.start + (i+1) * this.gap;
	}
	
	public static void probCheck(double fraction)
	{
		if(1 < fraction)
			throw new IllegalArgumentException("prob > 1");
		
		if(fraction <= 0)
			throw new IllegalArgumentException("prob <= 0");
	}
		
	public boolean count(double arg, int times)
	{
		this.total += times;
		if(arg < this.start)
		{
			this.downAndOut += times;
			if(this.fine)
				this.down_sum += times * arg;
			return false;
		}
		if(this.stop <= arg)
		{
			this.upAndOut += times;
			if(this.fine)
				this.up_sum += times * arg;
			return false;
		}
		final int index = (int)((arg-this.start)/this.gap);
		this.counts[index] += times;
		if(this.fine)
			this.bin_sum[index] += times * arg; 
		return true;
	}
	
	public boolean count(double arg)
	{
		return this.count(arg, 1);
	}
	
	public void add(double arg)
	{
		this.count(arg, 1);
	}
	
	public void count(ImageProcessor img, ImageProcessor mask, float thres, boolean high)
	{
		final int n = img.getPixelCount();
		if(high)
			for(int i = 0; i < n; i++)
			{
				if(mask.getf(i) >= thres)
					this.count(img.getf(i));
			}
		else
			for(int i = 0; i < n; i++)
			{
				if(mask.getf(i) < thres)
					this.count(img.getf(i));
			}
	}
	
	public void count(ImageStack stk)
	{
		final int n = stk.getSize();
		for(int i = 1; i <= n; i++)
			this.count(stk.getProcessor(i));
	}
	
	public void count(double[] args)
	{
		for(double i : args)
			this.count(i);
	}
	
	public void count(float[] args)
	{
		for(float i : args)
			this.count(i);
	}
	
	public boolean count(float val)
	{
		return this.count((double)val);
	}
	
	public void count(Iterable<Double> args)
	{
		for(Double i : args)
			this.count(i);
	}
	
	public double gap()
	{
		return this.gap;
	}
	
	public boolean fine()
	{
		return this.fine;
	}
	
	@Override
	public Iterator<Gap> iterator() 
	{
		return 
			new Iterator<Gap>()
			{
			
				private int i = -1;
			
				@Override
				public boolean hasNext() 
				{
					return this.i <= counts.length;
				}

				@Override
				public Gap next() 
				{
					if(i < 0)
					{
						this.i++;
						return new Gap(Double.NEGATIVE_INFINITY, start, downAndOut, total);
					}
					if(counts.length <= i)
					{
						this.i++;
						return new Gap(stop, Double.POSITIVE_INFINITY, upAndOut, total);
					}
					double s = start + gap * i;
					return new Gap(s, s + gap, counts[i++], total);
				}

				@Override
				public void remove() 
				{
					new UnsupportedOperationException().printStackTrace();
				}
			
			};
	}
	
	public double start()
	{
		return this.start;
	}
	
	public double stop()
	{
		return this.stop;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("( " + this.downAndOut);
		double s;
		for(int i = 0; i < this.counts.length; i++)
		{
			s = this.start + this.gap * i;
			sb.append(" (" + s + "] " + this.counts[i]);
		}
		sb.append(" (" + this.stop + "] " + this.upAndOut + " )");
		return new String(sb);
	}
	
	static void test0()
	{
		DoubleHistogram h = new DoubleHistogram(0, 10, 5, false);
		Random r = new Random();
		for(int i = 0; i < 100; i++)
			h.count(r.nextDouble()*10);
		System.out.println(h);
		for(Gap i : h)
			System.out.println(i);
	}

	@Override
	public double applyAsDouble(double arg)
	{
		return this.probablity(arg);
	}
	
	
	public double probablity(double arg)
	{
		return (double)this.number(arg)/this.total;
	}
	
	public long number(double arg)
	{
		if(arg < this.start)
			return this.downAndOut;
		if(this.stop <= arg)
			return this.upAndOut;
		return this.counts[(int)((arg-this.start)/this.gap)];
	}
	
	public double[][] pdfPoints()
	{
		final int n = this.counts.length;
		double[] x = new double[n];
		double[] y = new double[n];
		for(int i = 0; i < n; i++)
		{
			final double ni = (double)this.counts[i];
			if(ni == 0)
			{
				x[i] = (i+0.5) * this.gap + this.start;
				y[i] = 0;
			}
			else
			{
				x[i] = this.bin_sum[i]/ni;
				y[i] = ni/this.total;
			}
		}
		return new double[][]{x, y};
	}
	
	public double[] binMiddles()
	{
		final int n = this.counts.length;
		final double[] x = new double[n];
		
		for(int i = 0; i < n; i++)
			x[i] = this.binMiddle(i);

		return x;
	}
	
	public double[][] pdfPointsExcludeZero()
	{
		double[][] points = this.pdfPoints();
		final int n = points[1].length;

		int first = 0;
		while(points[1][first] == 0)
			first++;
		
		int last = n-1;
		while(points[1][last] == 0)
			last--;
		last++;
		
		return new double[][]{
				Arrays.copyOfRange(points[0], first, last), 
				Arrays.copyOfRange(points[1], first, last)
			};
	
	}
	
	public double rightTailMean()
	{
		return this.up_sum/this.upAndOut;
	}
	
	public double leftTailMean()
	{
		return this.down_sum/this.downAndOut;
	}
	
	public double max()
	{
		if(this.upAndOut > 0)
			return Double.NaN;
		int i = this.counts.length;
		while(this.counts[--i] == 0);
		return this.binRight(i);	
	}
	
	public double min()
	{
		if(this.downAndOut > 0)
			return Double.NaN;
		int i = -1;
		while(this.counts[++i] == 0);
		return this.binLeft(i);	
	}

	@Override
	public void set(DoubleHistogram that) 
	{
		this.ensureSameStructure(that);
		final int bins = this.bins();
		if(this.fine())
		{
			this.down_sum = that.down_sum;
			for(int i = 0; i < bins; i++)
				this.bin_sum[i] = that.bin_sum[i];
			this.up_sum = that.up_sum;
		}
		this.total = that.total;
		this.downAndOut = that.downAndOut;
		for(int i = 0; i < bins; i++)
			this.counts[i] = that.counts[i];
		this.upAndOut = that.upAndOut;
	}

	@Override
	public DoubleHistogram copy() 
	{
		DoubleHistogram thus = 
				new DoubleHistogram(this.start(), this.stop(), this.bins(), this.fine());
		thus.set(this);
		return thus;
	}

	@Override
	public void add(DoubleHistogram that) 
	{
		this.ensureSameStructure(that);
		final int bins = this.bins();
		if(this.fine())
		{
			this.down_sum += that.down_sum;
			for(int i = 0; i < bins; i++)
				this.bin_sum[i] += that.bin_sum[i];
			this.up_sum += that.up_sum;
		}
		this.total += that.total;
		this.downAndOut += that.downAndOut;
		for(int i = 0; i < bins; i++)
			this.counts[i] += that.counts[i];
		this.upAndOut += that.upAndOut;
	}
	
	public boolean hasSameStructure(DoubleHistogram that)
	{
		return this.start() == that.start() && this.stop() == that.stop() && this.gap() == that.gap() && this.fine() == that.fine();
	}
	
	public void ensureSameStructure(DoubleHistogram that)
	{
		if(!this.hasSameStructure(that))
			throw new IllegalArgumentException("Structures not the same.");
	}
	
	public void dropBelow(double value)
	{
		if(this.start() <= value)
		{
			this.total -= this.downAndOut;
			this.downAndOut = 0;
		
			int i = 0;
			while(this.binRight(i) <= value && i < this.bins())
			{
				this.total -= this.counts[i];
				this.counts[i] = 0;
				i++;
			}
			
			if(Double.POSITIVE_INFINITY == value)
			{
				this.total -= this.upAndOut;
				this.upAndOut = 0;
			}
		}
	}
	
	public double aboveOverBelow(double value)
	{
		final int i = (int)((value-this.start())/this.gap());
		final double[] cdf = this.cdf();
		final double[] rcdf = this.rcdf();
		return rcdf[i]/cdf[i];
	}
	
	public double above(double value)
	{
		final int i = (int)((value-this.start())/this.gap());
		final double[] rcdf = this.rcdf();
		return rcdf[i];
	}
	
	public double meanAbove(double value)
	{
		final int initial = (int)((value-this.start())/this.gap());
		double sumOfWeights = 0.0;
		double sumOfWeightedValues = 0.0;
		
		for(int index = initial; index < this.counts.length; index++)
		{
			final double weight = (this.counts[index]+0.0) / this.total;
			sumOfWeights += weight;
			sumOfWeightedValues += this.binMiddle(initial) * weight;
		}
		return sumOfWeightedValues / sumOfWeights;
	}
	
	public double[] iqr()
	{
		final double[] cdf = this.cdf();
		
		final double q1 = this.start + this.gap * Roots.firstOf(0.25, cdf);
		final double q2 = this.start + this.gap * Roots.firstOf(0.50, cdf);
		final double q3 = this.start + this.gap * Roots.firstOf(0.75, cdf);
		
		return new double[] {q3-q1, q1, q2, q3};
	}
	
	public double mean()
	{
		if(this.fine)
		{
			final int bins = this.bins();
			double total_sum = 0.0;
			total_sum += this.down_sum;
			for(int i = 0; i < bins; i++)
				total_sum += this.bin_sum[i];
			total_sum += this.up_sum;
			return total_sum/this.total;
			
		}
		else
		{
			if(this.downAndOut > 0 || this.upAndOut > 0)
				return Double.NaN;
			double sum = 0.0;
			int bins = this.bins();
			for(int i = 0; i < bins; i++)
				sum += this.counts[i] * this.binMiddle(i);
			return sum/this.total;
		}
	}
	
	public double mode()
	{
		return Stat.argMax(this.pdf());
	}
}
