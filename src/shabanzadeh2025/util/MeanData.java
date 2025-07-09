package shabanzadeh2025.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dene Ringuette
 */

public final class MeanData implements ComGrpUndAdd<MeanData>, DataSet, Dim, Spitter
{
	private double m;
	
	private double n;
	
	private double sum;
	
	public MeanData()
	{
		this.zero();
	}
	
	public static MeanData[] array(int n)
	{
		MeanData[] array = new MeanData[n];
		for(int i = 0; i < n; i++)
			array[i] = new MeanData();
		return array;
	}
	
	public static MeanData[] columns(double[][] data)
	{
		MeanData[] colVar = MeanData.array(Stat.width(data));
		for(double[] row : data)
			for(int i = 0; i < row.length; i++)
				colVar[i].add(row[i]);
		return colVar;
	}
	
	public static MeanData[] fullColumns(double[][] data)
	{
		final int n = Stat.numberOfFullColumns(data);
		MeanData[] colVar = MeanData.array(n);
		for(double[] row : data)
			for(int i = 0; i < n; i++)
				colVar[i].add(row[i]);
		return colVar;
	}
	
	public static ArrayList<MeanData> list(int n)
	{
		ArrayList<MeanData> array = new ArrayList<MeanData>(n);
		for(int i = 0; i < n; i++)
			array.add(new MeanData());
		return array;
	}
	
	public static final double MAX_ENTRY = Double.MAX_VALUE;
	
	public MeanData(int n, double mean)
	{
		this.n = n;
		this.sum = n * mean;
	}
	
	public MeanData(MeanData data)
	{
		this.set(data);
	}
	
	public MeanData(double arg)
	{
		this.zero();
		this.add(arg);
	}
	
	public MeanData(double[] args)
	{
		this.zero();
		for(double d : args)
			this.add(d);
	}
	
	public MeanData(double[] weights, double[] values)
	{
		final int n = weights.length;
		if(n != values.length)
			throw new IllegalArgumentException("Dimension Mismatch.");
		this.zero();
		for(int i = 0; i < n; i++)
			this.add(weights[i], values[i]);
	}
	
	public MeanData(float[] args)
	{
		this.zero();
		for(float f : args)
			this.add(f);
	}
	
	public MeanData(Collection<? extends Number> args)
	{
		this.zero();
		for(Number num : args)
			this.add(num.doubleValue());
	}
	
	public MeanData(int[] args)
	{
		this.zero();
		for(int i : args)
			this.add(i);
	}
	
	public int size()
	{
		return (int)this.n;
	}
	
	public double weight()
	{
		return this.n;
	}
	
	
	public double sum()
	{
		return this.sum;
	}
	
	public double sum2()
	{
		return this.sum * this.sum;
	}
	
	public void add(double arg)
	{
		this.n += 1;
		this.sum += arg;		
	}
	
	public void add(double w, double x)
	{
		this.n += w;
		this.sum += w * x;
	}
	
	public void sub(double arg)
	{
		this.n -= 1;
		this.sum -= arg;
	}
	
	public void sub(double w, double x)
	{
		this.n -= w;
		this.sum -= w * x;
	}
	
	public double[] moments()
	{
		return new double[]{this.weight(), this.mean()};
	}
	
	public double[] moments(double x0)
	{
		return new double[]{this.weight(), this.mean()-x0};
	}
	
	public static MultiVarFunc moment(final int order)
	{
		if(order < 0 || 1 < order)
			throw new IllegalArgumentException("Incompatible Moment Order of" + order);
		return new MultiVarFunc()
		{
			@Override
			public double apply(double... mu) 
			{
				return mu[order];	
			};
		};
	}
	
	public double mean()
	{
		return this.sum / this.n;
	}
	
	public double mean2()
	{
		return Pow.two(this.mean());
	}
	
	public void add(MeanData that) 
	{
		this.n += that.n;
		this.sum += that.sum;
	}

	public void neg() 
	{
		this.n = -this.n;
		this.sum = -this.sum;
	}

	public void sub(MeanData that) 
	{
		this.n -= that.n;
		this.sum -= that.sum;
	}

	public void zero() 
	{
		this.n = 0;
		this.sum = 0;
	}

	public void set(MeanData that) 
	{
		this.n = that.n;
		this.sum = that.sum;
	}

	public MeanData copy() 
	{
		MeanData thus = new MeanData();
		thus.set(this);
		return thus;
	}
	
	public int df()
	{
		return this.size() - 1;
	}
	
	public String toString()
	{
		return "{size=" + this.n + ", mean=" + this.mean() + "}"; 
	}
		
	public int dim()
	{
		return 1;
	}
	
	public void clear()
	{
		this.zero();
	}
	
	public int order()
	{
		return 1;
	}

	@Override
	public boolean permisible(double x)
	{
		return x < MeanData.MAX_ENTRY;
	}

	@Override
	public boolean permisible(double w, double x) 
	{
		return x < MeanData.MAX_ENTRY / w;
	}
	
	@Override
	public double[] normalizedMoments()
	{
		return new double[]{0.0, Double.NaN};
	}

	@Override
	public MeanData get() 
	{
		return new MeanData();
	}
	
	public void setPopulation(double total)
	{
		this.m = total;
	}
	
	public double sampleFraction()
	{
		return this.n/this.m;
	}

	@Override
	public boolean isFinite() 
	{
		return Double.isFinite(this.n) && Double.isFinite(this.sum);
	}
}
