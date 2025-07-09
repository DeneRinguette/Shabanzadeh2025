package shabanzadeh2025.util;

import org.apache.commons.math3.special.Erf;

/**
 * @author Dene Ringuette
 */

public class ZTest 
{
	public static double TWO_TAILED_UNDERFLOW = 0x1.340705d990bb6p5;
	public static double ONE_TAILED_UNDERFLOW = 0x1.33cc93097ea5cp5;
	
	public static double oneTailedprobability(double x, double x0, double sigma)
	{
		return oneTailedProbability((x-x0)/sigma);
	}
	
	public static double twoTailedprobability(double x, double x0, double sigma)
	{
		return twoTailedProbability((x-x0)/sigma);
	}
	
	public static double oneTailedProbability(double z)
	{
		return 0.5 * twoTailedProbability(z);
	}
	
	public static double twoTailedProbability(double z)
	{
		return Erf.erfc(Math.abs(z) / Math.sqrt(2.0));
	}
}
