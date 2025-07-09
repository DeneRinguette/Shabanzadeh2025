package shabanzadeh2025.mri;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * Graphing measured rabbit weights.
 * 
 * @author Dene Ringuette
 */

public class RabbitWeight 
{
	public static void main(String[] args) throws IOException
	{
		switch(1)
		{
			case 1: graph(); break;
			case 2: mortality(); break;
		}		
	}
	
	public static void graph() throws IOException
	{
		final double[][] ctrl = new double[][] {
			{3.02,3,2.91,2.79,2.515,2.65,2.7},
			{3.67,3.7,3.59,3.38,3.305,3.27,3.25},
			{3.35,3.33,2.94,3.28,3.3,3.31,3.3},
			{3.45,3.41,3.4,3.34,3.32,3.16,3.02},
			{3.3,3.1,2.9,2.995,2.995,2.995,2.995}
		};
		
		final double[][] test = new double[][] {
			{3.32,3.3,3.2,3.18,3.05,2.92,2.82},
			{3.215,3.2,3,3.12,3.13,2.99,2.92},
			{3.1,3.08,3,2.87,2.81,2.68,2.55},
			{3.08,2.98,2.9,2.8,2.7,2.7,2.71},
			{3.22,3.2,3.16,3.12,3.09,3.105,3.08},
			{3.24,3.22,3.28,3.08,3.03,3.06,3.04}
		};
		
		String[] times = new String[] {"0h", "6h", "24h", "48h", "72h", "5d", "7d"};
		
		RepeatedMeasuresFactorialANOVA.p_value(new double[][][] {ctrl, test}, true);	
		Syo.table("ctrl", times, Op.transpose(ctrl));
		Syo.table("pf42", times, Op.transpose(test));
		times[0] = "t=0";
		
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 6.5, 0, 4));
		double[] day = new double[] {0,1,2,3,4,5,6};
		VarData[] ctrl2 = VarData.fullColumns(ctrl);
		VarData[] test2 = VarData.fullColumns(test);
		pp.setTickSize(3);
		pp.yMarkup(0, 1, 0);
		pp.drawYAxis();
		pp.labelColumns(times);
		pp.rendWidth(1.0);
		pp.rendColor("red");
		pp.plotError(day, ctrl2);
		pp.rendColor("blue");
		pp.plotError(day, test2);
		pp.rendColor("black");
		pp.plotSignificance(day, true, 1, ctrl2, test2);
		pp.rendColor("none");
		pp.rendFillAlpha(0.5);
		pp.rendPtRad(1.5);
		pp.rendFill("red");
		pp.plotColumnScatter(Op.transpose(ctrl));
		pp.rendFill("blue");
		pp.plotColumnScatter(Op.transpose(test));
		SVG.writeToFile(pp.getSVG(), new File(Rabbit.SRC, "mass.svg"));
	}
	
	public static void mortality()
	{
		Syo.pl(new ChiSquareTest().chiSquareTest(new long[][] {{2, 5}, {1, 6}}));
		Syo.pl(new ChiSquareTest().chiSquareTest(new long[][] {{2, 1}, {5, 6}}));
	}
}
