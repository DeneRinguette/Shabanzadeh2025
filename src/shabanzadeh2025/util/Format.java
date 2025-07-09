package shabanzadeh2025.util;

import java.util.Formatter;

/**
 * @author Dene Ringuette
 */

public class Format 
{
	public static String decimals(double value, int decimals)
	{
		StringBuilder sb = new StringBuilder();
		Formatter form = new Formatter(sb); 
		form.format("%."+ decimals + "f", value);
		form.close();
		return sb.toString();
	}
	
	public static String scientific(double value, int decimals)
	{
		StringBuilder sb = new StringBuilder();
		Formatter form = new Formatter(sb); 
		form.format("%."+ decimals + "e", value);
		form.close();
		return sb.toString();
	}	
}
