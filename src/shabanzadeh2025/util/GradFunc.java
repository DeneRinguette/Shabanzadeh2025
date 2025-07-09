package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public abstract class GradFunc implements TensorFunc
{
	public Tensor grad(Tensor args)
	{
		this.apply(1, args);
		return this.grad();
	}
	
	public abstract Tensor grad();
	
	public int depth()
	{
		return 1;
	}
}
