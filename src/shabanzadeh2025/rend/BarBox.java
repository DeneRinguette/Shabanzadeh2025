package shabanzadeh2025.rend;

/**
 * Box in graph.
 * 
 * @author Dene Ringuette
 * @deprecated methods for bar graphs added to <code>Plot</code> which used <code>Box</code>
 */

public class BarBox
{
	/**
	 * Maximum value of value axis.
	 */
	
	public final double maxValue;
	
	/**
	 * Minimum value of value axis.
	 */
	
	public final double minValue;
	
	/**
	 * 
	 */
	
	public final int categories;
	
	/**
	 * 
	 */
	
	public final int subCategories;
	
	public BarBox(int cats, int subs, double min, double max)
	{
		this.categories = cats;
		this.subCategories = subs;
		this.minValue = min;
		this.maxValue = max;
	}
}
