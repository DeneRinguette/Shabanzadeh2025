package shabanzadeh2025.behavior;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.Box;
import shabanzadeh2025.rend.Plot;
import shabanzadeh2025.rend.SVG;
import shabanzadeh2025.rend.Span;
import shabanzadeh2025.rend.SvgColor;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class BedersonScore 
{
	public static File src = new File(Directory.BEHAVIOR, "Bederson Score");
	
	public static void main(String[] args) throws IOException
	{
		double[][][] data = new double[5][7][7];
		
		File a = new File(src, "All conditions.tab");
		BufferedReader r = Parser.buffer(a);
		r.readLine();
		for(int i = 0; i < 35; i++)
		{
			String[] split = r.readLine().split("\t");
			for(int j = 0; j < 7; j++)
				data[i/7][i%7][j] = Double.parseDouble(split[j+1]);
		}	
		r.close();
		
		plot("PF-429242", data, 0, 1);
		plot("RGMa-F155A", data, 0, 2);
		plot("NeoTie2creERT2", data, 3, 4);
	}
	
	static void plot(String name, double[][][] data, int ctrl_, int test_) throws IOException
	{
		Syo.pl(name);	
		final String[] time = new String[]{"Base", "4h", "24h", "48h", "72h", "5d", "7d"};	
		double[][] ctrl = data[ctrl_];
		double[][] test = data[test_];
		
		Syo.table("ctrl", time, Op.transpose(ctrl));
		Syo.table("test", time, Op.transpose(test));
		{
			double[][][] data_t = new double[][][] {ctrl, test}; 
			RepeatedMeasuresFactorialANOVA.p_value(data_t, true);
		}		
		String[] color = SvgColor.getDistinct(5, 0f, .7f, .8f);
		
		Plot pp = new Plot(new Box(50, 50, 100, 80), new Span(-0.5, 6.5, 0, 4)); 
		double[] x = new double[] {0, 1, 2, 3, 4, 5, 6};
		VarData[] ctrl_var = VarData.columns(ctrl);
		VarData[] test_var = VarData.columns(test);
		pp.drawYAxis();
		pp.drawXAxis();
		pp.setTickSize(3);
		pp.xTics(0, 1);
		pp.yMarkup(0, 1, 0);
		for(int i = 0; i < 7; i++)
			pp.labelXCorrdinate(i, time[i]);
		pp.rendFillAlpha(1.0);
		pp.rendColor(color[ctrl_]);
		pp.plotError(x, ctrl_var);
		pp.rendFillAlpha(1.0);
		pp.rendColor(color[test_]);
		pp.plotError(x, test_var);
		pp.rendColor("black");
		pp.rendText(10);
		pp.plotSignificance(x, false, 0, ctrl_var, test_var);
		SVG.writeToFile(pp.getSVG(), new File(src, name+".svg"));
		Syo.pl();
	}
}
