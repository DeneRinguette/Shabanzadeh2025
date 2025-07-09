package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public abstract class HessFunc extends GradFunc
{
	public Tensor hess(Tensor args)
	{
		this.apply(2, args);
		return this.hess();
	}
	
	public abstract Tensor hess();
	
	public int depth()
	{
		return 2;
	}
}
