package shabanzadeh2025.behavior;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class ForelimbPlacement 
{	
	static File ROOT = new File(Directory.BEHAVIOR, "Forelimb Placement");
	
	public static void main(String[] args) throws IOException
	{
		for(File tab : ROOT.listFiles((File file) -> file.getName().endsWith(".tab")))
		{
			String name = Extension.remove(tab.getName());
			Syo.pl(name);
			
			final double[][] array = Parser.getDoubles(tab, 3, new int[]{1,2,3,4,5,6,7,8,9,10,11,12});
			
			VarData[][] data = new VarData[4][]; // group x day
			double[][][] values = new double[4][5][6];
			
			for(int i = 0; i < 4; i++)
			{
				int offset = i * 3;
				double[][] group = Op.minor(0+offset, 3+offset, 0, 30, array);
				data[i] = new VarData[5];
				for(int d = 0; d < 5; d++)
					data[i][d] = new VarData();				
				
				for(int j = 0; j < 30; j++)
				{
					MeanData sample = new MeanData();
					for(int r = 0; r < 3; r++)
						sample.add(group[r][j]);
					double value = sample.mean();
					data[i][j/6].add(value);
					values[i][j/6][j%6] = value;
				}
			}
			
			{
				Syo.table("cfp_ctrl", new String[]{"t=0", "24h", "72h", "5d", "7d"}, values[0]);
				Syo.table("cfp_test", new String[]{"t=0", "24h", "72h", "5d", "7d"}, values[2]);
				Syo.table("ifp_ctrl", new String[]{"t=0", "24h", "72h", "5d", "7d"}, values[1]);
				Syo.table("ifp_test", new String[]{"t=0", "24h", "72h", "5d", "7d"}, values[3]);
				
				double[][] cfp_ctrl = Op.transpose(values[0]);
				double[][] ifp_ctrl = Op.transpose(values[1]);
				double[][] cfp_test = Op.transpose(values[2]);
				double[][] ifp_test = Op.transpose(values[3]);
				
				double[][][] ifp_data = new double[][][]{ifp_ctrl, ifp_test};
				double[][][] cfp_data = new double[][][]{cfp_ctrl, cfp_test};
				 
				Syo.pl("ifp_data");
				RepeatedMeasuresFactorialANOVA.p_value(ifp_data, true);
				Syo.pl("cfp_data");
				RepeatedMeasuresFactorialANOVA.p_value(cfp_data, true);
			}
			
			Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, 2));
			pp.setTickSize(3);
			pp.yMarkup(0, 1.0, 1);
			pp.drawYAxis();
			pp.labelXCorrdinate(0, "t=0");
			pp.labelXCorrdinate(1, "24h");
			pp.labelXCorrdinate(2, "72h");
			pp.labelXCorrdinate(3, "5d");
			pp.labelXCorrdinate(4, "7d");
			pp.rendWidth(1.0);
			pp.rendPtRad(1.5);
			double[] day = new double[] {0,1,2,3,4};
			String[] color = new String[] {"red", "green", "blue", "darkorange"};
			for(int i = 0; i < 4; i++)
			{
				pp.rendFill("none");
				pp.rendColor(color[i]);
				pp.plotError(day, data[i]);
				pp.rendFillAlpha(0.5);
				pp.rendFill(color[i]);
				pp.rendColor("none");
				pp.plotColumnScatters(day, values[i]);
				pp.rendFillAlpha(1.0);
			}
			pp.rendColor("black");
			pp.plotSignificance(day, true, 2, data[0], data[2]);
			pp.plotSignificance(day, true, 2, data[1], data[3]);
			SVG.writeToFile(pp.getSVG(), new File(ROOT, name+".svg"));
			Syo.pl();
		}
	}
}
