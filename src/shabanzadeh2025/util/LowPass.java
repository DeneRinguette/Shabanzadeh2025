package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class LowPass 
{
	public static double[] rcZeroPhase(double dt, double RC, double[] x)
	{
		final double RC1 = 1.0 / Math.sqrt(Math.sqrt(2.0)-1.0) * RC;
		double[] y = LowPass.rc(dt, RC1, x);
		return LowPass.rc_rev(dt, RC1, y);
	}

	public static double[] rc(double dt, double RC, double[] x)
	{
		final double alpha = dt / (RC + dt); // different from high-pass alpha
		final double[] y = new double[x.length];
		
		y[0] = x[0];
		for(int i = 1; i < x.length; i++)
			y[i] = y[i-1] + alpha * (x[i] - y[i-1]);
		return y;
	}
	
	public static double[] rc_rev(double dt, double RC, double[] x)
	{
		final double alpha = dt / (RC + dt);
		final double[] y = new double[x.length];
		final int end = x.length-1;
		
		y[end] = x[end];
		for(int i = end-1; 0 <= i; i--)
			y[i] = y[i+1] + alpha * (x[i] - y[i+1]);
		return y;
	}
}
