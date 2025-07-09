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
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class CD147WithNetrin1Treatment 
{
	public static void main(String[] args) throws IOException
	{
		File SRC = new File(Directory.WESTERN, "CD147 with Netrin-1 treatment");
		
		double[] CTRL = new double[] {1, 1, 1};
		double[] NETRIN = new double[] {1.284921297, 1.469570411, 2.041175261};
		double[] RGMA = new double[] {2.288699748, 2.527937062, 1.671002968};
		double[] NETRIN_RGMA = new double[] {2.048530331, 2.279019544, 0.170758513};
		double[] H151 = new double[] {1.005287962, 1.242556969, 0.266029836};
		double[] NETRIN_H151 = new double[] {3.129992738, 1.458540329, 0.359991884};
		String[] labels = new String[] {"Ctrl", "Netrin-1", "RGMa", "Netrin-1 + RGMa", "H151A", "Netrin-1 + H151A"};
		double[][] all = new double[][]{CTRL, NETRIN, RGMA, NETRIN_RGMA, H151, NETRIN_H151};
		Syo.table("FC CD147 / GAPDH with Netrin-1 treatment", labels, all);
		
		Syo.pl(Stat.mean(RGMA)-1);
		Syo.pl(Stat.mean(NETRIN)-1);
		Syo.pl(Err.div(Op.subtract(RGMA, 1), Op.subtract(NETRIN, 1)));
		Syo.pl(Err.div(Op.subtract(NETRIN, 1), Op.subtract(RGMA, 1)));
		
		TTest t = new TTest();
		Syo.pl(t.tTest(1, NETRIN));
		Syo.pl(t.tTest(1, RGMA));
		Syo.pl(t.tTest(1, NETRIN_RGMA));
		Syo.pl(t.tTest(1, H151));
		Syo.pl(t.tTest(1, NETRIN_H151));
		Syo.pl(t.homoscedasticTTest(NETRIN, RGMA));
		
		Plot bb = new Plot(new Box(100, 100, 80 + 3*26 , 80), Span.bar(all.length, 0, 3));
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, 1.0, 1);
		bb.setTextAngle(-45);
		bb.labelColumns(labels);
		bb.rendPtRad(2.0);
		bb.plotColumnScatter(all, SvgColor.getDefaultHSVDistinct(all.length));
		bb.plotColumnSems(all, 0.5);	
		bb.setTextAngle(0);
		bb.plotColumnPValues(all, new int[]{0,1}, true);
		bb.plotColumnPValues(all, new int[]{0,2}, true);
		bb.plotColumnPValues(all, new int[]{2,4}, false);
		bb.plotColumnPValues(all, new int[]{2,3}, false);
		bb.plotColumnPValues(all, new int[]{4,5}, false);
		SVG.writeToFile(bb.getSVG(), new File(SRC, "cd147 with Netrin.svg"));
	}
}
