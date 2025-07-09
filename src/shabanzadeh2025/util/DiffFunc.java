package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Dene Ringuette
 */

public interface DiffFunc extends DoubleUnaryOperator 
{
	DoubleUnaryOperator deriv();	
}
