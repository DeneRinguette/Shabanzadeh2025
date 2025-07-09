package shabanzadeh2025.util;

import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.Iterator;

/**
 * @author Dene Ringuette
 */

public abstract class Histogram
{
	protected final long[] counts;
	protected long downAndOut, upAndOut, total;
	
	Histogram(int groups)
	{
		this.counts = new long[groups];
		this.downAndOut = 0;
		this.upAndOut = 0;
		this.total = 0;
	}
	
	public long aboveRange()
	{
		return this.upAndOut;
	}
	
	public long belowRange()
	{
		return this.downAndOut;
	}
	
	public boolean bounded()
	{
		return this.downAndOut == 0 && this.upAndOut == 0;
	}
	
	public void clear()
	{
		this.total = 0;
		this.downAndOut = 0;
		this.upAndOut = 0;
		for(int i = 0; i < this.counts.length; i++)
			this.counts[i] = 0;
	}
	
	public abstract Iterator<?> iterator();
	
	public long outOfRange()
	{
		return this.downAndOut + this.upAndOut;
	}
	
	public int bins()
	{
		return this.counts.length;
	}
	
	public long inRange()
	{
		return this.total - this.outOfRange();
	}
	
	public abstract boolean count(float pix);
	
	public void count(ImageProcessor img)
	{
		final int nPixels = img.getPixelCount();
		for(int pixel = 0; pixel < nPixels; pixel++)
			this.count(img.getf(pixel));
	}
	
	public void count(ImageStack stk)
	{
		final int nSlices = stk.getSize();
		for(int slice = 1; slice <= nSlices; slice++)
			this.count(stk.getProcessor(slice));
	}
		
	public void trim()
	{
		this.total -= this.upAndOut;
		this.total -= this.downAndOut;
		this.upAndOut = 0;
		this.downAndOut = 0;
	}

	public double[] pdf()
	{
		final int n = this.counts.length;	
		double[] prob = new double[n];
		for(int i = 0; i < n; i++)
			prob[i] = (this.counts[i] + 0.0)/this.total;
		return prob;
	}
	
	public double[] counts()
	{
		final int n = this.counts.length;	
		double[] prob = new double[n];
		for(int i = 0; i < n; i++)
			prob[i] = (double)this.counts[i];
		return prob;
	}
		
	public double[] cdf()
	{
		double[] cdf = this.pdf();
		Ip.cumulative(cdf);
		return cdf;
	}
	
	public double[] rcdf()
	{
		double[] cdf = this.pdf();
		Ip.decumulative(cdf);
		return cdf;
	}
	
	public double upperFraction(int threshold)
	{
		final int n = this.counts.length;
		long above = 0L;
		for(int i = threshold; i < n; i++)
			above += this.counts[i];
		above += this.upAndOut;
		
		return (above + 0.0) / this.total;
		
	}
	
	public long total()
	{
		return this.total;
	}
	
	public void count(ImageStack data, ImageStack mask, double threshold)
	{
		IJTools.checkDim(data, mask);
		final int stack_size = data.getSize();
		final int pixels_per_slice = data.getWidth() * data.getHeight();
		for(int i = 1; i <= stack_size; i++)
		{
			ImageProcessor data_slice = data.getProcessor(i);
			ImageProcessor mask_slice = mask.getProcessor(i);
			for(int k = 0; k < pixels_per_slice; k++)
				if(mask_slice.getf(k) > threshold)
					this.count(data_slice.getf(k));
		}
	}
}
