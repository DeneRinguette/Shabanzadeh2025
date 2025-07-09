package shabanzadeh2025.od;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Syo;

public class RetinalArteryOcclusion 
{
	static File SRC = new File(Directory.EBOD, "Retina");
	
	public static void main(String[] args) throws IOException
	{
		graph(new File(SRC, "optical_density_per_gram.tab"), 5);
	}
	
	public static void graph(File tab, double max) throws IOException
	{
		String[] headers = new String[] {"Vehicle", "PF-429242"};
		double[][] data = Parser.getDoubles(tab, 1, 2);
		Syo.table("Optical density / gram", headers, data);
		
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.5, 1.5, 0, max));
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(0, max/5, 0);
		p.plotColumnSems(data, 0.5);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, SvgColor.getDistinct(2, 0.0f, 0.7f, 0.8f));
		p.plotColumnPValues(data, new int[] {0,1}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns(headers);	
		SVG.writeToFile(p.getSVG(), new File(SRC, Extension.change(tab.getName(), ".svg")));
	}
}
