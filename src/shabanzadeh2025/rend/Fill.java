package shabanzadeh2025.rend;

/**
 * Features of area rendering.
 * 
 * @author dene5
 *
 */

public class Fill 
{
	public final boolean link;
	public final String color;	
	public final double alpha;
	
	public Fill()
	{
		this("none", 1.0);
	}
	
	public Fill(double alpha)
	{
		this("black", alpha);
	}
	
	public Fill(String color)
	{
		this(color, 1.0);
	}
	
	public Fill(String color, double alpha)
	{
		this(color, alpha, false);
	}
	
	private Fill(String color, double alpha, boolean link)
	{
		this.color = color;
		this.alpha = alpha;
		this.link = link;
	}
	
	public static Fill link(String id)
	{
		return new Fill(id, Double.NaN, true);
	}
	
	
	public Fill changeColor(String color)
	{
		return new Fill(color, this.alpha);
	}
	
	public Fill changeAlpha(double alpha)
	{
		return new Fill(this.color, alpha);
	}
}
