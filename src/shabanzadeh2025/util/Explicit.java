package shabanzadeh2025.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dene Ringuette
 */

public class Explicit implements Addable<Explicit>, Updatable
{
	private List<Double> values;
	private VarData variance;
	private Range range;
	
	public Explicit()
	{
		this.values = new ArrayList<Double>();
		this.variance = new VarData();
		this.range = new Range();
	}

	@Override
	public void set(Explicit that) 
	{
		this.values.clear();
		this.values.addAll(that.values);
		this.variance.set(that.variance);
		this.range.set(that.range);
		
	}

	@Override
	public Explicit copy() 
	{
		Explicit copy = new Explicit();
		copy.set(this);
		return copy;
	}

	@Override
	public void add(Explicit arg) 
	{
		this.values.addAll(arg.values);
		this.variance.add(arg.variance);
		this.range.add(arg.range);
		
		
	}

	@Override
	public void add(double value) 
	{
		this.values.add(value);
		this.variance.add(value);
		this.range.add(value);
	}
	
	public double[] values()
	{
		return Arrayz.toDoubleArray(this.values);
	}
	
	public VarData varData()
	{
		return this.variance.copy();
	}
	
	public Range range()
	{
		return this.range.copy();
	}
	
	public int size()
	{
		return this.values.size();
	}

	@Override
	public Explicit get() 
	{
		return new Explicit();
	}
}
