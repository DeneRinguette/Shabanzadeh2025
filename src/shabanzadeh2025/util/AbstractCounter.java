package shabanzadeh2025.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 * @param <E>
 */

public abstract class AbstractCounter<E> implements Counter<E>, ComGrpUndAdd<AbstractCounter<E>>
{
	private long total;
	
	protected final Map<E, Count> map;
	
	protected AbstractCounter(Map<E, Count> arg0) 
	{
		this.map = arg0;
	}
	
	@Override
	public long count(E arg)
	{
		this.total++;
		Count i = this.map.get(arg);
		if(i == null)
		{
			i = new Count(0);
			this.map.put(arg, i);
		}
		i.e++;
		return i.e;
	}
	
	@Override
	public long count(E arg, int num)
	{
		this.total += num;
		Count i = this.map.get(arg);
		if(i == null)
		{
			i = new Count(0);
			this.map.put(arg, i);
		}
		i.e += num;
		return i.e;
	}
	
	@Override
	public void count(E[] args)
	{
		for(E i : args)
			this.count(i);
	}
	
	@Override
	public void count(Iterable<? extends E> args)
	{
		for(E i : args)
			this.count(i);
	}
	
	@Override
	public long debit(E arg)
	{
		this.total--;
		Count i = this.map.get(arg);
		if(i == null)
		{
			i = new Count(0);
			this.map.put(arg, i);
		}
		i.e--;
		return i.e;
	}
	
	@Override
	public long debit(E arg, int num)
	{
		this.total -= num;
		Count i = this.map.get(arg);
		if(i == null)
		{
			i = new Count(0);
			this.map.put(arg, i);
		}
		i.e -= num;
		return i.e;
	}
	
	@Override
	public void debit(Iterable<? extends E> args) 
	{
		for(E i : args)
			this.debit(i);
	}
	
	public boolean isZero()
	{
		if(this.total != 0)
			return false;
		for(Count i : this.map.values())
			if(i.e != 0)
				return false;
		return true;
	}

	@Override
	public E least()
	{
		long min = Long.MAX_VALUE;
		long value;
		E least = null;
		for(Entry<E, Count> i : this.map.entrySet())
		{
			value = i.getValue().e;
			if(value < min)
			{
				min = value;
				least = i.getKey();
			}
		}
		return least;
	}

	@Override
	public long max()
	{
		long max = Long.MIN_VALUE;
		for(Count i : this.map.values())
			if(max < i.e)
				max = i.e;
		return max;
	}

	@Override
	public long min() 
	{
		long min = Long.MAX_VALUE;
		for(Count i : this.map.values())
			if(i.e < min)
				min = i.e;
		return min;
	}

	@Override
	public E most()
	{
		long max = Long.MIN_VALUE;
		long value;
		E most = null;
		for(Entry<E, Count> i : this.map.entrySet())
		{
			value = i.getValue().e;
			if(max < value)
			{
				max = value;
				most = i.getKey();
			}
		}
		return most;
	}
	
	@Override
	public E removeMost()
	{
		E most = this.most();
		this.total -= this.map.remove(most).e;
		return most;
	}

	@Override
	public long number(E arg)
	{
		Count i = this.map.get(arg);
		if(i == null)
			return 0;
		else
			return i.e;
	}
	
	@Override
	public double probability(E arg) 
	{
		return (double)this.number(arg)/this.total();
	}
	
	@Override
	public void set(AbstractCounter<E> that) 
	{
		this.map.clear();
		for(Entry<E, Count> i : that.map.entrySet())
			this.map.put(i.getKey(), i.getValue().copy());
		this.total = that.total;
	}

	@Override
	public void add(AbstractCounter<E> that) 
	{
		Count val;
		for(Entry<E, Count> i : that.map.entrySet())
		{
			val = this.map.get(i.getKey());
			if(val == null)
			{
				val = new Count(0);
				this.map.put(i.getKey(), val);
			}
			val.e += i.getValue().e;
		}
		this.total += that.total;
	}

	@Override
	public void neg() 
	{
		for(Count i : this.map.values())
			i.e = - i.e;
		this.total = - this.total;
	}

	@Override
	public void sub(AbstractCounter<E> that) 
	{
		Count val;
		for(Entry<E, Count> i : that.map.entrySet())
		{
			val = this.map.get(i.getKey());
			if(val == null)
			{
				val = new Count(0);
				this.map.put(i.getKey(), val);
			}
			val.e -= i.getValue().e;
		}
		this.total -= that.total;
	}

	@Override
	public void zero() 
	{
		this.map.clear();
		this.total = 0;
	}

	@Override
	public long strike(E arg)
	{
		Count i = this.map.get(arg);
		if(i == null)
			return 0;
		if(i.e == 0)
		{
			this.map.remove(arg);
			return 0;
		}
		this.total--;
		i.e--;
		return i.e;
	}

	@Override
	public long strike(E arg, int num) 
	{
		Count i = this.map.get(arg);
		if(i == null)
			return 0;
		if(i.e <= num)
		{
			this.map.remove(arg);
			return 0;
		}
		this.total -= num;
		i.e -= num;
		return i.e;
	}

	@Override
	public void strike(Iterable<? extends E> args) 
	{
		for(E i : args)
			this.strike(i);
	}

	@Override
	public long total() 
	{
		return this.total;
	}
	
	@Override
	public void putProbabilities(Map<E, Double> map)
	{
		for(Entry<E, Count> i : this.map.entrySet())
			map.put(i.getKey(), (double)i.getValue().e/this.total());
	}

	public void clear()
	{
		this.map.clear();
		this.total = 0;
	}
	
	@Override
	public boolean add(E arg0) 
	{
		Count val = this.map.get(arg0);
		if(val == null)
		{
			this.map.put(arg0, new Count(0));
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) 
	{
		boolean changed = false;
		for(E i : arg0)
			if(this.add(i))
				changed = true;
		return changed;
	}

	@Override
	public boolean contains(Object arg0) 
	{
		return this.map.containsKey(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) 
	{
		return this.map.keySet().containsAll(arg0);
	}

	@Override
	public boolean isEmpty() 
	{
		return this.map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() 
	{
		final Iterator<Entry<E,Count>> iterator = this.map.entrySet().iterator();
		
		return 
			new Iterator<E>()
			{
				Entry<E, Count> temp;
				
				@Override
				public boolean hasNext() 
				{
					return iterator.hasNext();
				}

				@Override
				public E next() 
				{
					this.temp = iterator.next();
					return this.temp.getKey();
				}

				@Override
				public void remove() 
				{
					iterator.remove();
					total -= this.temp.getValue().e;
				}
			};
	}

	@Override
	public boolean remove(Object arg0) 
	{
		Count val = this.map.remove(arg0);
		if(val == null)
			return false;
		this.total -= val.e;
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) 
	{
		boolean changed = this.map.keySet().removeAll(arg0);
		if(changed)
		{
			this.total = 0;
			for(Count i : this.map.values())
				this.total += i.e;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) 
	{
		boolean changed = this.map.keySet().retainAll(arg0);
		if(changed)
		{
			this.total = 0;
			for(Count i : this.map.values())
				this.total += i.e;
		}
		return changed;
	}

	@Override
	public int size() 
	{
		return this.map.size();
	}

	@Override
	public Object[] toArray() 
	{
		return this.map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) 
	{
		return this.map.keySet().toArray(arg0);
	}
	
	@Override
	public boolean clearExplicitZeros()
	{
		boolean changed = false;
		long val;
		Iterator<Entry<E, Count>> iterator = this.map.entrySet().iterator();
		while(iterator.hasNext())
		{
			val = iterator.next().getValue().e;
			if(val == 0)
			{
				iterator.remove();
				this.total -= val;
				changed = true;
			}
		}
		return changed;
	}
	
	public boolean hasValidProbabilities()
	{
		for(Count i : this.map.values())
			if(i.e < 0)
				return false;
		return true;
	}
	
	public String toString()
	{
		String out = "[";
		for(Entry<E, Count> e : this.map.entrySet())
		{
			out = out + "(" + e.getKey() + "->" + e.getValue().e + ")";
		}
		out = out + "]";
		return out;
	}
	
	public List<Label<Double>> toNamedValueList()
	{
		List<Label<Double>> array = new ArrayList<Label<Double>>(this.map.size());
		
		for(Map.Entry<E, Count> e : this.map.entrySet())
			array.add(new Label<Double>((double)e.getValue().e, (String)e.getKey()));
		
		return array;
	}
	
	
}
