package shabanzadeh2025.blots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.Box;
import shabanzadeh2025.rend.Plot;
import shabanzadeh2025.rend.SVG;
import shabanzadeh2025.rend.Span;
import shabanzadeh2025.rend.SvgColor;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Xlsx;

/**
 * @author Dene Ringuette
 */

public class BSG 
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		File dir = new File(Directory.WESTERN, "BSG");
		Xlsx xlsx = new Xlsx(new File(dir, "BSG blots.xlsx"));
		xlsx.setSheet(0);
		
		double[] ctrl = xlsx.getNumericColumn("Control");
		double[] rgma = xlsx.getNumericColumn("RGMa");
		double[] h151 = xlsx.getNumericColumn("RGMa-H151A");
		
		String[] labels = new String[]{"Vehicle", "RGMa", "RGMa-H151A"};
		double[][] data = new double[][] {ctrl, rgma, h151};
		Syo.table("BSG/CD147 relative abundance", labels, data);	
		Syo.pl("p01 = "+new TTest().pairedTTest(ctrl, rgma));	
		Syo.pl("p12 = "+new TTest().pairedTTest(rgma, h151));
		
		Plot p = new Plot(new Box(50, 50, 60, 80), Span.bar(3, 0, 6));
		p.drawYAxis();
		p.yMarkup(0, 2, 0);
		p.rendPtRad(2);
		p.plotColumnScatter(data, SvgColor.getDistinct(3, 0f, .7f, .8f));
		p.plotColumnSems(data, 0.75);
		p.plotColumnPairedPValues(data, new int[] {0,1,1,2});
		p.setTextAngle(-45);
		p.labelColumns(labels);
		SVG.writeToFile(p.getSVG(), new File(dir, "BSG blots.svg"));
	}
}
