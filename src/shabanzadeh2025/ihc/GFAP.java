package shabanzadeh2025.ihc;

import java.io.File;
import java.io.IOException;
import loci.formats.FormatException;
import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.ANOVA;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class GFAP 
{
	public static void main(String[] args) throws FormatException, IOException
	{
		double[] rgma_ctrl = new double[]{
				2.671206,
				3.274258,
				1.492212,
				2.705411,
				4.503131,
				4.305705
			};		
		double[] rgma = new double[]{
				2.439481,
				1.955794,
				1.078963,
				1.269177,
				1.289937,
				1.305353
			};		
		double[] tie_ctrl = new double[]{
				4.056362,
				9.020163,
				6.032409,
				7.466151,
				1.067963,
				4.314291
			};
		double[] tie = new double[]{
				4.311789,
				1.950787,
				1.161429,
				1.350328,
				1.020518,
				1.824985
			};
		double[] pf_ctrl = new double[]{
				5.663514,
				7.58277,
				4.198429,
				2.26755,
				6.472158,
				13.73478
			};
		double[] pf = new double[]{	
				4.250227,
				1.279214,
				1.015937,
				1.711952,
				1.047944,
				3.978916
			};			
		graph("RGMa-F155A and WT", "WT", rgma_ctrl, "RGMa-F155A/+", rgma, 5);
		graph("NeoTie2creERT2 Tamoxifen and Vehicle", "w/ Vehicle", tie_ctrl, "w/ Tamoxifen", tie, 10);
		graph("PF-429242 Treatment and Vehicle", "Vehicle", pf_ctrl, "PF-429242", pf, 15);	
	}
	
	static void graph(String name, String ctrl_name, double[] ctrl, String treat_name, double[] treat, double max) throws IOException
	{	
		String[] labels = new String[] {ctrl_name, treat_name}; 
		double[][] all = new double[][]{ctrl, treat};		
		Syo.table("Relative volume GFAP+", labels, all);
				
		printRatio(ctrl, treat);
		
		Plot bb = new Plot(new Box(200,100,80,100), Span.bar(2, 0.0, max));
		bb.rendText(8);
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, max/5, 0);
		bb.setTextAngle(-45);
		bb.labelColumns("WT", "PF");
		bb.rendPtRad(2.0);
		bb.setTextAngle(0);
		bb.plotColumnSems(all, 0.75);
		bb.plotColumnScatter(all, new String[]{"red" , "blue"});
		bb.plotColumnPValues(all, new int[]{0,1}, false);
		SVG.writeToFile(bb.getSVG(), new File(Directory.IHC, "GFAP\\"+name+".svg"));
		Syo.pl(ANOVA.anova(new double[][]{ctrl, treat}));
		Syo.pl();
	}
	
	public static void printRatio(double[] data_control, double[] data_treatment)
	{
		Syo.p("fold = ");
		double[] r = Err.div(data_treatment, data_control);
		r[0] = r[0]-1.0;
		Ip.mult(r, 100);
		Syo.pl(r);
	}
}
