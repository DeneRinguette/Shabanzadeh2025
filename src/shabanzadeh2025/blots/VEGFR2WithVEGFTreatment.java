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
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class VEGFR2WithVEGFTreatment
{
	public static void main(String[] args) throws IOException
	{
		File SRC = new File(Directory.WESTERN, "VEGF-R2 with VEGF treatment");
		
		double[] VEGF = new double[] {3.235395, 3.397427, 1.532852, 1.239092, 1.421164};
		double[] CTRL = new double[] {1, 1, 1, 1, 1};
		double[] VEGF_RGMA = new double[] {0.514759, 1.184027, 0.965726007, 0.653995471, 0.956860413};
		double[] VEGF_H151 = new double[] {0.933036, 1.080897, 2.543917265, 1.344774806, 1.946967125};
		String[] labels = new String[] {"VEGF-A", "Ctrl", "RGMa+VEGF-A", "H151+VEGF-A"};
		double[][] all = new double[][]{VEGF, CTRL, VEGF_RGMA, VEGF_H151};
		Syo.table("log(FC)+1 VEGF-R2 / GAPDH with VEGF treatment", labels, all);
		
		// assess re-scale validity
		{
			Syo.pl("std " + Stat.std(VEGF));
			Syo.pl("std " + Stat.std(VEGF_RGMA));
			Syo.pl("std " + Stat.std(VEGF_H151));
			
			Syo.pl("mean " + Stat.mean(VEGF));
			Syo.pl("mean " + Stat.mean(VEGF_RGMA));
			Syo.pl("mean " + Stat.mean(VEGF_H151));
			
			double[] std = new double[]{1.0576897355366082, 0.2678905050493806, 0.6682608564498529};
			double[] mean = new double[]{2.165186, 0.8550735782000001, 1.5699184391999998};
			
			Plot p = new Plot(new Box(100, 100, 80, 80), new Span(0, 2.5, 0, 1.25));
			p.drawBox();
			p.yMarkup(0, .25, 2);
			p.xMarkup(0, .5, 1);
			p.plotPoints(mean, std);
			SVG.writeToFile(p.getSVG(), new File(SRC, "correlation.svg"));
		}
		
		Ip.apply(all, (double x) -> Math.log(x)+1);
		Syo.table("log(FC)+1 VEGF-R2 / GAPDH with VEGF treatment", labels, all);
		
		TTest t = new TTest();
		Syo.pl();
		Syo.pl(t.tTest(1, VEGF));
		Syo.pl(t.tTest(1, VEGF_RGMA));
		Syo.pl(t.tTest(1, VEGF_H151));
		Syo.pl(t.homoscedasticTTest(VEGF_RGMA, VEGF_H151));
		Syo.pl(t.homoscedasticTTest(VEGF, VEGF_RGMA));
		Syo.pl(t.homoscedasticTTest(VEGF, VEGF_H151));
		
		Plot bb = new Plot(new Box(100, 100, 106, 80), Span.bar(all.length, 0, 3));
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
		bb.plotColumnPValues(all, new int[]{1,0}, true);
		bb.plotColumnPValues(all, new int[]{0,2}, false);
		bb.plotColumnPValues(all, new int[]{2,3}, false);
		SVG.writeToFile(bb.getSVG(), new File(SRC, "Vegf-r2 with Vegf-a treatment.svg"));
	}
}
