package shabanzadeh2025.mri;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * Graphing recorded Neurological severity scores.
 * 
 * @author Dene Ringuette
 */

public class RabbitNeurologicalScores 
{
	public static void main(String[] args) throws IOException
	{
		double[][] ctrl = new double[][]{
				{0,6,6,5,3,2,3},
				{0,6,4,3,4,4,3},
				{0,5,5,5,5,5,5},
				{0,5,5,4,4,3,3},
				{0,4,4,4,4,4,4}
			};
		
		double[][] test = new double[][]{
				{0,5,5,3,2,2,2},
				{0,5,4,2,2,1,1},
				{0,4,4,3,4,2,1},
				{0,4,4,5,3,1,1},
				{0,6,3,4,2,1,1},
				{0,3,3,3,3,2,2}
			};
		
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 6.5, 0, 8));
		{
			double[][][] data = new double[][][] {ctrl, test}; 
			RepeatedMeasuresFactorialANOVA.p_value(data, true);
		}
		String[] times = new String[] {"t=0", "6h", "24h", "48h", "72h", "5d", "7d"};
		Syo.table("ctrl", times, Op.transpose(ctrl));
		Syo.table("test", times , Op.transpose(test));
		double[] day = new double[] {0,1,2,3,4,5,6};
		VarData[] ctrl2 = VarData.fullColumns(ctrl);
		VarData[] test2 = VarData.fullColumns(test);
		pp.setTickSize(3);
		pp.yMarkup(0, 2, 0);
		pp.drawYAxis();
		for(int i = 0; i < times.length; i++)
			pp.labelXCorrdinate(i, times[i]);
		pp.rendWidth(1.0);
		pp.rendColor("red");
		pp.plotError(day, ctrl2);
		pp.rendColor("blue");
		pp.plotError(day, test2);
		pp.rendColor("black");
		pp.plotSignificance(day, false, 0, ctrl2, test2);
		pp.rendColor("none");
		pp.rendFillAlpha(0.5);
		pp.rendPtRad(1.5);
		pp.rendFill("red");
		pp.plotColumnScatter(Op.transpose(ctrl));
		pp.rendFill("blue");
		pp.plotColumnScatter(Op.transpose(test));
		SVG.writeToFile(pp.getSVG(), new File(Rabbit.SRC, "neurological.svg"));
	}
}
