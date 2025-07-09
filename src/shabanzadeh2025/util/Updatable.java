package shabanzadeh2025.util;

import java.util.Collection;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;

import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public interface Updatable 
{
	public void add(double value);
		
	public static <T extends Updatable> T update(T stat, ImageProcessor processor)
	{
		final int pixels = processor.getPixelCount();
		for(int pixel = 0; pixel < pixels; pixel++)
			stat.add(processor.getf(pixel));
		return stat;
	}
	
	public static <T extends Updatable> T update(T stat, ImageStack stack)
	{
		final int size = stack.getSize();
		for(int slice = 1; slice <= size; slice++)
			update(stat, stack.getProcessor(slice));
		return stat;
	}
	
	public static <T extends Updatable> T update(T stat, ImageProcessor processor, DoubleUnaryOperator func)
	{
		final int pixels = processor.getPixelCount();
		for(int pixel = 0; pixel < pixels; pixel++)
			stat.add(func.applyAsDouble(processor.getf(pixel)));
		return stat;
	}
	
	public static <T extends Updatable> T update(T stat, ImageProcessor a, ImageProcessor b, DoubleBinaryOperator func)
	{
		final int width = a.getWidth();
		final int height = a.getHeight();
		if(width != b.getWidth() || height != b.getHeight())
			throw new IllegalArgumentException();
		final int pixels = a.getPixelCount();
		for(int pixel = 0; pixel < pixels; pixel++)
			stat.add(func.applyAsDouble(a.getf(pixel), b.getf(pixel)));
		return stat;
	}
	
	public static <T extends Updatable> T update(T stat, ImageStack stack, DoubleUnaryOperator func)
	{
		final int size = stack.getSize();
		for(int slice = 1; slice <= size; slice++)
			update(stat, stack.getProcessor(slice), func);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, Collection<R> values, ToDoubleFunction<R> func)
	{
		for(R value : values)
			stat.add(func.applyAsDouble(value));
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, R[] values, ToDoubleFunction<R> func)
	{
		for(R value : values)
			stat.add(func.applyAsDouble(value));
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, R[][] values, ToDoubleFunction<R> func)
	{
		for(R[] row : values)
			update(stat, row, func);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[] values)
	{
		for(double value : values)
			stat.add(value);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[][] values)
	{
		for(double[] row : values)
			update(stat, row);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[][][] values)
	{
		for(double[][] block : values)
			update(stat, block);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[] values, DoubleUnaryOperator func)
	{
		for(double value : values)
			stat.add(func.applyAsDouble(value));
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[][] values, DoubleUnaryOperator func)
	{
		for(double[] row : values)
			update(stat, row, func);
		return stat;
	}
	
	public static <T extends Updatable, R> T update(T stat, double[][][] values, DoubleUnaryOperator func)
	{
		for(double[][] block : values)
			update(stat, block, func);
		return stat;
	}
}