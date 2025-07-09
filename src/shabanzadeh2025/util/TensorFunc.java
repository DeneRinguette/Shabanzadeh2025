package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public interface TensorFunc
{	
	double apply(int depth, Tensor arg);
	
	int depth();	
}
