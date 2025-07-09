package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Cov 
{
	public static double toPeasons(double fisher)
	{
		return Math.tanh(fisher);
	}
	
	public static double toFisher(double peasonsR)
	{
		return Hyperbolic.artanh(peasonsR);
	}
}
