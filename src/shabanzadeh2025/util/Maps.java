package shabanzadeh2025.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

/**
 * @author Dene Ringuette
 */

public class Maps 
{
	public static <K, V> Map<V, Set<K>> groupFromValue(Map<K, V> map)
	{
		return groupFromValue(map, () -> new TreeSet<K>());
	}

	public static <K, V> Map<V, Set<K>> groupFromValue(Map<K, V> map, Supplier<Set<K>> init)
	{
		Map<V, Set<K>> index = new TreeMap<V, Set<K>>();
		for(Map.Entry<K, V> e : map.entrySet())
		{
			Set<K> set = index.get(e.getValue());
			if(set == null)
			{
				set = new TreeSet<K>();
				index.put(e.getValue(), set);
			}
			set.add(e.getKey());
		}
		return index;
	}
	
	public static <K, V, T> Map<K, Double> putApply(Map<K, V> map_a, V def_a, Map<K, T> map_b, T def_b, Map<K, Double> dst, ToDoubleBiFunction<V, T> func)
	{
		for(Entry<K, V> entry : map_a.entrySet())
		{
			K key = entry.getKey();
			V value_a = entry.getValue();
			T value_b = map_b.get(key);
			
			if(value_a == null)
				value_a = def_a;
			
			if(value_b == null)
				value_b = def_b;
			
			dst.put(key, func.applyAsDouble(value_a, value_b));
		}
		
		for(Entry<K, T> entry : map_b.entrySet())
		{
			K key = entry.getKey();
			V value_a = map_a.get(key);
			T value_b = entry.getValue();
			
			if(value_a == null)
				value_a = def_a;
			
			if(value_b == null)
				value_b = def_b;
				
			dst.put(key, func.applyAsDouble(value_a, value_b));
		}
		return dst;
	}
}
