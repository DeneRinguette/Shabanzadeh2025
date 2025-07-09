package shabanzadeh2025.sc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Syo;

/**
 * Graphing complementary Car4 histological quantification.
 * 
 * @author Dene Ringuette
 */

public class Sdf1 
{
	public static void main(String[] args) throws IOException
	{
		double[] control_both = new double[] {28, 25, 46, 17, 44};
		double[] control_isol = new double[] {45, 53, 65, 39, 70};
		
		double[] pf429242_both = new double[] {19, 12, 8, 4, 8};
		double[] pf429242_isol = new double[] {39, 37, 52, 42, 40};
		
		double[] control = Op.compDiv(control_both, control_isol);
		double[] pf429242 = Op.compDiv(pf429242_both, pf429242_isol);
		
		Ip.mult(control, 100);
		Ip.mult(pf429242, 100);
		
		String title = "SDF1";
		String[] labels = new String[] {"Vehicle", "PF-429242"}; 
		double[][] data = new double[][]{control, pf429242};
		
		Syo.table(title, labels, data);
		Syo.pl("p = " +new TTest().homoscedasticTTest(control, pf429242));
		
		Plot pp = new Plot(new Box(50, 50, 60, 80), new Span(-0.75, 1.75, 0, 100));
		pp.drawYAxis(); 
		pp.setTickSize(3);
		pp.yMarkup(0, 25, 0);
		pp.rendColor("black");
		pp.rendText(10);
		pp.rendPtRad(2);
		pp.plotColumnSems(data, 0.75);
		pp.plotColumnScatter(data, new String[]{"red", "blue"});
		pp.plotColumnPValues(data, new int[]{0,1}, false);		 
		pp.rendText(8);
		pp.getGraphRender().text = pp.getGraphRender().text.changeTransform(new TextOrientation(-45));
		pp.labelXCorrdinate(0, "Vehicle");
		pp.labelXCorrdinate(1, "PF-429242");
		SVG.writeToFile(pp.getSVG(), new File(Directory.IHC, "SDF1\\graph.svg"));
	}
}
