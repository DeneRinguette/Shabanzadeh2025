package shabanzadeh2025.util;

import java.util.function.DoublePredicate;

/**
 * @author Dene Ringuette
 */

public interface Span extends DoublePredicate
{
	public double range();
	
	public double midRange();
	
	public double max();
	
	public double min();
	
	public boolean contains(Span that);
}
