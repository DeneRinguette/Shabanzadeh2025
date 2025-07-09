package shabanzadeh2025.behavior;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import shabanzadeh2025.util.Parser;
import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class OpenFieldTest 
{
	public static void main(String[] args) throws IOException
	{
		File src = new File(Directory.BEHAVIOR, "Open Field Test");
		File file = new File(src, "All conditions.txt");
		
		String[] measure = new String[] {"Total Activity", "Distance Travelled"};
		int[] column = new int[]{2,23};
		double[] range = new double[] {750, 60};
		String[] name = new String[] {"RMGa-F155F", "PF-429242", "NeoTie2creERT2"};
		
		for(int parameter = 0; parameter < 2; parameter++)
		{
			final double[] array = Parser.getDoubles(file, 3, new int[]{column[parameter]})[0];
		
			int i = 0;
			final double[] rgma_ctrl = Arrays.copyOfRange(array, i, i += 36);
			final double[] rgma_test = Arrays.copyOfRange(array, i, i += 36);
			final double[] pf42_ctrl = Arrays.copyOfRange(array, i, i += 36);
			final double[] pf42_test = Arrays.copyOfRange(array, i, i += 36);
			final double[] tie2_ctrl = Arrays.copyOfRange(array, i, i += 36);
			final double[] tie2_test = Arrays.copyOfRange(array, i, i += 36);
			
			for(int condition = 0; condition < 3; condition++)
			{
				double[] ctrl_ = null;
				double[] test_ = null;
				
				if(condition == 0)
				{
					ctrl_ = rgma_ctrl;
					test_ = rgma_test;
				}
				if(condition == 1)
				{
					ctrl_ = pf42_ctrl;
					test_ = pf42_test;
				}
				if(condition == 2)
				{
					ctrl_ = tie2_ctrl;
					test_ = tie2_test;
				}
				
				double[][] ctrl = Op.to2D(ctrl_, 6);
				double[][] test = Op.to2D(test_, 6);
				
				
				String[] times = new String[]{"t=0", "24h", "48h", "72h", "5d", "7d"};
				
				Syo.table(measure[parameter] + " "+ name[condition] + " ctrl", times, ctrl);
				Syo.table(measure[parameter] + " "+ name[condition] + " test", times, test);
				
				{
					double[][][] data = new double[][][] {Op.transpose(ctrl), Op.transpose(test)};
					RepeatedMeasuresFactorialANOVA.p_value(data, true);
				}
				Syo.pl();
				
				Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, range[parameter]));
				double[] day = new double[] {0,1,2,3,4,5};
				VarData[] ctrl2 = VarData.rows(ctrl);
				VarData[] test2 = VarData.rows(test);
				VarData ratio = new VarData();
				for(int d = 2; d < 6; d++)
					ratio.add(test2[d].mean()/ctrl2[d].mean());
				pp.setTickSize(3);
				pp.yMarkup(0, range[parameter]/3, 0);
				pp.drawYAxis();
				pp.labelXCorrdinate(0, "t=0");
				pp.labelXCorrdinate(1, "24h");
				pp.labelXCorrdinate(2, "48h");
				pp.labelXCorrdinate(3, "72h");
				pp.labelXCorrdinate(4, "5d");
				pp.labelXCorrdinate(5, "7d");
				pp.rendWidth(1.0);
				pp.rendPtRad(1.5);
				pp.rendColor("red");
				pp.plotError(day, ctrl2);
				pp.rendColor("blue");
				pp.plotError(day, test2);
				pp.rendColor("black");
				pp.plotSignificance(day, false, 0, ctrl2, test2);				
				pp.rendColor("none");
				pp.rendFillAlpha(0.5);
				pp.rendFill("red");
				pp.plotColumnScatters(day, ctrl);
				pp.rendFill("blue");
				pp.plotColumnScatters(day, test);
				SVG.writeToFile(pp.getSVG(), new File(src, measure[parameter]+" "+name[condition]+".svg"));
			}
		}
	}
}
