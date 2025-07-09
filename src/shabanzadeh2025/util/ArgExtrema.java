package shabanzadeh2025.util;

import java.util.Map;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Dene Ringuette
 * @param <T>
 */

public abstract class ArgExtrema<T> 
{
	private long count = 0;
	
	private double val_at_ext;
	
	private T arg_of_ext = null;
	
	private List<T> degenerates = null;
	
	private final boolean last;
	
	private long degeneracy = 0L;
	
	public ArgExtrema(boolean forwardBias, double initial)
	{
		this.last = forwardBias;
		this.val_at_ext = initial;
	}
	
	public void storeDegenerates()
	{
		this.degenerates = new LinkedList<T>();
	}
	
	public void update(T arg, double val)
	{
		if(sufficientForUpdate(val))
		{
			this.degeneracy = 1L;
			this.arg_of_ext = arg;
			this.val_at_ext = val;
			if(this.degenerates != null)
			{
				this.degenerates.clear();
				this.degenerates.add(arg);
			}
		}
		else if(val == this.val_at_ext)
		{
			this.degeneracy += 1L;
			if(this.last)
				this.arg_of_ext = arg;
			if(this.degenerates != null)
				this.degenerates.add(arg);
		}
		this.count++;
	}
	
	public abstract boolean sufficientForUpdate(double value);
	
	public void update(Map<? extends T, ? extends Number> map)
	{
		for(Entry<? extends T, ? extends Number> entry : map.entrySet())
			this.update(entry.getKey(), entry.getValue().doubleValue());
	}
	
	public T get()
	{
		return this.arg_of_ext;
	}
	
	public T[] getDegenerates(T[] array)
	{
		return this.degenerates.toArray(array);
	}
	
	public <S extends Collection<T>> S addDegenerates(S col)
	{
		for(T arg : this.degenerates)
			col.add(arg);
		return col;
	}
	
	public double value()
	{
		return this.val_at_ext;
	}
	
	public long count() 
	{
		return count;
	}
	
	public long degeneracy()
	{
		return this.degeneracy;
	}
	
	protected abstract String label();
	
	public String toString()
	{
		return "[arg" + this.label() + "=" + this.arg_of_ext + "; value="+ this.val_at_ext +" ; degen=" + this.degeneracy + "; count=" +  this.count + "]";
	}
}
