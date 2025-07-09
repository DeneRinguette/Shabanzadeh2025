package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public enum Order 
{
	ASCENDING
	{

		@Override
		public boolean is(double... args) 
		{
			return Meth.isAscending(args);
		}

		@Override
		public String info() 
		{
			return "If ascending, the smallest value is ranked as 1.";
		}
		
	},
	DESCENDING
	{

		@Override
		public boolean is(double... args) 
		{
			return Meth.isDescending(args);
		}

		@Override
		public String info() 
		{
			return "If descending, the largest value is ranked as 1.";
		}
		
	};
	
	public abstract String info();
	
	public abstract boolean is(double... args);
}
