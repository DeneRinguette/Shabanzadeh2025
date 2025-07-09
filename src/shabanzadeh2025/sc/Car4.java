package shabanzadeh2025.sc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Syo;

/**
 * Graphing complementary Car4 histological quantification.
 * 
 * @author Dene Ringuette
 */

public class Car4
{	
	public static void main(String[] args) throws IOException
	{
		final double[] ctrl = {56, 62, 49, 54, 45};
		final double[] pf42 = {33, 20, 32, 24, 40};
		
		TTest test = new TTest();
		Syo.pl(test.homoscedasticTTest(ctrl, pf42));
		
		double fov = 0.2125481;
		
		Ip.div(ctrl, fov*fov);
		Ip.div(pf42, fov*fov);
		
		String title = "Car4";
		String[] labels = new String[] {"Vehicle", "PF-429242"}; 
		double[][] data = new double[][]{ctrl, pf42};		
		Syo.table(title, labels, data);
		
		Ip.div(ctrl, 1000);
		Ip.div(pf42, 1000);
		
		Plot p = new Plot(new Box(50,50,80,80), new Span(-0.5, 1.5, 0, 1.5));
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(0, 0.500, 1);
		p.plotColumnSems(data, 0.5);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		p.plotColumnPValues(data, new int[] {0,1}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns("Vehicle", "PF429242");
		SVG.writeToFile(p.getSVG(), new File(Directory.IHC, "CAR4\\graph.svg"));
	}
}
