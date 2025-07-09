package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Syo;

public class RGMaELISA 
{	
	public static void main(String[] args) throws IOException
	{
		double[] CTRL = new double[] {
				7.620205, 
				9.525575,
				3.707161,
				7.172634,
				6.098465,
				5.663683,
				4.3834383,
				7.2637264
			};
		
		double[] MCAO = new double[] {
				8.719949,
				6.303069,
				8.451407,
				15.86859,
				12.91629,
				11.51215
			};
		
		Plot bb = new Plot(new Box(100, 100, 80 , 80), Span.bar(2, 0, 20));
		
		double[][] all = new double[][]{CTRL, MCAO};
		
		TTest t = new TTest();
		Syo.pl(t.homoscedasticTTest(MCAO, CTRL));
		
		Syo.table("Concentration of RGMa (ng/mL)", new String[] {"Sham", "MCAO"}, new double[][] {CTRL, MCAO});
		bb.rendWidth(1);
		bb.drawYAxis();
		bb.setTickSize(3);
		bb.yMarkup(0, 10, 0);
		bb.setTextAngle(-45);
		bb.labelColumns("Sham", "MCAO");
		bb.setTextAngle(-90);
		bb.rendPtRad(2.0);
		bb.plotColumnScatter(all, SvgColor.getDefaultHSVDistinct(2));
		bb.plotColumnSems(all, 0.5);
		bb.setTextAngle(0);
		bb.plotColumnPValues(all, new int[]{0,1}, false);	
		SVG.writeToFile(bb.getSVG(), new File(Directory.ELISA, "RGMa_Elisa.svg"));
	}
}
