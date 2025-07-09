package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

/**
 * Antidifferentiable function.
 * @author Dene Ringuette
 */


public interface AntiFunc extends DoubleUnaryOperator
{
	DoubleUnaryOperator anti();
}
