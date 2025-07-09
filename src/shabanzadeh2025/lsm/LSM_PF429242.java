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
 * Light sheet microscopy analysis for PF-429242 and vehicle treated mice. 
 * 
 * @author Dene Ringuette
 */

public class LSM_PF429242 
{
	static File LIGHT_SHEET = Directory.LSM;
	static File ALL = new File(LIGHT_SHEET, "1-Blind");
	static File CTRL = new File(LIGHT_SHEET, "WT_Vehicle");
	static File TEST = new File(LIGHT_SHEET, "WT_PF-429242");
	
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
				-0.017,
				+0.203,
				+0.091,
				+0.068,
				+0.285,
				-0.001,
				+0.235,
				+0.305
			};
				
		double[] test = new double[]{	
				+0.018,
				-0.002,
				+0.056,
				+0.084,
				-0.010,
				+0.022,
				+0.038
			};
		
		double[] test_mean = Err.mean(test);
		Syo.pl(test_mean);
		double[] control_mean = Err.mean(ctrl);
		Syo.pl(control_mean);
		double[] comb = Err.sub(test_mean[0], test_mean[1], control_mean[0], control_mean[1]);
		Syo.pl(comb);
		double[] exp = Err.exp(comb[0], comb[1]);
		exp[0] = exp[0] - 1.0;
		Ip.mult(exp, 100);
		Syo.pl("fold change = " + Format.decimals(exp[0], 1) + ((char)177) + Format.decimals(exp[1],1));
		
		String title = "Relative Mean log-Intensity";
		String[] labels = new String[] {"Vehicle", "PF-429242"}; 
		double[][] all = new double[][]{ctrl, test};		
		Syo.table(title, labels, all);
		
		Plot plot = new Plot(new Box(200,100,80,100), Span.bar(2, -0.05, 0.35));
		plot.rendText(8);
		plot.rendWidth(1);
		plot.drawYAxis();	
		plot.setTickSize(3);
		plot.yMarkup(0, .1, 1);
		plot.setTextAngle(-45);
		plot.labelColumns("WT", "PF");
		plot.rendPtRad(2.0);
		plot.setTextAngle(0);
		plot.plotColumnSems(all, 0.75);
		plot.plotColumnScatter(all, new String[]{"red" , "blue"});
		plot.plotColumnPValues(all, new int[]{0,1}, false);	
		SVG.writeToFile(plot.getSVG(), new File(LIGHT_SHEET, "summary--WT--Vehicle-vs-PF-429242.svg"));	
		Syo.pl(new TTest().homoscedasticTTest(ctrl, test));
	}
}
