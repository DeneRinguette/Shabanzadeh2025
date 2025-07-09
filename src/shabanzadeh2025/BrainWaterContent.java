package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class BrainWaterContent 
{	
	static File SRC = Directory.BWC;
	
	public static void main(String[] args) throws IOException
	{
		graph(new File(SRC, "BWC.tab"));
	}
	
	public static void graph(File tab) throws IOException
	{
		String[] labels = new String[]{"Contra.", "Ipsi.", "Contra.", "Ipsi."};
		double[][] data = Parser.getDoubles(tab, 2, 4);
		
		Plot p = new Plot(new Box(50,50,120,80), Span.bar(4, 60, 100));
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(60, 10, 0);
		
		double[] temp;
		temp = data[1];
		data[1] = data[0];
		data[0] = temp;
		
		temp = data[3];
		data[3] = data[2];
		data[2] = temp;
		
		Syo.table("Brain water content (%)", labels, data);
		
		p.plotColumnSems(data, 0.75);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, SvgColor.getDistinct(4, 0.6f, 0.7f, 0.8f));
		p.plotColumnPValues(data, new int[] {0,2}, false);
		p.plotColumnPValues(data, new int[] {1,3}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns(labels);	
		SVG.writeToFile(p.getSVG(), new File(SRC, Extension.change(tab.getName(), ".svg")));	
	}
}
