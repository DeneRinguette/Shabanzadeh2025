package shabanzadeh2025.od;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Syo;

public class LiverIschemia 
{
	public static void main(String[] args) throws IOException
	{
		String[] headers = new String[] {"Sham", "Vehicle", "PF-429242", "Vehicle", "PF-429242"};		
		double[] sham = new double[] {12.94666667, 11.14953271, 5.961904762, 9.544, 31.60588235, 11.05263158};
		double[] ctrl_30min = new double[] {31.22142857, 26.64233577, 17.0718232, 18.42631579, 32.54166667, 18.89473684};
		double[] pf42_30min = new double[] {27.03076923, 17.6, 28.92857143, 21.57462687, 24.60264901, 20.30456853};
		double[] ctrl_60min = new double[] {19.9609375, 35.28571429, 13.50691244, 35.42666667, 34.4625};
		double[] pf42_60min = new double[] {8.536585366, 38.048, 21.0173913, 30.00625, 35.45333333};
		double[][] data = new double[][] {sham, ctrl_30min, pf42_30min, ctrl_60min, pf42_60min};
		
		Syo.table("Optical density / gram", headers, data);
		
		Plot p = new Plot(new Box(50, 50, 80, 80), Span.bar(3, 0, 40));	
		Text label = new Text(8);		
		p.drawYAxis();
		p.yMarkup(0, 10, 0);		
		p.plotColumnSems(data, 0.5);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, SvgColor.getDistinct(5, 0.0f, 0.7f, 0.8f));
		p.plotColumnPValues(data, new int[] {0,1, 1,2, 0,3, 3,4}, false);	
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns(headers);	
		SVG.writeToFile(p.getSVG(), new File(Directory.EBOD, "Liver\\graph.svg"));	
	}
}
