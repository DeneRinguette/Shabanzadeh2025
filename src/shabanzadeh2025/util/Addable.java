package shabanzadeh2025.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface Addable<T> extends Setable<T>
{
	
	void add(T arg);
	
	public static <K, V extends Addable<V>> void add(Map<K, V> dst, Map<K, V> src)
	{
		for(Entry<K, V> entry : src.entrySet())
		{
			V a = dst.get(entry.getKey());
			V b = entry.getValue();
			if(a == null)
				dst.put(entry.getKey(), b.copy());
			else
				a.add(b);
		}
	}
	
}
