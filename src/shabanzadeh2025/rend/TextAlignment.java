package shabanzadeh2025.rend;

/**
 * Merges horizontal and vertical alignment for text labels.
 * Converters text centeric terms to point relative terms for graph labeling.
 * 
 * @author Dene Ringuette
 */

public enum TextAlignment 
{
	LEFT("end", "central"),
	RIGHT("start", "central"),
	TOP("middle", "bottom"),
	BOTTOM("middle", "top"),
	Y_AXIS("end", "top"),
	OVER("middle", "central"),
	TOP_LEFT("end", "bottom"),
	TOP_RIGHT("start", "bottom"),
	BOTTOM_LEFT("end", "top"),
	BOTTOM_RIGHT("start", "top");
	
	private final String horizontal;
	private final String vertical;
	
	private TextAlignment(String h, String v)
	{
		this.horizontal = h;
		this.vertical = v;
	}
	
	public String horizontal()
	{
		return this.horizontal;
	}

	public String vertical()
	{
		return this.vertical;
	}
}
