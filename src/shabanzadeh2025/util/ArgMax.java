package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public class ArgMax<T> extends ArgExtrema<T>
{
	public ArgMax(boolean forwardBias)
	{
		super(forwardBias, Double.NEGATIVE_INFINITY);
	}
	
	public boolean sufficientForUpdate(double value) 
	{
		return this.value() < value;		
	}
	
	protected String label()
	{
		return "max";
	}	
}
