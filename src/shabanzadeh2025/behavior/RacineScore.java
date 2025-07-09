package shabanzadeh2025.behavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class RacineScore 
{
	public static File src = new File(Directory.BEHAVIOR, "Racine Score");
	
	static void plot(String name, double[][][] data, int ctrl_, int test_) throws IOException
	{
		Syo.pl(name);
		
		final String[] time = new String[]{"4h", "24h", "48h", "72h", "5d", "7d"};
		
		double[][] ctrl = data[ctrl_];
		double[][] test = data[test_];
		
		double[] ctrl_sum = Stat.sums(ctrl);
		double[] test_sum = Stat.sums(test);
		
		{
			double[][] ctrl_tn = Op.transpose(ctrl);
			double[][] test_tn = Op.transpose(test);
			
			TTest ttest = new TTest();
			Syo.pl("total\tp = " + ttest.tTest(ctrl_sum, test_sum));
			for(int d = 0; d < ctrl_tn.length; d++)
			{
				Syo.pl(time[d]+ "\tp = " + ttest.tTest(ctrl_tn[d], test_tn[d]));
			}
			Syo.table("ctrl", Op.append(time, "Total"), Op.append(ctrl_tn, new double[][] {ctrl_sum}));
			Syo.table("test", Op.append(time, "Total"), Op.append(test_tn, new double[][] {test_sum}));
		}
		Syo.pl();
		 
		String[] color = SvgColor.getDistinct(6, 0f, .7f, .8f);
		Plot pp = new Plot(new Box(50, 50, 100, 80), new Span(-0.5, 5.5, 0, 4));
		double[] x = new double[] {0, 1, 2, 3, 4, 5};
		VarData[] ctrl_var = VarData.columns(ctrl);
		VarData[] test_var = VarData.columns(test);
		pp.drawYAxis();
		pp.drawXAxis();
		pp.setTickSize(3);
		pp.xTics(0, 1);
		pp.yMarkup(0, 1, 0);
		for(int i = 0; i < time.length; i++)
			pp.labelXCorrdinate(i, time[i]);
		pp.rendPtRad(2.0);
		pp.rendFillAlpha(0.5);
		pp.rendFill(color[ctrl_]);
		pp.rendColor("none");
		double[][] ctrl_trans = Op.transpose(ctrl);
		ctrl_trans = Op.redact(ctrl_trans, (double a) -> a == 0);
		pp.plotColumnScatter(ctrl_trans);
		pp.rendFill("none");
		pp.rendFillAlpha(1.0);
		pp.rendColor(color[ctrl_]);
		pp.plotError(x, ctrl_var);
		pp.rendFillAlpha(0.5);
		pp.rendFill(color[test_]);
		pp.rendColor("none");
		double[][] test_trans = Op.transpose(test);
		test_trans = Op.redact(test_trans, (double a) -> a == 0);
		pp.plotColumnScatter(test_trans);
		pp.rendFill("none");
		pp.rendFillAlpha(1.0);
		pp.rendColor(color[test_]);
		pp.plotError(x, test_var);
		pp.rendColor("black");
		pp.rendText(10);
		pp.plotSignificance(x, false, 0, ctrl_var, test_var);
		SVG.writeToFile(pp.getSVG(), new File(src, name+".svg"));
	 }
	 
	public static void main(String[] args) throws IOException
	{
		double[][][] dataPooled = new double[5][][];
		
		for(boolean ttc : new boolean[] {true,false})
		{
		 	int groups = ttc ? 5 : 6;
		 	int animals = ttc ? 7 : 6;
		 	int total = groups * animals;
		 	int points = 6;
		 
			double[][][] data = new double[groups][animals][points];
			
			File a = new File(src, ttc ? "TTC.tab" : "Immuno.tab");
			
			BufferedReader r = Parser.buffer(a);
			r.readLine();
			for(int index = 0; index < total; index++)
			{
				String[] split = r.readLine().split("\t");
				for(int point = 0; point < points; point++)
					data[index/animals][index%animals][point] = Double.parseDouble(split[point+1]);
			}
			r.close();
			
			if(ttc)
			{
				for(int i = 0; i < 5; i++)
					dataPooled[i] = data[i];
			}
			else
			{
				dataPooled[0] = Op.append(dataPooled[0], data[0], data[2]);
				dataPooled[1] = Op.append(dataPooled[1], data[1]);
				dataPooled[2] = Op.append(dataPooled[2], data[3]);
				dataPooled[3] = Op.append(dataPooled[3], data[4]);
				dataPooled[4] = Op.append(dataPooled[4], data[5]);
			}
		};
		
		plot("PF", dataPooled, 0, 1);
		plot("RGma", dataPooled, 0, 2);
		plot("Tie2", dataPooled, 3, 4);
	}
}