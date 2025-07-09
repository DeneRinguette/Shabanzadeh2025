package shabanzadeh2025.rend;

/**
 * Data space of graph.
 * 
 * @author Dene Ringuette
 */

public class Span
{
	public final double xMax;
	
	public final double yMax;
	
	public final double xMin;
	
	public final double yMin;
	
	public Span(double xMin, double xMax, double yMin, double yMax)
	{
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public static Span origin(double radius)
	{
		return new Span(-radius, radius, -radius, radius);
	}
	
	public static Span bar(int groups, double vMin, double vMax)
	{
		return new Span(-0.75, groups-0.25, vMin, vMax);
	}
	
	public static Span positive(double x, double y)
	{
		return new Span(0, x, 0, y);
	}
	
	public double normX(double x)
	{
		return (x - this.xMin)/this.rangeX();	
	}

	public double normY(double y)
	{
		return (y - this.yMin)/this.rangeY();
	}

	public double normDiffX(double deltaX)
	{
		return deltaX/this.rangeX();	
	}
	
	public double normDiffY(double deltaY)
	{
		return deltaY/this.rangeY();
	}
	
	public double rangeX()
	{
		return this.xMax - this.xMin;
	}
	
	public double rangeY()
	{
		return this.yMax - this.yMin;
	}		
}
