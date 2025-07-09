package shabanzadeh2025.util;

import java.util.function.Supplier;

/**
 * @author Dene Ringuette
 *
 * @param <T>
 */

public interface ZeroSupplier<T> extends Supplier<T> 
{
	T get();
}