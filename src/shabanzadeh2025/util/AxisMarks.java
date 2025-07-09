package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class AxisMarks 
{
	private final Domain dom;
	private final Increment inc;
	
	public AxisMarks(Domain dom, Increment inc)
	{
		this.dom = dom;
		this.inc = inc;
	}
	
	public double[] getPoints()
	{
		if(this.dom.test(this.inc.origin()))
		{ 
			final double s = inc.increment();
			final double xo = inc.origin();
			double x;
			
			// count elements inside domain
			
			// the positive
			int n = 0;
			x = xo;
			while(dom.test(x += s))
				n += 1;
			
			// the negative
			int m = 0;
			x = xo;
			while(dom.test(x -= s))
				m += 1;
			
			// total
			final int c = n+m+1;
			// index of origin
			final int o = m;
			
			// assign elements to array
			final double[] points = new double[c];
			
			points[o] = xo;
			
			// the positive
			int i = o;
			x = xo;
			while(dom.test(x += s))
				points[i += 1] = x;
			
			// the negative
			int j = o;
			x = xo;
			while(dom.test(x -= s))
				points[j -= 1] = x;
			
			return points;
		}
		else
			return null;
	}
}
