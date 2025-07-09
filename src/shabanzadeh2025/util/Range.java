package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class Range implements Addable<Range>, DataSet, Updatable, Span
{
	private long count = 0;
	
	private double max_val = Double.NEGATIVE_INFINITY;
	
	private long deg_max = 0;
	
	private double min_val = Double.POSITIVE_INFINITY;
	
	private long deg_min = 0;
	
	public Range()
	{
		
	}
	
	public Range(double... values)
	{
		this.add(values);
	}
	
	public void add(double[][][] values)
	{
		for(double[][] value : values)
			this.add(value);
	}
	
	public void add(double[][] values)
	{
		for(double[] value : values)
			this.add(value);
	}
	
	public void add(double... values)
	{
		for(double value : values)
			this.add(value);
	}
	
	public void add(ImagePlus img)
	{
		final int n = img.getStackSize();
		if(n == 1)
			this.add(img.getProcessor());
		else
			this.add(img.getStack());
	}
	
	public void add(ImageProcessor pro)
	{
		final int n = pro.getPixelCount();
		for(int i = 0; i < n; i++)
			this.add(pro.getf(i));
	}
	
	public void add(ImageStack stk)
	{
		final int n = stk.getSize();
		for(int i = 1; i <= n; i++)
			this.add(stk.getProcessor(i));
	}
	
	public void add(float val)
	{
		this.add((double)val);
	}
	
	public void add(double val)
	{
		this.count++;
		this.update_min(val);
		this.update_max(val);
	}
	
	private void update_max(double val)
	{
		if(this.max_val == val)
			this.deg_max += 1;
		else if(this.max_val < val)
		{
			this.deg_max = 1;
			this.max_val = val;
		}
	}
	
	private void update_min(double val)
	{
		if(this.min_val == val)
			this.deg_min += 1;
		else if(this.min_val > val)
		{
			this.deg_min = 1;
			this.min_val = val;
		}
	}
	
	public double range()
	{
		if(this.count > 0)
			return this.max_val - this.min_val;
		else
			return Double.NaN;
	}
	
	public double midRange()
	{
		if(this.count > 0)
			return 0.5 * this.max_val + 0.5 * this.min_val;
		else
			return Double.NaN;
	}
	
	public double max()
	{
		if(this.count > 0)
			return this.max_val;
		else
			return Double.NaN;
	}
	
	public long degeneracyOfMax()
	{
		return this.deg_max;
	}
	
	public double min()
	{
		if(this.count > 0)
			return this.min_val;
		else
			return Double.NaN;
	}
	
	public double min(double range_buffer)
	{
		return this.min() - this.range() * range_buffer;
	}
	
	public double max(double range_buffer)
	{
		return this.max() + this.range() * range_buffer;
	}
	
	public long degeneracyOfMin()
	{
		return this.deg_min;
	}
	
	public double meanGap()
	{
		if(this.count > 1)
			return this.range() / (this.count - 1);
		else
			return Double.NaN;
	}
	
	public double densityWithinBounds()
	{
		if(this.count > 0)
			return this.count / this.range();
		else
			return Double.NaN;
	}
	
	public long count()
	{
		return this.count;
	}
	
	public boolean test(final double arg)
	{
		if(this.count == 0)
			throw new IllegalArgumentException("No data defining range.");
		return this.min_val <= arg && arg <= this.max_val;
	}
		
	public boolean overlapping(Range that)
	{
		return this.test(that.min()) || this.test(that.max()) || that.test(this.min()) || that.test(this.max());
	}
	
	public boolean contains(Range that)
	{
		return this.test(that.min()) && this.test(that.max());	
	}

	@Override
	public void set(Range that) 
	{
		this.count = that.count;
		this.deg_max = that.deg_max;
		this.deg_min = that.deg_min;
		this.max_val = that.max_val;
		this.min_val = that.min_val;
	}

	@Override
	public Range copy() 
	{
		Range copy = new Range();
		copy.set(this);
		return copy;
	}

	@Override
	public void add(Range that) 
	{
		if(that.count == 0)
			return;
		
		if(this.count == 0)
		{
			this.set(that);
			return;
		}
		
		this.count += that.count;
		
		if(this.max_val < that.max_val)
		{
			this.max_val = that.max_val;
			this.deg_max = that.deg_max;
		}
		else if(this.max_val == that.max_val)
		{
			this.deg_max += that.deg_max;
		}
		
		if(this.min_val > that.min_val)
		{
			this.min_val = that.min_val;
			this.deg_min = that.deg_min;
		}
		else if(this.min_val == that.min_val)
		{
			this.deg_min += that.deg_min;
		}
	}
	
	public double winsorize(double value)
	{
		if(this.count == 0)
			throw new IllegalStateException("No range defined.");
		
		if(this.min_val > value)
			return this.min_val;
		
		if(this.max_val < value)
			return this.max_val;
		
		return value;
	}

	@Override
	public int size() 
	{
		if(this.count > Integer.MAX_VALUE)
			return -1;
		return (int)this.count;
	}

	@Override
	public void clear() 
	{
		this.count = 0;
		this.max_val = Double.NEGATIVE_INFINITY;
		this.deg_max = 0;
		this.min_val = Double.POSITIVE_INFINITY;
		this.deg_min = 0;
	}
	
	public Domain immutable()
	{
		if(this.count == 0)
			throw new IllegalArgumentException("No data defining range.");
		return new Domain(this.min_val, true, this.max_val, true);
	}
	
	public boolean contains(Span that)
	{
		return this.test(that.min()) && this.test(that.max());
	}
	
	public String toString()
	{
		return (this.deg_min > 0 ? ("'" + this.deg_min) : "") + "[" + this.min_val + ", " + this.max_val + "]" + (this.deg_max > 0 ? (this.deg_max + "'") : "");
	}
	
	public double normed(double value)
	{
		if(this.count == 0)
			return Double.NaN;
		if(value > this.max())
			return 1.0;
		if(value < this.min())
			return 0.0;
		return (value-this.min())/this.range();
	}
	
	public int display(double value)
	{
		return (int)(255*this.normed(value));
	}

	@Override
	public Range get() 
	{
		return new Range();
	}
	
	public void rescale(DoubleUnaryOperator func)
	{
		final double max = func.applyAsDouble(this.max_val);
		final double min = func.applyAsDouble(this.min_val);
		
		if(min == max && this.min_val != this.max_val)
			throw new IllegalArgumentException("Rescaling function cannot be one-to-one as it introduced range degeneracy.");
		else if(min < max)
		{
			this.max_val = max;
			this.min_val = min;
		}
		else if(max < min)
		{
			this.max_val = min;
			this.min_val = max;
			this.deg_max = this.deg_min;
			this.deg_min = this.deg_max;
			
		}
	}
	
	public double[] marksForPlot()
	{
		final double max = this.max();
		{
			if(max == 0)
				new AxisMarks(Domain.inclusive(0, 1), Increment.fromZero(0.2)).getPoints();
			final double upper_pow10 = Math.ceil(Math.log10(max));
			double upper = Math.pow(10, upper_pow10);
			double step = 0.1 * upper;
			//So.pl(upper);
			if(max < 0.2*upper)
				upper *= 0.2;
			else if(max < 0.3*upper)
				upper *= 0.3;
			else if(max < 0.4*upper)
				upper *= 0.4;
			else if(max < 0.5*upper)
				upper *= 0.5;
			else if(max < 0.6*upper)
			{
				upper *= 0.6;
				step *= 2;
			}
			else if(max < 0.8*upper)
			{
				upper *= 0.8;
				step *= 2;
			}
			else if(max < 0.9*upper)
			{
				upper *= 0.9;
				step *= 3;
			}
			else
				step *= 2;
			
			return new AxisMarks(Domain.inclusive(0, upper), Increment.fromZero(step)).getPoints();
		}
	}
}