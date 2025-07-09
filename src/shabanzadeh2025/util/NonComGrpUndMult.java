package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface NonComGrpUndMult<T> extends GrpUndMult<T>
{
	void multL(T arg);
	
	void multR(T arg);
}
