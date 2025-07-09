package shabanzadeh2025.util;

import org.apache.commons.math3.special.Beta;

/**
 * Complementary cumulative distribution function.
 * @author Dene Ringuette
 */

public class Ccdf 
{	
	public static double f(double arg, int df_n, int df_d)
	{
		return Beta.regularizedBeta(df_d / (df_n * arg + df_d), df_d * 0.5, df_n * 0.5);
	}
		
	public static double t(double t, int df)
	{
		return Beta.regularizedBeta(df / (t * t + df), df * 0.5, 0.5);
	}
}
