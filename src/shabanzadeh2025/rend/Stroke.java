package shabanzadeh2025.rend;

/**
 * Features of curve rendering.
 * 
 * @author Dene Ringuette
 */

public class Stroke 
{
	public final String color;
	
	public final double width;
	
	public final double alpha;
	
	public final LineJoin join;
			
	public final LineCap end;
	
	public static enum LineCap
	{
		BUTT("butt", true),
		ROUND("round", false),
		SQUARE("square", false);
		
		final String value;
		final boolean isDefault;
		
		private LineCap(String name, boolean dflt)
		{
			this.value = name;
			this.isDefault = dflt;
		}
		
		public boolean isDefaultSVG()
		{
			return this.isDefault;
		}
	}
	
	public static enum LineJoin
	{
		ARCS("arcs", false),
		BEVEL("bevel", false),
		MITER("miter", true),
		MITER_CLIP("miter-clip", false),
		ROUND("round", false);
		
		final String value;
		final boolean isDefault;
		
		private LineJoin(String name, boolean dflt)
		{
			this.value = name;
			this.isDefault = dflt;
		}
		
		public boolean isDefaultSVG()
		{
			return this.isDefault;
		}
	}
	
	public Stroke()
	{
		this("black", 1.0, 1.0, LineJoin.MITER, LineCap.BUTT);
	}
	
	public Stroke(String color, double alpha, double width, LineJoin join, LineCap cap)
	{
		this.color = color;
		this.alpha = alpha;
		this.width = width;
		this.join = join;
		this.end = cap;
	}
	
	public Stroke changeColor(String color)
	{
		return new Stroke(color, this.alpha, this.width, this.join, this.end);
	}
	
	public Stroke changeAlpha(double alpha)
	{
		return new Stroke(this.color, alpha, this.width, this.join, this.end);
	}
	
	public Stroke changeWidth(double width)
	{
		return new Stroke(this.color, this.alpha, width, this.join, this.end);
	}
	
	public Stroke changeJoin(LineJoin join)
	{
		return new Stroke(this.color, this.alpha, this.width, join, this.end);
	}
	
	public Stroke changeEnd(LineCap end)
	{
		return new Stroke(this.color, this.alpha, this.width, this.join, end);
	}

	public static Stroke axis()
	{
		return Stroke.axis(1.0);
	}
	
	public static Stroke axis(double width)
	{
		return Stroke.axis("black", width);
	}
	
	public static Stroke axis(String color, double width)
	{
		return Stroke.axis(color, 1.0, width);
	}
	
	public static Stroke axis(String color, double alpha, double width)
	{
		return new Stroke(color, alpha, width, LineJoin.MITER, LineCap.SQUARE);
	}
	
	public static Stroke data()
	{
		return Stroke.data(1.0);
	}
	
	public static Stroke data(double width)
	{
		return Stroke.data("black", width);
	}
	
	public static Stroke data(String color, double width)
	{
		return Stroke.data(color, 1.0, width);
	}
	
	public static Stroke data(String color, double alpha, double width)
	{
		return new Stroke(color, alpha, width, LineJoin.ROUND, LineCap.BUTT);
	}
}
