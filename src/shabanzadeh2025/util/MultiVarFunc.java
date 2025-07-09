package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public interface MultiVarFunc
{
	double apply(double... args);

	static MultiVarFunc linear(double a, MultiVarFunc f, double b, MultiVarFunc g)
	{
		return (arg) -> a * f.apply(arg) + b * g.apply(arg);
	}
	
	static MultiVarFunc quotient(MultiVarFunc f, MultiVarFunc g)
	{
		return (arg) -> (f.apply(arg) / g.apply(arg));
	}

	static MultiVarFunc product(MultiVarFunc f, MultiVarFunc g)
	{
		return (arg) -> (f.apply(arg) * g.apply(arg));
	}
	
	static double[] applyAll(MultiVarFunc f, double[]... a)
	{
		final int n = a.length;
		double[] b = new double[n];
		for(int i = 0; i < n; i++)
			b[i] = f.apply(a[i]);
		return b;
	}
}
