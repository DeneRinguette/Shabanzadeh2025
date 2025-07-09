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
 * Graphing complementary BSG histological quantification.
 * 
 * @author Dene Ringuette
 */

public class Bsg 
{
	public static void main(String[] args) throws IOException
	{	
		double[] control_isol = new double[] {52, 50, 25, 32, 39};
		double[] control_both = new double[] {24, 25, 12, 22, 18};
		
		double[] pf429242_isol = new double[] {32, 45, 30, 60, 21};
		double[] pf429242_both = new double[] {7, 16, 12, 15, 4};
		
		double[] control = Op.compDiv(control_both, control_isol);
		double[] pf429242 = Op.compDiv(pf429242_both, pf429242_isol);
		
		Ip.mult(control, 100);
		Ip.mult(pf429242, 100);
		
		Syo.pl("p = " + new TTest().homoscedasticTTest(control, pf429242));
		
		String title = "BSG";
		String[] labels = new String[] {"Vehicle", "PF-429242"}; 
		double[][] data = new double[][]{control, pf429242};		
		Syo.table(title, labels, data);
		
		Plot pp = new Plot(new Box(50, 50, 60, 80), new Span(-0.75, 1.75, 0, 75));
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
		SVG.writeToFile(pp.getSVG(), new File(Directory.IHC, "BSG\\graph.svg"));
	}
	

}
