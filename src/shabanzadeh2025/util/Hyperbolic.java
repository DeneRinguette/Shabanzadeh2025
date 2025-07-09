package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Hyperbolic 
{	
	public static double arcosh(double arg)
	{
		return Math.log(arg + Math.sqrt(arg * arg - 1));
	}
	
	public static double arcoth(double arg)
	{
		return Math.log((arg + 1) / (arg - 1)) / 2;
	}
	
	public static double arsech(double arg)
	{
		return Math.log((1 + Math.sqrt(1 - arg * arg)) / arg);
	}
	
	public static double arsinh(double arg)
	{
		return Math.log(arg + Math.sqrt(arg * arg + 1));
	}
		
	public static double artanh(double arg)
	{
		return 0.5 * Math.log((1.0 + arg) / (1.0 - arg));
	}
	
	public static double tanh(double arg)
	{
		if(19.0 < arg)
			return 1.0;
		if(arg < -19.0)
			return -1.0;
		double exp = Math.exp(arg);
		double inv = 1.0 / exp;
		return (exp - inv) / (exp + inv);
	}
	
	public static double arcsch(double arg)
	{
		return Math.log(1 / arg + Math.sqrt(1 + arg * arg) / Math.abs(arg));
	}
	
	public static double sinh(double arg)
	{
		double expArg = Math.exp(arg);
		return (expArg - 1.0 / expArg) / 2;
	}
	
	public static double sech(double arg)
	{
		double expArg = Math.exp(arg);
		return 2 / (expArg + 1.0 / expArg);
	}
	
	public static double cosh(double arg)
	{
		double expArg = Math.exp(arg);
		return (expArg + 1.0 / expArg) / 2;
	}
	
	public static double coth(double arg)
	{
		if(19.0 < arg)
			return 1.0;
		if(arg < -19.0)
			return -1.0;
		double exp = Math.exp(arg);
		double inv = 1.0 / exp;
		return (exp + inv) / (exp - inv);
	}
	
	public static double csch(double arg)
	{
		double expArg = Math.exp(arg);
		return 2 / (expArg - 1.0 / expArg);
	}
}
