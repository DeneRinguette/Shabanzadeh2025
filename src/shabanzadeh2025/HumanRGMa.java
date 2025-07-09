package shabanzadeh2025;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Explicit;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Xlsx;

/**
 * @author Dene Ringuette
 */

public class HumanRGMa 
{
	public static void rgmaConcentrations() throws IOException
	{
		File dir = Directory.HUMAN;
		Xlsx xlsx = new Xlsx(new File(dir, "RGMA ELISA data.xlsx"));
		xlsx.setSheet(0);
		
		double[] aisp = xlsx.getNumericColumn(0, 1);
		double[] ctrl = xlsx.getNumericColumn(1, 1);
		double[][] data = new double[][] {ctrl, aisp};
		Ip.div(data, 1000);
		
		Syo.pl("MannWhitneyUTest: p = " + new MannWhitneyUTest().mannWhitneyUTest(aisp, ctrl));	
		Syo.table("Patient serum RGMa (ng/mL)", new String[] {"Healthy", "AIS"}, new double[][] {ctrl, aisp});
		
		Plot p = new Plot(new Box(50, 50, 2*60, 2*80), Span.bar(2, 0, 15));
		p.drawYAxis();
		p.yMarkup(0, 5, 0);
		p.rendPtRad(2);
		p.plotColumnScatter(data, SvgColor.getDistinct(2, 0f, .7f, .8f));
		p.plotColumnStars(data, new int[] {0,1}, false);
		p.setTextAngle(-45);
		p.labelColumns(new String[]{"Healthy", "AIS"});		
		p.rendColor("green");
		p.plotColumnQuartiles(data, .5);
		SVG.writeToFile(p.getSVG(), new File(dir, "RGMA ELISA data.svg"));
	}
	
	public static void categoricalStatistics()
	{
		long[][] ratios = new long[][] {
			{31, 12}, 
			{41, 7}, 
			{18, 9}, 
			{24, 2}, 
			{13, 6}, 
			{35, 5}, 
			{15, 0}, 
			{6, 0}
		};
		
		for(long[] ratio : ratios)
		{		
			long[] total = new long[] {83, 22};
			total[0] -= ratio[0];
			total[1] -= ratio[1];
			Syo.pl(ratio);
			Syo.pl(total);
			Syo.pl("p = " + new ChiSquareTest().chiSquareTest(new long[][] {ratio, total}));
			Syo.pl();	
		}
	}
		
	public static void continuousStatistics() throws IOException
	{
		File dir = Directory.HUMAN;
		Xlsx xlsx = new Xlsx(new File(dir, "Participant Characteristics Data.xlsx"));
		xlsx.setSheet(0);
		
		List<String> headers = xlsx.getHeaders(); 
		List<List<Double>> columns = xlsx.getColumns(1);		
		List<Explicit> hcp = new ArrayList<Explicit>(14);
		List<Explicit> ais = new ArrayList<Explicit>(14); 
		
		for(int i = 0; i < 14; i++)
		{
			Explicit hcp_ = new Explicit();
			hcp.add(hcp_);
			Explicit ais_ = new Explicit();
			ais.add(ais_);

			List<Double> column = columns.get(i);			
			for(int j = 0; j < 105; j++)
			{
				Double v = column.get(j);
				if(j < 83)
					ais_.add(v);
				else
					hcp_.add(v);
			}
		}
		
		Syo.pl("Parametric:");
		Syo.pl();
		TTest tTest = new TTest();
		for(int i : new int[] {2, 11, 12})
		{
			double[] a = ais.get(i).values();
			double[] b = hcp.get(i).values();
			Syo.pl(headers.get(i));
			Syo.pl("p = " + tTest.homoscedasticTTest(a, b));
			Syo.pl();
		}	
		
		Syo.pl("Non-parametric:");
		Syo.pl();
		MannWhitneyUTest uTest = new MannWhitneyUTest();
		for(int i : new int[] {3,13})
		{
			double[] a = ais.get(i).values();
			double[] b = hcp.get(i).values();
			Syo.pl(headers.get(i));
			Syo.pl("p = " + uTest.mannWhitneyUTest(a, b));
			Syo.pl();
		}	
	}
	
	public static void main(String[] args) throws IOException
	{
		switch(3)
		{
			case 1: rgmaConcentrations(); break;
			case 2:	categoricalStatistics(); break; 
			case 3: continuousStatistics(); break;
		}	
	}
}