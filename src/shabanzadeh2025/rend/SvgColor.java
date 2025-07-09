package shabanzadeh2025.rend;

import java.awt.Color;

/**
 * SVG color utility.
 * 
 * @author Dene Ringuette
 */

public class SvgColor 
{
	public static String make(Color color)
	{
		return SvgColor.make(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static String make(int red, int green, int blue)
	{
		return "rgb(" + red + "," + green + "," + blue + ")";
	}
	
	public static String[] makeAll(int[] rgb)
	{
		String[] array = new String[rgb.length];
		for(int i = 0 ; i < rgb.length; i++)
			array[i] = SvgColor.make(rgb[i]);
		return array;
	}
	
	public static String make(int rgb)
	{
		return SvgColor.make(new Color(rgb));
	}
	
	public static String[] getRandomHueDistinct(final int groups, final float saturation, final float brightness)
	{
		return getDistinct(groups, (float)Math.random(), saturation, brightness);
	}
	
	public static String[] getDistinct(final int groups, final float hue0, final float saturation, final float value)
	{
		final double golden_ratio_conjugate = 0.618033988749895;
		
		final int[] color = new int[groups];
		
		double hue = hue0;
		for(int k = 0; k < groups; k++)
		{
			color[k] = Color.HSBtoRGB((float)hue, saturation, value);
			hue += golden_ratio_conjugate;
			hue %= 1.0;
		}
				
		return makeAll(color);
	}
	
	public static String[] getDefaultHSVDistinct(final int groups)
	{
		return getDistinct(groups, 0, 0.6f, 0.7f);
	}
}
