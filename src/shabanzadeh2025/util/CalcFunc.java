package shabanzadeh2025.util;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Dene Ringuette
 */

public interface CalcFunc extends DiffFunc, AntiFunc
{

	public static CalcFunc exp()
	{
		return new CalcFunc()
		{
			
			@Override
			public DoubleUnaryOperator anti()
			{
				return Math::exp;
			}
	
			@Override
			public double applyAsDouble(double arg)
			{
				return Math.exp(arg);
			}
			
			@Override
			public DoubleUnaryOperator deriv()
			{
				return Math::exp;
			}
	
		};
	}
	
	
	
}
