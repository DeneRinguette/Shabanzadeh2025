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
 * Light sheet microscopy analysis for NeoTie2creERT2 mice. 
 * 
 * @author Dene Ringuette
 */

public class LSM_NeoTie2cre 
{
	static File LIGHT_SHEET = Directory.LSM;
	static File ALL = new File(LIGHT_SHEET, "3-Blind");
	static File CTRL = new File(LIGHT_SHEET, "NeoTie2cre_Vehicle");
	static File TEST = new File(LIGHT_SHEET, "NeoTie2cre_Tamoxifen");
	
	public static void main(String[] args) throws IOException
	{
		switch(3)
		{
			case 1: Masks.masks(ALL);
			case 2: Stats.compute(CTRL, TEST); break;
			case 3: graph(); break;	
		}		
	}
		
	static void graph() throws IOException
	{
		double[] ctrl = new double[] 
		{
				+0.185,
				+0.636,
				+0.600,
				+0.130,
				+0.248,
				+0.343,
				+0.830
		};
			
		double[] test = new double[] 
		{	
				+0.272,
				+0.160,
				+0.198,
				+0.217,
				-0.006,
				+0.152,
				-0.077,
				+0.086,
				+0.122,
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
		
		String[] labels = new String[] {"w/ Vehicle", "w/ Tamoxifen"};
		double[][] all = new double[][]{ctrl, test};
		Syo.table("Relative Mean log-intensity", labels, all);
		
		Plot plot = new Plot(new Box(200,100,80,100), Span.bar(2, -0.1, 0.9));
		plot.rendText(8);
		plot.rendWidth(1);
		plot.drawYAxis();
		plot.setTickSize(3);
		plot.yMarkup(0, .2, 1);
		plot.setTextAngle(-45);
		plot.labelColumns("Vehicle", "Tamoxifen");
		plot.rendPtRad(2.0);
		plot.setTextAngle(0);
		plot.plotColumnSems(all, 0.75);
		plot.plotColumnScatter(all, new String[]{"red" , "blue"});
		plot.plotColumnPValues(all, new int[]{0,1}, false);
		SVG.writeToFile(plot.getSVG(), new File(LIGHT_SHEET, "summary--NeoTie2cre--Vehicle-vs-Tamoxifen.svg"));
		Syo.pl(new TTest().homoscedasticTTest(ctrl, test));
	}
}
