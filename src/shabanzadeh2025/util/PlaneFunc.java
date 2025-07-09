package shabanzadeh2025.util;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * @author Dene Ringuette
 */

public interface PlaneFunc
{
	double apply(double arg0, double arg1);
	
	public static PlaneFunc bind(DoubleConsumer input0, DoubleConsumer input1, DoubleSupplier output)
	{
		return (arg0, arg1) -> {input0.accept(arg0); input1.accept(arg1); return output.getAsDouble();};
	}
}
