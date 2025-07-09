package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public interface Moments extends Dim
{
	public double[] moments();
	
	public double[] normalizedMoments();
	
	public int order();
	
	public double weight();
	
	public boolean isFinite();
}
