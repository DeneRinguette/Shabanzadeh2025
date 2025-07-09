package shabanzadeh2025.behavior;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class Weight 
{
	public static void main(String[] args) throws IOException
	{
		File src = new File(Directory.BEHAVIOR, "Weights");
		
		for(String c : new String[] {"RGMa", "PF429242", "Tie2"})
		{
			File file = new File(src, c + ".tab");
			
			Syo.pl(file.getName());		
			final double[][] array = Parser.getDoubles(file, 1, new int[]{1,2,3,4,5,6});
			
			double[][] ctrl = Op.minor(0, 6, 0, 6, array);
			
			double[][] test = Op.minor(0, 6, 6, 12, array);
			
			double[][] ctrl_ = Op.transpose(ctrl);
			double[][] test_ = Op.transpose(test);
						
			RepeatedMeasuresFactorialANOVA.p_value(new double[][][] {ctrl_, test_}, true);			
			
			Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, 30));
			double[] day = new double[] {0,1,2,3,4,5};
			VarData[] ctrl2 = VarData.rows(ctrl);
			VarData[] test2 = VarData.rows(test);
			pp.setTickSize(3);
			pp.yMarkup(0, 10, 0);
			pp.drawYAxis();
			pp.labelXCorrdinate(0, "t=0");
			pp.labelXCorrdinate(1, "24h");
			pp.labelXCorrdinate(2, "48h");
			pp.labelXCorrdinate(3, "72h");
			pp.labelXCorrdinate(4, "5d");
			pp.labelXCorrdinate(5, "7d");
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
			pp.plotColumnScatters(day, ctrl);
			pp.rendFill("blue");
			pp.plotColumnScatters(day, test);
			SVG.writeToFile(pp.getSVG(), new File(src, c + ".svg"));
			Syo.pl();
		}
	}
}
