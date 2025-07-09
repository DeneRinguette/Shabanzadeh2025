package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Increment 
{
	private final double origin;
	
	private final double increment;
	
	public Increment(int start, double step)
	{
		this.origin = start;
		this.increment = step;
	}
	
	public static Increment fromZero(double step)
	{
		return new Increment(0, step);
	}
	
	public double origin() 
	{
		return origin;
	}
	
	public double increment() 
	{
		return increment;
	}
}
