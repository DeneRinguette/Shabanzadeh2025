package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class MouseTTC
{
	public static File src = new File(Directory.TTC, "Mice");
	
	public static void main(String[] args) throws IOException
	{
		infarction_and_edema(true);
		infarction_and_edema(false); 
	}
	
	public static void infarction_and_edema(boolean i) throws IOException
	{
		File a = new File(src, "Infarction and Edema Quantification.tab");
		String title = i ? "infracton" : "edema";
		String[] headers = new String[]{"Vehicle", "PF429242", "RGMa", "Tie2 -TAM", "Tie2 +TAM"};
		double[][] data = Parser.getDoubles(a, 1, new int[] {2,3});
		double[] using = data[i ? 0 : 1];
		double[][] col = Op.to2D(using, 7);
		
		Syo.table(title, headers, col);		
		for(double[] g : col)
			Syo.pl(Err.mean(g));
		
		Plot p = new Plot(new Box(50, 50, 120, 80), Span.bar(5, 0, i ? 50 : 10));
		p.drawYAxis();
		p.yMarkup(0, i ? 10 : 2, 0);
		p.rendPtRad(2);
		p.plotColumnScatter(col, SvgColor.getDistinct(5, 0f, .7f, .8f));
		p.plotColumnSems(col, 0.75);
		p.plotColumnPValues(col, new int[] {0,1, 0,2, 3,4}, false);
		p.setTextAngle(-45);
		p.labelColumns(headers);
		SVG.writeToFile(p.getSVG(), new File(src, title + ".svg"));
		Syo.pl();
	}
}
