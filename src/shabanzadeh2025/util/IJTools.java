package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class IJTools 
{
	final static String WRONG_DIM_MESSAGE = "Dimension Mis-match."; 
	
	public static boolean equisized(ImageStack a, ImageStack b)
	{
		return 
				a.getWidth() == b.getWidth() &&
				a.getHeight() == b.getHeight() &&
				a.getSize() == b.getSize();
	}
	
	public static void checkDim(ImageStack a, ImageStack b)
	{
		if(IJTools.equisized(a, b))
			return;
		throw new IllegalArgumentException(IJTools.WRONG_DIM_MESSAGE);
	}
	
	public static ImageStack toFloat(ImageStack stk)
	{
		final int w = stk.getWidth();
		final int h = stk.getHeight();
		final int n = stk.getSize();
		ImageStack thus = new ImageStack(w, h, n);
		for(int i = 1; i <= n; i++)
		{
			thus.setSliceLabel(stk.getSliceLabel(i), i);
			thus.setProcessor(IJTools.toFloat(stk.getProcessor(i)), i);
		}
		return thus;
	}
	
	public static void checkDim(ImageProcessor a, ImageProcessor b)
	{
		if(IJTools.equisized(a, b))
			return;
		throw new IllegalArgumentException(IJTools.WRONG_DIM_MESSAGE);
	}
	
	public static boolean equisized(ImageProcessor a, ImageProcessor b)
	{
		return  
				a.getWidth() == b.getWidth() && 
				a.getHeight() == b.getHeight();
	}
	
	public static FloatProcessor toFloat(ImageProcessor pro)
	{
		final int w = pro.getWidth();
		final int h = pro.getHeight();
		final int s = w * h;
		FloatProcessor fpro = new FloatProcessor(w, h);
		for(int i = 0; i < s; i++)
			fpro.setf(i, pro.getf(i));
		return fpro;
	}
	
	public static void apply(ImageStack stk, DoubleUnaryOperator func)
	{
		for(int i = 1; i <= stk.getSize(); i++)
			IJTools.apply(stk.getProcessor(i), func);
	}
	
	public static void apply(ImageProcessor pro, DoubleUnaryOperator func)
	{
		for(int i = 0; i < pro.getPixelCount(); i++)
			pro.setf(i, (float)func.applyAsDouble(pro.getf(i)));
	}
	
	public static ByteProcessor toByte(float min, float max, ImageProcessor fpro)
	{
		int w = fpro.getWidth();
		int h = fpro.getHeight();
		float fact = 255/(max-min);
		ByteProcessor bpro = new ByteProcessor(w, h);
		for(int i = 0; i < w * h; i++)
		{
			float value = fpro.getf(i);
			if(value >= max)
				bpro.setf(i, 255);
			else if(value <= min)
				bpro.setf(i, 0);
			else
				bpro.setf(i, fact*(value-min));
		}
		return bpro;
	}
	
	public static ImageStack toByte(float min, float max, ImageStack stk)
	{
		final int w = stk.getWidth();
		final int h = stk.getHeight();
		final int n = stk.getSize();
		ImageStack thus = new ImageStack(w, h, n);
		for(int i = 1; i <= n; i++)
		{
			thus.setSliceLabel(stk.getSliceLabel(i), i);
			thus.setProcessor(IJTools.toByte(min, max, stk.getProcessor(i)), i);
		}
		return thus;
	}
	
	public static ByteProcessor toByte(ImageProcessor pro)
	{
		return toByte(pro, IStat.min(pro), IStat.max(pro));
	}
	
	public static ByteProcessor toByte(ImageProcessor pro, double min, double max)
	{
		final int w = pro.getWidth();
		final int h = pro.getHeight();
		final int n = w * h;
		final double fact = 255.0/(max-min);
		ByteProcessor bro = new ByteProcessor(w, h);
		for(int i = 0; i < n; i++)
			bro.set(i, (int)(fact*(pro.getf(i)-min)));
		return bro;
	}
	
	public static double[] toDoubles(ImageProcessor pro)
	{
		final int n = pro.getPixelCount();
		double[] matrix = new double[n];
		for(int i = 0; i < n; i++)
			matrix[i] = pro.getf(i);
		return matrix;
	}
}
