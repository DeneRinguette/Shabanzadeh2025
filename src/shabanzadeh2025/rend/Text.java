package shabanzadeh2025.rend;

/**
 * Text formatting.
 * 
 * @author Dene Ringuette
 */

public class Text 
{
	private final TextAlignment alignment;
	private final TextOrientation transform;
	private final String style;
	private final double size;
	private final String color;
	
	public Text(double size)
	{
		this(TextAlignment.LEFT, size);
	}
	
	public Text(TextAlignment alignment, double size)
	{
		this(alignment, "Arial", size);
	}
	
	public Text(TextAlignment alignment, String style, double size)
	{
		this(alignment, style, size, null);
	}
	
	public Text(TextAlignment alignment, String style, double size, TextOrientation transform)
	{
		this(alignment, style, size, "black", transform);
	}
	
	public Text(TextAlignment alignment, String style, double size, String color, TextOrientation transform)
	{
		this.alignment = alignment;
		this.transform = transform;
		this.style = style;
		this.size = size;
		this.color = color;
	}
	
	public Text changeAlignment(TextAlignment alignment)
	{
		return new Text(alignment, this.style, this.size, this.color, this.transform);
	}
	
	public Text changeStyle(String style)
	{
		return new Text(this.alignment, style, this.size, this.color, this.transform);
	}
	
	public Text changeSize(int size)
	{
		return new Text(this.alignment, this.style, size, this.color, this.transform);
	}
	
	public Text changeColor(String color)
	{
		return new Text(this.alignment, this.style, this.size, color, this.transform);
	}
	
	public Text changeTransform(TextOrientation transform)
	{
		return new Text(this.alignment, this.style, this.size, this.color, transform);
	}
	
	public double size()
	{
		return this.size;
	}
	
	public String color()
	{
		return this.color;
	}
	
	public String style()
	{
		return this.style;
	}
	
	public boolean hasTransform()
	{
		return this.transform != null;
	}
	
	public TextOrientation transform()
	{
		return this.transform;
	}
	
	public TextAlignment alignment()
	{
		return this.alignment;
	}
}
