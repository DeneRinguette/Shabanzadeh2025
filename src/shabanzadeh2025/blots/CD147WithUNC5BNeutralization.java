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

public class CD147WithUNC5BNeutralization 
{
	public static void main(String[] args) throws IOException
	{
		File SRC = new File(Directory.WESTERN, "CD147 with UNC5B neurtralization");
		
		double[] CTRL = new double[] {1, 1, 1};
		double[] UNC5B = new double[] {0.551392809, 0.185028808, 0.429446161};
		double[] UNC5B_RGMA = new double[] {0.212483409, 0.203182254, 0.120106308};
		double[] RGMA = new double[] {1.457595527, 1.525792686, 1.92032894};
		double[] UNC5B_H151 = new double[] {0.548595456, 0.308214518, 0.481816308};
		double[] H151 = new double[] {1.188097188, 1.054489702, 0.747785579};
		String[] labels = new String[] {"Ctrl", "UNC5B", "UNC5B+RGMa", "RGMa", "UNC5B+H151A", "H151A"};
		double[][] all = new double[][]{CTRL, UNC5B, UNC5B_RGMA, RGMA, UNC5B_H151, H151};
		Syo.table("FC CD147 / GAPDH with UNC5B neuralization", labels, all);
		Syo.pl();
		TTest t = new TTest();
		Syo.pl(t.tTest(1, UNC5B));
		Syo.pl(t.tTest(1, RGMA));
		Syo.pl(t.tTest(1, UNC5B_RGMA));
		Syo.pl(t.tTest(1, H151));
		Syo.pl(t.tTest(1, UNC5B_H151));
		Syo.pl(t.homoscedasticTTest(UNC5B, RGMA));
		
		Plot bb = new Plot(new Box(100, 100, 80 + 3*26, 80), Span.bar(all.length, 0, 2));		
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, 1.0, 1);
		bb.setTextAngle(-45);
		bb.labelColumns(labels);
		bb.setTextAngle(0);
		bb.rendPtRad(2.0);
		bb.plotColumnScatter(all, SvgColor.getDefaultHSVDistinct(all.length));
		bb.plotColumnSems(all, 0.5);
		bb.plotColumnPValues(all, new int[]{0,1}, true);
		bb.plotColumnPValues(all, new int[]{0,2}, true);
		bb.plotColumnPValues(all, new int[]{0,4}, true);
		bb.plotColumnPValues(all, new int[]{2,3}, false);
		bb.plotColumnPValues(all, new int[]{4,5}, false);
		SVG.writeToFile(bb.getSVG(), new File(SRC, "cd147 with Unc5b.svg"));
	}
}
