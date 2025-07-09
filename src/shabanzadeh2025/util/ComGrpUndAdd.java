package shabanzadeh2025.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public interface ComGrpUndAdd<T> extends Addable<T>
{
	void sub(T that);
	
	void neg();
	
	void zero();
		
	public static <K, V extends ComGrpUndAdd<V>> void sub(Map<K, V> dst, Map<K, V> src)
	{
		for(Entry<K, V> entry : src.entrySet())
		{
			V a = dst.get(entry.getKey());
			V b = entry.getValue();
			if(a == null)
			{
				V c = b.copy();
				c.neg();
				dst.put(entry.getKey(), c);
			}
			else
				a.sub(b);
		}
	}
}
