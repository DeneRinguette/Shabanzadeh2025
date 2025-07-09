package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface Field<T> extends ComRing<T>
{
	void div(T arg);
	void rec();
}
