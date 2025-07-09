package shabanzadeh2025.rend;

/**
 * Text Orientation.
 * 
 * @author Dene Ringuette
 */

public class TextOrientation
{
	private final String rotation;
	
	public TextOrientation(double rotation)
	{
		this.rotation = "" + rotation;;
	}
		
	public String rotation()
	{
		return "rotate(" + this.rotation + ")";
	}
	
	public String rotation(String x, String y)
	{
		return "rotate(" + this.rotation + " " + x + " " + y + ")";
	}
	
	public String toString()
	{
		return rotation();
	}
}
