package shabanzadeh2025.util;

/**
 * @author Dene Ringuette
 */

public class RepeatedMeasuresFactorialANOVA 
{	
	public static double[] p_value(double[][][] data, boolean print_results_table)
	{
		// g = number of groups, n = number of subjects in group, k = treatments or time points per subject 
		// data[g][n][k]			
		final int repeats = data[0][0].length;
		for(double[][] group : data)
			for(double[] subject : group)
				if(subject.length !=  repeats)
					throw new IllegalArgumentException("subjects must have all repeats");
			
		final double mean3 = Stat.mean(data);
		
		final double SS_total = Stat.sse(data, mean3);
		
		final double[][] mean_S = Row.apply(data, Stat::mean);
		final double[][] size_S = Row.apply(data, Stat::size);
		final double SS_BS = Stat.sse(size_S, mean_S, mean3);
		
		double error = 0;
		for(double[][] group : data)
			for(double[] subject : group)
				error += Stat.sse(subject);
		final double SS_WS = error;
		
		final double[] mean_G = Row.apply(mean_S, (double[] arg) -> Stat.mean(arg));
		final double[] size_G = Row.apply(size_S, (double[] arg) -> Stat.sum(arg));
		final double SS_G = Stat.sse(size_G, mean_G, mean3);
		final int df_G = data.length - 1;
		int df_SvG = 0;
		for(double[][] group : data)
			df_SvG += group.length - 1;
		final double MS_G = SS_G / df_G;
		
		final double SS_SvG = SS_BS - SS_G;
		final double MS_SvG = SS_SvG / df_SvG;
		
		final double[] sum_T = new double[repeats];
		double size_T = 0;
		for(double[][] group : data)
			for(double[] subject : group)
			{
				Ip.add(sum_T, subject);
				size_T += 1;
			}
		double[] mean_T = Op.div(sum_T, size_T);
		
		double[][] mean_GT = new double[data.length][];
		for(int i = 0; i < data.length; i++)
			mean_GT[i] = Column.mean(data[i]);
		
		double SS_cellsTxG = 0.0;
		for(int i = 0; i < data.length; i++)
		{
			for(int k = 0; k < repeats; k++)
				SS_cellsTxG += data[i].length * Pow.two(mean_GT[i][k]-mean3);
		}
		
		final double SS_T = size_T * Stat.sse(mean_T, mean3);
		final int df_T = repeats - 1;
		final double MS_T = SS_T / df_T;
		
		final int df_TxG = df_T * df_G;
		
		int df_S = 0;
		for(double[][] group : data)
			df_S += group.length - 1;
		final int df_TxSvG = df_S * df_T;
		
		final int df_BS = df_G + df_SvG;
		final int df_WS = df_T + df_TxG + df_TxSvG;
		final int df_total = df_BS + df_WS;
		
		final double SS_TxG = SS_cellsTxG - SS_T - SS_G;	
		final double MS_TxG = SS_TxG / df_TxG;
		
		final double SS_TxSvG = SS_WS - SS_T - SS_TxG;
		final double MS_TxSvG = SS_TxSvG / df_TxSvG;
		
		final double F_G = MS_G / MS_SvG;
		final double F_T = MS_T / MS_TxSvG;
		final double F_TxG = MS_TxG / MS_TxSvG;
		
		if(1e-6 < Math.abs((SS_total - (SS_G+SS_SvG+SS_T+SS_TxG+SS_TxSvG))/SS_total))
			throw new IllegalStateException("SS error invalid.");
		
		double[] p_values = new double[]{
				Ccdf.f(F_G, df_G, df_SvG),
				Ccdf.f(F_T, df_T, df_TxSvG),
				Ccdf.f(F_TxG, df_TxG, df_TxSvG)
			};
		
		if(print_results_table)
		{
			System.out.println("Source\tdf\tSS\tMS\tF\tp");
			System.out.println("BS\t"+ df_BS + "\t"+ SS_BS);
			System.out.println(" G\t" + df_G + "\t "+ SS_G + "\t "+ MS_G  + "\t" + F_G + "\t" + p_values[0]);
			System.out.println(" SvG\t" + df_SvG  + "\t "+ SS_SvG  + "\t "+ MS_SvG);
			System.out.println("WS\t"+ df_WS + "\t"+ SS_WS);	
			System.out.println(" T\t"+ df_T +  "\t" + SS_T + "\t" + MS_T + "\t" + F_T + "\t" + p_values[1]);
			System.out.println(" TxG\t"+ df_TxG +  "\t" + SS_TxG + "\t" + MS_TxG + "\t" + F_TxG + "\t" + p_values[2]);
			System.out.println(" TxSvG\t" + df_TxSvG + "\t" + SS_TxSvG + "\t" + MS_TxSvG);
			System.out.println("Total\t" + df_total + "\t"+ SS_total);
		}	
		return p_values;
	}
}
