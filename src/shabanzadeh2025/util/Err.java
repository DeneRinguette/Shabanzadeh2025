package shabanzadeh2025.util;

/**
 * Error propagation.
 * 
 * @author Dene Ringuette
 */

public class Err 
{
	public static double[] mean(double[] x)
	{
		final int size = x.length;
		final double mean = Sum.neumaierSum(x) / size;
		return new double[]{mean, Math.sqrt(Stat.sse(x, mean)/(size-1)/size)};
	}
	
	public static double[] sub(double x, double dx, double y, double dy)
	{
		if(dx < 0 || dy < 0)
			throw new IllegalArgumentException("Errors cannot be negative");
		return new double[]{x - y, Math.hypot(dx, dy)};
	}
	
	public static double[] exp(double x, double dx)
	{
		if(dx < 0)
			throw new IllegalArgumentException("Errors cannot be negative.");
		final double val = Math.exp(x);
		return new double[]{val, val * dx};
	}
	
	public static double[] div(double[] x, double[] y)
	{
		return Err.div(Stat.mean(x), Stat.se(x), Stat.mean(y), Stat.se(y));
	}
	
	public static double[] div_(double[] x_dx, double[] y_dy)
	{
		return Err.div(x_dx[0], x_dx[1], y_dy[0], y_dy[1]);
	}
	
	public static double[] div(double x, double dx, double y, double dy)
	{
		if(dx < 0 || dy < 0)
			throw new IllegalArgumentException("Errors cannot be negative");
		final double val = x / y;
		return new double[]{val, Math.abs(val) * Math.hypot(dx/x, dy/y)};
	}
	
	public static double[] sq_(double[] x_dx)
	{
		return Err.sq(x_dx[0], x_dx[1]);
	}
	
	public static double[] sq(double x, double dx)
	{
		if(dx < 0)
			throw new IllegalArgumentException("Errors cannot be negative");
		return new double[]{x * x, 2 * x * dx};
	}	
}
