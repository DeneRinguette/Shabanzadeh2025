package shabanzadeh2025.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dene Ringuette
 * @param <E>
 */

public interface Counter<E> extends Set<E>
{
	class Count
	{
		
		public long e;
		
		public Count()
		{
			this.e = 0;
		}
		
		public Count(long arg)
		{
			this.e = arg;
		}
		
		public Count copy()
		{
			return new Count(this.e);
		}
		
		public long get()
		{
			return this.e;
		}
		
		public void set(long value)
		{
			this.e = value;
		}
		
	}
		
	long count(E arg);
	
	long count(E arg, int num);
	
	void count(E[] args);
	
	void count(Iterable<? extends E> args);
	
	long debit(E arg);
	
	long debit(E arg, int num);
	
	void debit(Iterable<? extends E> args);
	
	E least();
	
	long max();
	
	long min();
	
	E most();
	
	E removeMost();
	
	long number(E arg);
	
	double probability(E arg);
	
	void putProbabilities(Map<E, Double> map);
	
	long strike(E arg);
	
	long strike(E arg, int num);
	
	void strike(Iterable<? extends E> args);
	
	long total();
	
	boolean clearExplicitZeros();
	
	boolean hasValidProbabilities();
	
	List<Label<Double>> toNamedValueList();
	
	Map<E, Long> toMap();
}
