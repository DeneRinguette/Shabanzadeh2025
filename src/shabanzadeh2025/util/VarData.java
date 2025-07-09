package shabanzadeh2025.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.DoublePredicate;

import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public final class VarData implements ComGrpUndAdd<VarData>, DataSet, Dim, Spitter, Serializable
{
	private static final long serialVersionUID = -7723996588708683668L;
	
	private double n;

	private double sumX;
	
	private double sumXX;
		
	public static double anova(VarData... sets)
	{
		final VarData grand = new VarData();
		for(VarData set : sets)
			grand.add(set);
		
		final int nTotal = grand.size();
		final int kTreat = sets.length;
		
		double ssT = 0.0;
		for(VarData set : sets)
			ssT += set.sum2()/set.size();
		double ssTreat = ssT - grand.sum2()/nTotal;
		
		double ssError = 0.0;
		for(VarData set : sets)
			ssError += set.ss();
		ssError -= ssT;
		
		final int dfTreat = kTreat - 1;
		final int dfError = nTotal - kTreat;
		final double msTreat = ssTreat/dfTreat;
		final double msError = ssError/dfError;
		final double fStatistic = msTreat/msError;
		return Ccdf.f(fStatistic, dfTreat, dfError);
	}
	
	public static double anova(int i, VarData[]... sets)
	{
		VarData[] col = new VarData[sets.length];
		for(int k = 0; k < col.length; k++)
			col[k] = sets[k][i];
		return anova(col);
	}
	
	public VarData()
	{
		this.zero();
	}
	
	public static VarData[] array(int n)
	{
		VarData[] array = new VarData[n];
		for(int i = 0; i < n; i++)
			array[i] = new VarData();
		return array;
	}
	
	public static VarData[] columns(double[][] data)
	{
		VarData[] colVar = VarData.array(Stat.width(data));
		for(double[] row : data)
			for(int i = 0; i < row.length; i++)
				colVar[i].add(row[i]);
		return colVar;
	}
	
	public static VarData[] columns(double[][] data, DoublePredicate c)
	{
		VarData[] colVar = VarData.array(Stat.width(data));
		for(double[] row : data)
			for(int i = 0; i < row.length; i++)
				if(c.test(row[i]))	
					colVar[i].add(row[i]);
		return colVar;
		
	}
	
	public static VarData[] rows(double[][] data)
	{
		VarData[] colVar = VarData.array(data.length);
		for(int i = 0; i < data.length; i++)
			colVar[i].addAll(data[i]);
		return colVar;
	}
	
	public static VarData[] fullColumns(double[][] data)
	{
		final int n = Stat.numberOfFullColumns(data);
		VarData[] colVar = VarData.array(n);
		for(double[] row : data)
			for(int i = 0; i < n; i++)
				colVar[i].add(row[i]);
		return colVar;
	}
	
	public static ArrayList<VarData> list(int n)
	{
		ArrayList<VarData> array = new ArrayList<VarData>(n);
		for(int i = 0; i < n; i++)
			array.add(new VarData());
		return array;
	}
	
	public static final double MAX_ENTRY = Math.sqrt(Double.MAX_VALUE);
	
	public VarData(int n, double mean, double sd)
	{
		this.n = n;
		this.sumX = n * mean;
		this.sumXX = (n-1) * sd * sd + n * mean * mean;
	}
	
	public VarData(VarData data)
	{
		this.set(data);
	}
	
	public void add(ImageProcessor data)
	{
		final int n = data.getPixelCount();
		for(int i = 0; i < n; i++)
			this.add(data.getf(i));
	}
	
	public VarData(double arg)
	{
		this.zero();
		this.add(arg);
	}
	
	public VarData(double[] args)
	{
		this.zero();
		for(double d : args)
			this.add(d);
	}
	
	public void addAll(double[] args)
	{
		for(double arg : args)
			this.add(arg);
	}
	
	public VarData(VarData[] args)
	{
		this.zero();
		for(VarData d : args)
			this.add(d);
	}
	
	public VarData(double[] weights, double[] values)
	{
		final int n = weights.length;
		if(n != values.length)
			throw new IllegalArgumentException("Dimension Mismatch.");
		this.zero();
		for(int i = 0; i < n; i++)
			this.add(weights[i], values[i]);
	}
	
	public VarData(float[] args)
	{
		this.zero();
		for(float f : args)
			this.add(f);
	}
	
	public VarData(Collection<? extends Number> args)
	{
		this.zero();
		for(Number num : args)
			this.add(num.doubleValue());
	}
	
	public VarData(int[] args)
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
		return this.sumX;
	}
	
	public double sum(double k)
	{
		return this.sumX - k * this.n;
	}
	
	public double ss()
	{
		return this.sumXX;
	}
	
	public double ss(double k)
	{
		return this.sumXX - 2*k*this.sumX + k*k*this.n;
	}
	
	public double sum2()
	{
		return this.sum() * this.sum();
	}
	
	public void add(double arg)
	{
		this.n += 1;
		this.sumX += arg;
		this.sumXX += arg * arg;
	}
	
	public void add(double w, double x)
	{
		this.n += w;
		final double wx = w * x;
		this.sumX += wx;
		this.sumXX += wx * x;	
	}
	
	public void sub(double arg)
	{
		this.n -= 1;
		this.sumX -= arg;
		this.sumXX -= arg * arg;
	}
	
	public void sub(double w, double x)
	{
		this.n -= w;
		final double wx = w * x;
		this.sumX -= wx;
		this.sumXX -= wx * x;	
	}
	
	public double[] moments()
	{
		final double weight = weight();
		final double invW = 1.0 / weight;
		final double mean = this.sum() * invW;
		final double var = this.ss() * invW - mean * mean;
		return new double[]{weight, mean, var};
	}
	
	public double[] moments(double x0)
	{
		final double weight = this.weight();
		final double invW = 1.0 / weight;
		final double mean = this.sum()*invW;
		final double mse = this.ss()*invW + (x0-2*mean)*x0;
		return new double[]{weight, mean-x0, mse};
	}
	
	public static MultiVarFunc moment(final int order)
	{
		if(order < 0 || 2 < order)
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
	
	public double var()
	{
		return this.sse()/this.df();
	}
	
	public double sigma2()
	{
		return this.sse()/this.weight();
	}
	
	public double mean()
	{
		return this.sum()/this.weight();
	}
	
	public double mean2()
	{
		return Pow.two(this.mean());
	}
	
	public double sse()
	{
		return this.ss() - this.sum2() / this.weight();
	}
	
	public double sse(double k)
	{
		return this.ss(k) - this.pow2(this.sum(k)) / this.weight();
	}
	
	public double sse_safe()
	{
		return this.sse(this.mean());
	}
	
	private double pow2(double arg)
	{
		return arg * arg;
	}
	
	public void add(VarData that) 
	{
		this.n += that.n;
		this.sumX += that.sumX;
		this.sumXX += that.sumXX;
	}
	
	public void neg() 
	{
		this.n = -this.n;
		this.sumX = -this.sumX;
		this.sumXX = -this.sumXX;
	}

	public void sub(VarData that) 
	{
		this.n -= that.n;
		this.sumX -= that.sumX;
		this.sumXX -= that.sumXX;
	}

	public void zero() 
	{
		this.n = 0;
		this.sumX = 0;
		this.sumXX = 0;
	}

	public void set(VarData that) 
	{
		this.n = that.n;
		this.sumX = that.sumX;
		this.sumXX = that.sumXX;
	}

	public VarData copy() 
	{
		VarData thus = new VarData();
		thus.set(this);
		return thus;
	}
	
	public int df()
	{
		return this.size() - 1;
	}
	
	public String toString()
	{
		return "{size=" + this.n + ", mean=" + this.mean() + ", variance=" + this.var() + "}"; 
	}
	
	public String toSEM()
	{
		return "{mean=" + this.mean() + ", sem=" + this.sem() + "}"; 
	}
	
	public double stdDev()
	{
		return Math.sqrt(this.var());
	}
	
	public double sigma()
	{
		return Math.sqrt(this.sigma2());
	}
	
	public double stdErr()
	{
		return this.std_of_mean();
	}
	
	public double var_of_mean()
	{
		return this.var()/this.weight();
	}
	
	public double std_of_mean()
	{
		return Math.sqrt(this.var_of_mean());
	}
	
	public double sem()
	{
		return this.std_of_mean();
	}
	
	public double scr()
	{
		return Math.sqrt((this.ss()/this.sum()) * (this.weight()/this.sum()) - 1.0);
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
		return 2;
	}

	@Override
	public boolean permisible(double x)
	{
		return x < VarData.MAX_ENTRY;
	}

	@Override
	public boolean permisible(double w, double x) 
	{
		return x < VarData.MAX_ENTRY / w;
	}
	
	public double[] normalizedMoments()
	{
		final double[] mu = this.moments();
		
		final double sigmaX = Math.sqrt(mu[2]);
		
		final double snrX = mu[1] / sigmaX;
		
		return new double[]{0.0, snrX, 1.0};
	}
	
	public double sfi()
	{
		final double fact = this.sum() * this.mean();
		return fact/(this.ss()-fact);
	}
	
	public double sn_arrr()
	{
		return this.mean()/this.stdDev();
	}
	
	public double snr()
	{
		return this.mean2() / this.var();
	}
	
	public double[] snr_()
	{
		return Err.div_(this.mean2_(), this.var_());
	}
	
	public double std_of_var()
	{
		return this.var() * Math.sqrt(2.0/this.df());
	}
	
	public double[] var_()
	{
		return new double[]{this.var(), this.std_of_var()};
	}
	
	public double[] mean_()
	{
		return new double[]{this.mean(), this.std_of_mean()};
	}
	
	public double[] mean2_()
	{
		return Err.sq_(this.mean_());
	}
	
	public static double[] weights(VarData[] sets)
	{
		final int n = sets.length;
		final double[] weights = new double[n];
		for(int i = 0; i < n; i++)
			weights[i] = sets[i].weight();
		return weights;
	}
	
	public static double[] means(VarData[] sets)
	{
		final int n = sets.length;
		final double[] means = new double[n];
		for(int i = 0; i < n; i++)
			means[i] = sets[i].mean();
		return means;
	}
	
	public static double[] means(List<VarData> list)
	{
		final int n = list.size();
		final double[] means = new double[n];
		for(int i = 0; i < n; i++)
			means[i] = list.get(i).mean();
		return means;
	}
	
	public static double[] sse(List<VarData> list)
	{
		final int n = list.size();
		final double[] means = new double[n];
		for(int i = 0; i < n; i++)
			means[i] = list.get(i).sse();
		return means;
	}
	
	public static double[] sem(List<VarData> list)
	{
		final int n = list.size();
		final double[] means = new double[n];
		for(int i = 0; i < n; i++)
			means[i] = list.get(i).sem();
		return means;
	}
	
	public static double[] stdDevs(VarData[] sets)
	{
		final int n = sets.length;
		final double[] stdDev = new double[n];
		for(int i = 0; i < n; i++)
			stdDev[i] = sets[i].stdDev();
		return stdDev;
	}
	
	public static double[] stds_of_mean(VarData[] sets)
	{
		final int n = sets.length;
		final double[] std_of_means = new double[n];
		for(int i = 0; i < n; i++)
			std_of_means[i] = sets[i].std_of_mean();
		return std_of_means;
	}
	
	public double studentsT(double mu)
	{
		return (this.mean()-mu)/this.std_of_mean();
	}
	
	public double zScoreOfMean(double mu)
	{
		return (this.mean()-mu)/this.sigmaOfMean();
	}
		
	public double zTest(double mu)
	{
		return ZTest.twoTailedProbability(this.zScoreOfMean(mu));
	}
	
	public double zTest(VarData that)
	{
		final double z = (this.mean()-that.mean()) /
				Math.sqrt(
						this.sigma2()/this.weight()+
						that.sigma2()/that.weight()
					);
		
		return ZTest.twoTailedProbability(z);
	}
	
	public double zTest(VarData that, double s0)
	{
		final double z = (this.mean()-that.mean()) /
				Math.sqrt(
						this.sigma2()/this.weight()+
						that.sigma2()/that.weight()
						+s0
					);
		
		return ZTest.twoTailedProbability(z);
	}
	
	public double sigmaOfMean()
	{
		return Math.sqrt(this.sigma2()/this.weight());
	}
	
	public double studentsT(VarData that)
	{
		return (this.mean()-that.mean())
				/ 
				Math.sqrt(
						(1.0/this.n+1.0/that.n)
						*
						(this.df()*this.var()+that.df()*that.var())
						/
						(this.df()+that.df())
					);
	}
	
	public double studentsTtest(double mu)
	{
		return Ccdf.t(this.studentsT(mu), this.df());
	}
	
	public double studentsTtest(VarData that)
	{
		return Ccdf.t(this.studentsT(that), this.df()+that.df());
	}
		
	public double catastrophy_index()
	{
		return (this.n/this.sumX)*(this.sumXX/this.sumX);
	}
		
	public void div(double fact)
	{
		this.sumX /= fact;
		this.sumXX /= fact;
		this.sumXX /= fact;
	}
	
	public static VarData[][] make(int n, int m)
	{
		VarData[][] array = new VarData[n][];
		for(int i = 0; i < n; i++)
			array[i] = VarData.make(m);
		return array;
	}
	
	public static VarData[] make(int n)
	{
		VarData[] array = new VarData[n];
		for(int i = 0; i < n; i++)
			array[i] = new VarData();
		return array;
	}
	
	public static double cohensD(VarData data0, VarData data1)
	{	
		return (data0.mean() - data1.mean())/Math.sqrt(pooledVariance(data0, data1));
	}
	
	public static double pooledVariance(VarData data0, VarData data1)
	{
		double sum_sse = data0.sse()+data1.sse();
		double sum_df = data0.df()+data1.df();
		
		return sum_sse / sum_df;
	}

	@Override
	public VarData get() 
	{
		return new VarData();
	}
	
	public double density()
	{
		return this.weight() / Math.sqrt(this.sigma2());
	}
	
	public boolean isFinite()
	{
		return Double.isFinite(this.sumXX) && Double.isFinite(this.sumX) && Double.isFinite(this.n);
	}
}