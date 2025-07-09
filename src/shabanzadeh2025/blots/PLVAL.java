package shabanzadeh2025.blots;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.Box;
import shabanzadeh2025.rend.Plot;
import shabanzadeh2025.rend.SVG;
import shabanzadeh2025.rend.Span;
import shabanzadeh2025.rend.SvgColor;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class PLVAL 
{
	public static void  main(String[] args) throws IOException
	{
		File SRC = new File(Directory.WESTERN, "PLVAP");
		
		String[] labels = new String[] {"Ctrl", "RGMa", "H151A"};
		double[] CTRL = new double[] {1, 1, 1, 1};
		double[] RGMA = new double[] {0.999430959, 1.310653276, 0.775074136, 0.895464384};
		double[] H151 = new double[] {0.775267559, 0.951086282, 0.986286408, 1.392827356};
		double[][] all = new double[][]{CTRL, RGMA, H151};
		Syo.table("FC PLVAP / GAPDH", labels, all);
		
		TTest t = new TTest();
		Syo.pl(t.tTest(1, RGMA));
		Syo.pl(t.tTest(1, H151));
		Syo.pl(t.homoscedasticTTest(RGMA, H151));
		
		Plot bb = new Plot(new Box(100, 100, 80 , 80), Span.bar(3, 0, 2.0));
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, 0.5, 1);
		bb.setTextAngle(-45);
		bb.labelColumns(labels);
		bb.rendPtRad(2.0);
		bb.plotColumnScatter(all, SvgColor.getDefaultHSVDistinct(all.length));
		bb.plotColumnSems(all, 0.5);
		bb.setTextAngle(0);
		bb.plotColumnPValues(all, new int[]{0,1}, true);
		bb.plotColumnPValues(all, new int[]{1,2}, false);
		SVG.writeToFile(bb.getSVG(), new File(SRC, "plvap.svg"));
	}
}
