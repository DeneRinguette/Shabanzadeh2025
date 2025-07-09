package shabanzadeh2025.ihc;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class CD45_CD68_colabel 
{
	static File SRC = new File(Directory.IHC, "CD45-CD68");
	
	public static void main(String[] args) throws IOException
	{
		graph(new File(SRC, "CD45 positive per mm2.tab"), 400);
		graph(new File(SRC, "CD68 positive per mm2.tab"), 300);
	}
	
	public static void graph(File tab, double max) throws IOException
	{
		String[] labels = new String[] {"Vehicle", "PF429242"};
		double[][] data = Parser.getDoubles(tab, 1, 2);		
		Syo.pl(Stat.mean(data[0]));
		Syo.pl(Stat.mean(data[1]));
		double[] ratio = Err.div(data[1], data[0]);
		ratio[0] -= 1.0;
		Ip.mult(ratio, 100);
		Syo.pl(ratio);
		
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.5, 1.5, 0, max));		
		Text label = new Text(8);		
		p.drawYAxis();
		p.yMarkup(0, 100, 0);		
		Syo.table(tab.getName(), labels, data);		
		p.plotColumnSems(data, 0.5);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		p.plotColumnPValues(data, new int[] {0,1}, false);	
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns(labels);		
		SVG.writeToFile(p.getSVG(), new File(SRC, Extension.change(tab.getName(), "svg")));	
		Syo.pl();
	}
}	

