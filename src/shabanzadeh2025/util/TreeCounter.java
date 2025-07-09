package shabanzadeh2025.util;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 * @param <E>
 */

public class TreeCounter<E> extends AbstractCounter<E>
{

	public TreeCounter() 
	{
		super(new TreeMap<E, Count>());
	}

	@Override
	public TreeCounter<E> copy() 
	{
		TreeCounter<E> copy = new TreeCounter<E>();
		copy.set(this);
		return copy;
	}
	
	public E firstKey()
	{
		return ((TreeMap<E, Count>)this.map).firstKey();
	}
	
	public E lastKey()
	{
		return ((TreeMap<E, Count>)this.map).lastKey();
	}
	
	public void putFractionalRanks(Map<E, Double> map, Order order)
	{
		long last = 0;
		long rank = 0;
		NavigableMap<E, Count> smap = (TreeMap<E, Count>)this.map;
		if(order == Order.DESCENDING)
			smap = smap.descendingMap();
		
		for(Entry<E, Count> i : smap.entrySet())
		{
			last = rank;
			long step = i.getValue().e;
			
			if(step > 0)
			{
				rank += step;
				map.put(i.getKey(), (rank+last+1)*0.5);
			}
			else if(step < 0)
				throw new IllegalArgumentException("Cannot rank with negative counts.");
		}
	}
	
	@Override
	public TreeMap<E, Long> toMap() 
	{
		TreeMap<E, Long> map = new TreeMap<E, Long>();
		for(Entry<E, Count> entry : this.map.entrySet())
			map.put(entry.getKey(), entry.getValue().get());
		return map;
	}

	@Override
	public TreeCounter<E> get() 
	{
		return new TreeCounter<E>();
	}
}