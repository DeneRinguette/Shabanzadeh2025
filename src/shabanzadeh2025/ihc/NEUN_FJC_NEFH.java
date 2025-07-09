package shabanzadeh2025.ihc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class NEUN_FJC_NEFH 
{
	public static void main(String[] args) throws IOException
	{
		switch(3)
		{
			case 1: neun(); break;
			case 2: fjc(); break;
			case 3: nefh(); break;
		}
	}
	
	protected static void fjc() throws IOException
	{
		File dst = new File(Directory.IHC, "FJC");
		{
			double[] control = new double[]{759.836282, 820.718314, 737.2093023, 531, 565.5555556, 495.0289975, 378.2318599};
			double[] rgma = new double[]{509.1029827, 110.6727845, 199.2141996, 395.266269, 221.3491107, 47.43195228, 129.1203145};
			graph("FJC", dst, 900, "RGMa-WT", control, "RGMa-H151A", rgma);
		}
		
		{
			double[] control = new double[]{793.1764689, 818.9917547, 1328.094737, 623.8020736, 818, 565.6699808, 729.5864541};
			double[] tie2 = new double[]{276.6864036, 221.3491107, 85.13427804, 406.8917475, 244.1350485, 101.7229369, 589.9930339};
			graph("FJC", dst, 1500, "Tie2Cre,TAM-", control, "Tie2Cre,TAM+", tie2);
		}
		
		{
			double[] control = new double[]{868.1776352, 664.047332, 442.5, 590.2643277, 482.9488859, 442.6982458, 426.8877832};
			double[] pf4292 = new double[]{343.812, 387.3609651, 268.7810778, 270.5378169, 166.0119157, 146.0904858, 94.17403216};
			graph("FJC", dst, 900, "Vehicle", control, "PF-429242", pf4292);
		}
	}
	
	protected static void nefh() throws IOException
	{
		// Fov = 212.55 um x 212.55 um
		final double fov = 0.0451775025; // mm^2
		
		File dst = new File(Directory.IHC, "NEFH");
		{
			double[] control = new double[]{205, 165, 54, 42, 60, 158, 181};
			double[] rgma = new double[]{353, 173, 322, 183, 201, 284, 246};
			Ip.div(control, fov);
			Ip.div(rgma, fov);
			graph("NEFH", dst, 8000.0, "RGMa-WT", control, "RGMa-H151A", rgma);
		}
		
		{
			double[] control = new double[]{53, 162, 111, 128, 101, 146, 54};
			double[] tie2 = new double[]{326, 220, 285, 276, 137, 274, 292};
			Ip.div(control, fov);
			Ip.div(tie2, fov);
			control[0] *= fov/0.0182115025; // different FOV for first control point
			graph("NEFH", dst, 8000.0, "Tie2Cre,TAM-", control, "Tie2Cre,TAM+", tie2);
		}
		
		{
			double[] control = new double[]{149, 157, 125, 149, 136, 149, 178};
			double[] pf4292 = new double[]{320, 347, 307, 127, 292, 199, 266};			
			Ip.div(control, fov);
			Ip.div(pf4292, fov);
			control[0] *= fov/0.077587015; // different FOV for first control point
			graph("NEFH", dst, 8000.0, "Vehicle", control, "PF-429242", pf4292);
		}
	}	
	
	protected static void neun() throws IOException
	{
		File dst = new File(Directory.IHC, "NeuN");
		{
			double[] control = new double[]{907.5314, 1195.285, 818.9917, 1173.15, 664.0473, 1151.015, 1881.467};	
			double[] rgma = new double[]{1239.555, 1549.444, 1792.928, 2235.626, 2479.11, 2966.078, 2014.277};
			graph("NeuN", dst, 3200, "RGMa-WT", control, "RGMa-H151A", rgma);
		}
		
		{
			double[] control = new double[]{885.3964, 1084.611, 708.3171, 575.5077, 531.2379, 1084.611, 1615.849};
			double[] tie2 = new double[]{2368.436, 1660.118, 1903.602, 1217.42, 2523.38, 1837.198, 1970.007};
			graph("NeuN", dst, 2800, "Tie2Cre,TAM-", control, "Tie2Cre,TAM+", tie2);
		}
		
		{
			double[] control = new double[]{1328.095, 996.071, 641.9124, 929.6663, 951.8011, 708.3171, 332.0237};	
			double[] pf4292 = new double[]{1615.849, 1350.23, 1970.007, 1460.904, 2479.11, 1881.467, 1660.118};
			graph("NeuN", dst, 2800, "Vehicle", control, "PF-429242", pf4292);
		}
	}
	
	protected  static void graph(String title, File dst, double max, String name_control, double[] data_control, String name_treatment, double[] data_treatment) throws IOException
	{
		Syo.p("fold = ");
		double[] r = Err.div(data_treatment, data_control);
		r[0] = r[0]-1.0;
		Ip.mult(r, 100);
		Syo.pl(r);
		
		Plot bb = new Plot(new Box(100,100,80,100), Span.bar(2, 0, max));
		bb.rendText(8);
		bb.rendWidth(1);
		bb.drawYAxis();
		String[] labels = new String[] {name_control, name_treatment}; 
		double[][] all = new double[][]{data_control, data_treatment};		
		Syo.table(title, labels, all);
		bb.setTickSize(3);
		bb.yMarkup(0, max/4, 0);
		bb.setTextAngle(-45);
		bb.labelColumns(name_control, name_treatment);
		bb.rendPtRad(2.0);
		bb.setTextAngle(0);
		bb.plotColumnSems(all, 0.75);
		bb.plotColumnScatter(all, new String[]{"red" , "blue"});
		bb.plotColumnPValues(all, new int[]{0,1}, false);	
		SVG.writeToFile(bb.getSVG(), new File(dst, "summary--" + name_control + "--v--" + name_treatment + ".svg"));
		
		Syo.pl("p = "+new TTest().homoscedasticTTest(data_control, data_treatment));
		Syo.pl();
	}
}
