package shabanzadeh2025.util;

/**
 * Standard statistical test of Analysis of Variance (ANOVA).
 * @author Dene Ringuette
 */

public class ANOVA 
{	
	public static double anova(double[]... a)
	{
		int nTotal = Stat.size(a);
		int kTreat = a.length;
		double ssT = Sum.sumOfAverageSumSquared(a);
		double ssTreat = ssT - Stat.sum2(a)/nTotal;
		double ssError = Stat.sos(a) - ssT;
		int dfTreat = kTreat - 1;
		int dfError = nTotal - kTreat;
		double msTreat = ssTreat/dfTreat;
		double msError = ssError/dfError;
		double fStatistic = msTreat/msError;
		return Ccdf.f(fStatistic, dfTreat, dfError);
	}

	public static double[] anova(double[][][] a)
	{
		final int ka_treat = a.length;
		final int kb_treat = a[0].length;
		for(int i = 1; i < ka_treat; i++)
			if(a[i].length != kb_treat)
				throw new IllegalArgumentException("Non-rectangular Groups.");
		
		final double mean = Stat.mean(a);
		
		final double ss_total = Stat.sse(a, mean);
		
		double ss_error = 0.0;
		for(double[][] b : a)
			for(double[] c : b)
				ss_error += Stat.sse(c, Stat.mean(c));
		double df_error = 0.0;
		for(double[][] b : a)
			for(double[] c : b)
				df_error += Stat.size(c)-1;
		
		double ss_cells = ss_total - ss_error;
		
		double[][] means = Row.apply(a, Stat::mean);
		double[][] sizes = Row.apply(a, (args) -> {return Stat.size(args);});
	
		final double df_a = ka_treat-1;
		double[] mean_a = Row.apply(means, Stat::mean);
		double[] size_a = Row.apply(sizes, Stat::sum);
		double ss_a = 0.0;
		for(int i = 0; i < mean_a.length; i++)
			ss_a += size_a[i] * Stat.pow2(mean_a[i]-mean);
		final double ms_a = ss_a / df_a;
		
		final double df_b = kb_treat-1;
		final double[] mean_b = Row.apply(Op.transpose(means), Stat::mean);
		final double[] size_b = Row.apply(Op.transpose(sizes), Stat::sum);
		double ss_b = 0.0;
		for(int i = 0; i < mean_b.length; i++)
			ss_b += size_b[i] * Stat.pow2(mean_b[i]-mean);
		final double ms_b = ss_b/df_b;
	
		final double df_ab = df_a * df_b;
		final double ss_ab = ss_cells - ss_a - ss_b;
		final double ms_ab = ss_ab/df_ab; 
		
		final double ms_error = ss_error/df_error;
		
		double f_a = ms_a/ms_error;
		double f_b = ms_b/ms_error;
		double f_ab = ms_ab/ms_error;
		
		return new double[] {
				Ccdf.f(f_a,  (int)df_a,  (int)df_error),
				Ccdf.f(f_b,  (int)df_b,  (int)df_error),
				Ccdf.f(f_ab, (int)df_ab, (int)df_error)
		};
	}
}
