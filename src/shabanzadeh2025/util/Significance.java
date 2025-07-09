package shabanzadeh2025.util;

import java.text.DecimalFormat;

import org.apache.commons.math3.special.Erf;

/**
 * @author Dene Ringuette
 */

public class Significance 
{
	public static final String NON = "n.s.";    
	
	public static double ONE_STAR = 0.05;
	
	public static double TWO_STAR = 0.01;
	
	public static double THREE_STAR = 0.001;
	
	public static double FOUR_STAR = 0.0001;
	
	public static String toStars(double p)
	{
		if(Significance.ONE_STAR <= p)
			return Significance.NON; /* No significance */
		if(Significance.TWO_STAR <= p)
			return "*"; /* 0.01 <= p < 0.05 */
		if(Significance.THREE_STAR <= p)
			return "**"; /* 0.001 <= p < 0.01 */
		if(Significance.FOUR_STAR <= p) 
			return "***"; /* 0.0001 <= p < 0.001 */
		return "****"; /* p < 0.0001 */
	}
	
	public static String toStars3(double p)
	{
		if(Significance.ONE_STAR <= p)
			return Significance.NON; /* No significance */
		if(Significance.TWO_STAR <= p)
			return "*"; /* 0.01 <= p < 0.05 */
		if(Significance.THREE_STAR <= p)
			return "**"; /* 0.001 <= p < 0.01 */
		return "***"; /* p < 0.001 */		
	}
	
	public static String toFourDecimals(double p)
	{
		if(1 < p)
			throw new IllegalArgumentException("Probability cannot exceed a value of unity.");
		
		if(p < 0.0001)
			return "<0.0001";
		else
			return new DecimalFormat("0.0000").format(p);
	}
		
	public static double fromSigma(double sigma)
	{
		return Erf.erfc(sigma / Math.sqrt(2));
	}
}
