package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface NormVecSpc <T> extends VecSpc<T> 
{
	double norm();
	
	double norm2();
	
	void normalize();
}
