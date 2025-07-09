package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class RelativeNeoAPBinding 
{
	public static final File DATA = Directory.NEOAP;
	
	public static void main(String[] args) throws IOException
	{
		double[] WT = {1,1,1,1};
		
		double[] control = {
				0.042340262,
				0.06122449,
				0.041543027,
				0.061,
			};
		
		double[] RGMa_F147A = {
				0.040800616,
				0.091428571,
				0.011869436,
				0.091,
			};	
		
		double[] RGMa_D149A = {
				0.037721324,
				0.110204082,
				0.020771513,
				0.11,
			};
		
		double[] RGMa_P151A = {
				0.029253272,
				0.026706231,
				0.0292
			};
		
		Plot bb = new Plot(new Box(100,100,120,80), Span.bar(5, -0.1, 1.1));
		String[] labels = new String[] {"RGMa", "Albumin", "F147A", "D149A", "H151A"};
		double[][] all = new double[][]{WT, control, RGMa_F147A, RGMa_D149A, RGMa_P151A};
		Syo.table("Relative Neo-AP binding", labels, all);		
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, 0.2, 1);
		bb.setTextAngle(-45);
		bb.labelColumns(labels);		
		bb.setTextAngle(-90);
		GraphRender rend = bb.getGraphRender();
		rend.text = rend.text.changeAlignment(TextAlignment.OVER);
		bb.drawText(100-25, 100+40, "Relative Neo-AP binding");		
		bb.rendPtRad(2.0);
		bb.plotColumnScatter(all, SvgColor.getDefaultHSVDistinct(5));
		bb.plotColumnSems(all, 0.5);
		bb.setTextAngle(-45);
		bb.plotColumnPValues(all, new int[]{0,1,0,2,0,3,0,4}, true);	
		SVG.writeToFile(bb.getSVG(), new File(DATA, "graph.svg"));
	}
}
