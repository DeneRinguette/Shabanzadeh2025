package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Xlsx;

/**
 * @author Dene Ringuette
 */

public class GelZymography 
{
	public static void main(String[] args) throws IOException
	{
		for(int i = 2; i <= 10; i += 7)
		{
			File dir = new File(Directory.ZYMO, "MMP"+i);
			Xlsx xlsx = new Xlsx(new File(dir, "MMP"+i+" blots.xlsx"));
			xlsx.setSheet(0);
			
			double[] ctrl = xlsx.getNumericColumn("Control");
			double[] rgma = xlsx.getNumericColumn("RGMa");
			double[] h151 = xlsx.getNumericColumn("RGMa-H151A");
			
			String[] labels = new String[]{"Vehicle", "RGMa", "RGMa-H151A"};
			double[][] data = new double[][] {ctrl, rgma, h151};
			Syo.table("MMP"+i, labels, data);
			
			Syo.pl("p01="+new TTest().pairedTTest(ctrl, rgma));
			Syo.pl("p12="+new TTest().pairedTTest(rgma, h151));	
			
			Plot p = new Plot(new Box(50, 50, 60, 80), Span.bar(3, 0, 4));
			p.drawYAxis();
			p.yMarkup(0, 2, 0);
			p.rendPtRad(2);
			p.plotColumnScatter(data, SvgColor.getDistinct(3, 0f, .7f, .8f));
			p.plotColumnSems(data, 0.75);
			p.plotColumnPairedPValues(data, new int[] {0,1,1,2});
			p.setTextAngle(-45);
			p.labelColumns(labels);
			SVG.writeToFile(p.getSVG(), new File(dir, "MMP"+i+".svg"));
			Syo.pl();
		}
	}
}
