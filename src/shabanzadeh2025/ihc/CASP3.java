package shabanzadeh2025.ihc;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class CASP3 
{
	static File SRC = new File(Directory.IHC, "CASP3");
	
	public static void main(String[] args) throws IOException
	{
		graph(new File(SRC, "Colocate CASP3 to endo.tab"), 200, 2);
		graph(new File(SRC, "CASP3 per mm2.tab"), 3000, 3);
	}
	
	public static void graph(File tab, double max, int split) throws IOException
	{
		String[] labels = new String[] {"Vehicle", "PF-429242"};
		double[][] data = Parser.getDoubles(tab, 1, 2);
		Syo.table(tab.getName(), labels, data);
		
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.5, 1.5, 0, max));
		
		Text label = new Text(8);
		
		p.drawYAxis();
		p.yMarkup(0, max/split, 0);
		
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
