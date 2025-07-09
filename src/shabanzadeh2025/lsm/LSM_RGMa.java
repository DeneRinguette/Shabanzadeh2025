package shabanzadeh2025.lsm;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Syo;

/**
 * Light sheet microscopy analysis for WT and RGMaF155A/+ mice. 
 * 
 * @author Dene Ringuette
 */

public class LSM_RGMa 
{
	
	static File LIGHT_SHEET = Directory.LSM;
	static File ALL = new File(LIGHT_SHEET, "2-Blind");
	static File CTRL = new File(LIGHT_SHEET, "RGMa_WT");
	static File TEST = new File(LIGHT_SHEET, "RGMa_F155A");
	
	public static void main(String[] args) throws IOException
	{
		switch(2)
		{
			case 1: Masks.masks(ALL); break;
			case 2: Stats.compute(CTRL, TEST); break;
			case 3: graph(); break;
		}	
	}	
		
	static void graph() throws IOException
	{
		double[] ctrl = new double[]{
				+0.233,
				+0.267,
				+0.398,
				+0.060,
				+0.003, 
				-0.014, 
				+0.391,
				+0.005,
			};
				
		double[] test = new double[]{	
				-0.070, 
				-0.077, 
				+0.050, 
				+0.072, 
				-0.171, 
				+0.002,
				+0.031,
			    -0.006,
			};
		
		String[] labels = new String[] {"WT", "RGMa-F155A/+"};
		double[][] all = new double[][]{ctrl, test};
		Syo.table("Relative Mean log-Intensity", labels, all);
		
		double[] pfm = Err.mean(test);
		Syo.pl(pfm);
		double[] wtm = Err.mean(ctrl);
		Syo.pl(wtm);
		double[] comb = Err.sub(pfm[0], pfm[1], wtm[0], wtm[1]);
		Syo.pl(comb);
		double[] exp = Err.exp(comb[0], comb[1]);
		exp[0] = exp[0] - 1.0;
		Ip.mult(exp, 100);
		Syo.pl("fold change = " + Format.decimals(exp[0], 1) + ((char)177) + Format.decimals(exp[1],1));
		
		Plot plot = new Plot(new Box(200,100,80,100), Span.bar(2, -0.25, 0.45));
		plot.rendText(8);
		plot.rendWidth(1);
		plot.drawYAxis();
		plot.setTickSize(3);
		plot.yMarkup(0, .2, 1);
		plot.setTextAngle(-45);
		plot.labelColumns("WT", "F155A");
		plot.rendPtRad(2.0);
		plot.setTextAngle(0);
		plot.plotColumnSems(all, 0.75);
		plot.plotColumnScatter(all, new String[]{"red" , "blue"});
		plot.plotColumnPValues(all, new int[]{0,1}, false);
		SVG.writeToFile(plot.getSVG(), new File(LIGHT_SHEET, "summary--RGMa--WT-vs-F155A.svg"));
		Syo.pl(new TTest().homoscedasticTTest(ctrl, test));
	}

}
