package shabanzadeh2025.util;

import java.util.Iterator;

/**
 * @author Dene Ringuette
 */

public class IntegerHistogram extends Histogram implements Iterable<IntegerHistogram.Gap> 
{
	public static class Gap
	{	
		private final int start, stop;
		private final long count;
		
		public Gap(int start, int stop, long count)
		{
			this.start = start;
			this.stop = stop;
			this.count = count;
		}

		public long count() 
		{
			return count;
		}

		public int start() 
		{
			return start;
		}

		public int stop() 
		{
			return stop;
		}
		
		public String toString()
		{
			return "[" + this.start + "," + this.stop + ") -> " + this.count;
		}
	}
	
	private final int start;
	
	private final int stop;
	
	private final int gap;
	
	public IntegerHistogram(int start, int stop, int groups)
	{
		super(groups);
		if(stop < start)
			throw new IllegalArgumentException();
		this.start = start;
		this.stop = stop;
		final int range = stop - start;
		if(range % groups != 0)
			throw new IllegalArgumentException("Number of groups must correspond to range.");
		this.gap = range/groups; 
	}
	
	public IntegerHistogram(int start, int stop)
	{
		super(stop-start);
		if(stop < start)
			throw new IllegalArgumentException("Start cannot be before stop.");
		this.start = start;
		this.stop = stop;
		this.gap = 1;
	}
	
	public static IntegerHistogram uint16()
	{
		return new IntegerHistogram(0, 65536);
	}
	
	public static IntegerHistogram uint14()
	{
		return new IntegerHistogram(0, 16384);
	}
	
	public static IntegerHistogram uint12()
	{
		return new IntegerHistogram(0, 4096);
	}
	
	public static IntegerHistogram uint10()
	{
		return new IntegerHistogram(0, 1024);
	}
	
	public static IntegerHistogram uint8()
	{
		return new IntegerHistogram(0, 256);
	}
	
	public int[] integerCounts()
	{
		final int n = this.counts.length;
		int[] intCount = new int[n];
		for(int i = 0; i < n; i++)
			intCount[i] = (int)this.counts[i];
		return intCount;
	}
	
	public boolean count(int arg, int times)
	{
		this.total += times;
		if(arg < this.start)
		{
			this.downAndOut += times;
			return false;
		}
		if(this.stop <= arg)
		{
			this.upAndOut += times;
			return false;
		}
		this.counts[(int)((arg-this.start)/this.gap)] += times;
		return true;
	}
	
	public boolean count(float val)
	{
		return this.count((int)val);
	}
	
	public boolean count(int arg)
	{
		return this.count(arg, 1);
	}
	
	public void count(int[] args)
	{
		for(int i : args)
			this.count(i);
	}
	
	public void count(Iterable<Integer> args)
	{
		for(Integer i : args)
			this.count(i);
	}
	
	public int gap()
	{
		return this.gap;
	}
	
	@Override
	public Iterator<Gap> iterator() 
	{
		return 
			new Iterator<Gap>()
			{
			
				private int i = -1;
			
				@Override
				public boolean hasNext() 
				{
					return this.i <= counts.length;
				}

				@Override
				public Gap next() 
				{
					if(i < 0)
					{
						this.i++;
						return new Gap(Integer.MIN_VALUE, start, downAndOut);
					}
					if(counts.length <= i)
					{
						this.i++;
						return new Gap(stop, Integer.MAX_VALUE, upAndOut);
					}
					int s = start + gap * i;
					return new Gap(s, s + gap, counts[i++]);
				}

				@Override
				public void remove() 
				{
					new UnsupportedOperationException().printStackTrace();
				}
			
			};
	}
	
	public int start()
	{
		return this.start;
	}
	
	public int stop()
	{
		return this.stop;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[ " + this.downAndOut);
		double s;
		for(int i = 0; i < this.counts.length; i++)
		{
			s = this.start + this.gap * i;
			sb.append(" (" + s + ") " + this.counts[i]);
		}
		sb.append(" (" + this.stop + ") " + this.upAndOut + " ]");
		return new String(sb);
	}
}
