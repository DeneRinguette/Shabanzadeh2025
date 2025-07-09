package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface NonComRing<T> extends Ring<T>
{	
	void multL(T arg);
	
	void multR(T arg);
}
