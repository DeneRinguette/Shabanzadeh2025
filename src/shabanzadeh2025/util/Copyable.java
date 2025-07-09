package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface Copyable<T> extends ZeroSupplier<T>
{
	T copy();
}
