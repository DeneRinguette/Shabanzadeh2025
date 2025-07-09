package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public interface Spitter extends Moments, Redactable 
{
	public double mean();
	
	public double[] moments(double x0);
	
	public void add(double w, double x);
	
	public void sub(double w, double x);
	
	public void add(double x);
	
	public void sub(double x);
	
	public boolean permisible(double x);
	
	public boolean permisible(double w, double x);	
}
