package shabanzadeh2025.rend;

/**
 * Dash style.
 * 
 * @author Dene Ringuette
 */

public class Dash
{
	public final String array;
	public final int offset;
	
	public Dash()
	{
		this("2 1", 0);
	}
	
	public Dash(String array, int offset)
	{
		this.array = array;
		this.offset = offset;
	}
	
	public Dash changeArray(String array)
	{
		return new Dash(array, this.offset);
	}
	
	public Dash changeOffset(int offset)
	{
		return new Dash(this.array, offset);
	}

}
