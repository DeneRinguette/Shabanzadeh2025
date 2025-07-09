package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public enum Optimization implements PlaneFunc
{	
	MAXIMIZE(-1, Double.NEGATIVE_INFINITY)
	{
		@Override
		public double apply(double arg0, double arg1) 
		{
			return Math.max(arg0, arg1);
		}

		@Override
		public double arg(double arg0, double val0, double arg1, double val1) 
		{
			return (val0 < val1) ? arg1 : arg0;
		}

		@Override
		public boolean fi(double ref, double arg) 
		{
			return ref < arg;
		}
	},
	
	MINIMIZE(+1, Double.POSITIVE_INFINITY)
	{
		@Override
		public double apply(double arg0, double arg1) 
		{
			return Math.min(arg0, arg1);
		}
		
		@Override
		public double arg(double arg0, double val0, double arg1, double val1) 
		{
			return (val1 < val0) ? arg1 : arg0;
		}

		@Override
		public boolean fi(double ref, double arg) 
		{
			return arg < ref;
		}
	};
	
	public final double antithetical;
	
	public final int definiteness;
	
	private Optimization(int def, double anti)
	{
		this.antithetical = anti;
		this.definiteness = def;
	}
	
	public abstract double arg(double arg0, double val0, double arg1, double val1);
	
	public abstract boolean fi(double ref, double arg);
}
