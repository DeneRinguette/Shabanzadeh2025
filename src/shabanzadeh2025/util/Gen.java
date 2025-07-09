package shabanzadeh2025.util;

import java.util.Random;

/**
 * @author Dene Ringuette
 */

public class Gen 
{
	private static Random rand = new Random();
	
	public static double[] range(double start, double step, double end)
	{
		int gaps = (int)(Math.abs(end-start)/step);
		double[] values = new double[gaps+1];
		for(int i = 0; i <= gaps; i++)
			values[i] = start + i * step;
		return values;
	}
	
	public static double[] fromBy(double start, double step, int gaps)
	{
		double[] values = new double[gaps+1];
		for(int i = 0; i <= gaps; i++)
			values[i] = start + i * step;
		return values;
	}
	
	public static double[] constant(double arg0, int size)
	{
		double[] cons = new double[size];
		for(int i = 0; i < size; i++)
			cons[i] = arg0;
		return cons;
	}
	
	public static double[] unit(int dim)
	{
		double[] thus = Gen.gaussian(dim);
		Ip.normHyp(thus);
		return thus;
	}
	
	public static double[] gaussian(int dim)
	{
		double[] thus = new double[dim];
		for(int i = 0; i < dim; i++)
			thus[i] = Gen.rand.nextGaussian();
		return thus;
	}
}
