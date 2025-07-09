package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class NewtonOptimization 
{
	public static final int MAXIT = 1265;
	
	public static Tensor run(Optimization optimization, HessFunc function, Tensor initial, double precision)
	{
		return run(optimization, function, initial, precision, NewtonOptimization.MAXIT);
	}
	
	public static Tensor run(Optimization optimization, HessFunc function, Tensor initial, double precision, int maxit)
	{
		Tensor param = initial.copy();
		double change = Double.POSITIVE_INFINITY;
		int iteration = 0;
		do
		{
			Tensor start = param;
			param = param.copy();
			
			double value = function.apply(2, param);
			Tensor grad = function.grad();
			Tensor hess = function.hess();
			
			if(hess.def(optimization.definiteness))
			{
				hess.inv();
				param.sub(TensorTools.mult(hess, grad));
			}
			else
			{
				double second = TensorTools.quadProd(grad, hess, grad).rGet(0);
				if(second * optimization.definiteness > 0)
				{
					grad.div(second);
					param.add(-optimization.definiteness, grad);
				}
				else
				{
					grad.normalize();
					grad.mult(precision);
					Tensor temp = param;
					double last;
					double current = value;
					do
					{	
						param = temp;
						temp = param.copy();
						grad.mult(2.0);
						temp.add(-optimization.definiteness, grad);
						last = current;
						current = function.apply(0, temp);
					}
					while(last > current);
				}
			}
			
			start.sub(param);
			change = start.hypot();
			iteration++;
		}
		while(precision < change && iteration < maxit);
		return param;
	}
}