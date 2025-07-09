package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class Domain implements Span
{
	private final double max_value;
	
	private final boolean inclusive_max;
	
	private final double min_value;
	
	private final boolean inclusive_min;

	public static Domain inclusive(double min, double max)
	{
		return new Domain(min, true, max, true);
	}
	
	public static Domain exclusive(double min, double max)
	{
		return new Domain(min, false, max, false);
	}
	
	public static Domain standard(double min, double max)
	{
		return new Domain(min, true, max, false);
	}
	
	public static Domain reverse(double min, double max)
	{
		return new Domain(min, false, max, true);
	}
	
	public Domain(double min, boolean include_min, double max, boolean include_max)
	{
		if(max < min)
			throw new IllegalArgumentException("Max cannot be smaller than min.");
		this.min_value = min;
		this.inclusive_min = include_min;
		this.max_value = max;
		this.inclusive_max = include_max;
	}
	
	public double range()
	{
		return this.max_value - this.min_value;
	}
	
	public double midRange()
	{
		return 0.5 * this.max_value + 0.5 * this.min_value;
	}
	
	public double max()
	{
		return this.max_value;
	}
	
	public double maxInclusive()
	{
		return this.inclusive_max ? this.max_value : Math.nextDown(this.max_value);
	}

	public double minInclusive()
	{
		return this.inclusive_min ? this.min_value : Math.nextUp(this.min_value);
	}

	
	public double min()
	{
		return this.min_value;
	}
	
	public boolean test(final double arg)
	{
		return (this.inclusive_min ? this.min_value <= arg : this.min_value < arg) && 
				(this.inclusive_max ? arg <= this.max_value : arg < this.max_value);
	}
	
	public boolean overlapping(Domain that)
	{
		return this.test(that.min()) || this.test(that.max()) || that.test(this.min()) || that.test(this.max()) || 
				(this.max() == that.max() && this.min() == that.min() && this.min() != this.max()); // this line handles special case of exclusivity
	}
	
	public boolean contains(Span that)
	{
		return this.test(that.min()) && this.test(that.max());
	}
	
	public Domain stepForward()
	{
		final double step = this.range();
		return new Domain(this.min_value + step, this.inclusive_min, this.max_value + step, this.inclusive_max);
	}
	
	public Domain stepBackward()
	{
		final double step = this.range();
		return new Domain(this.min_value - step, this.inclusive_min, this.max_value - step, this.inclusive_max);
	}	
}
