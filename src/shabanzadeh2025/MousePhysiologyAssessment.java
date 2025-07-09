package shabanzadeh2025;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.inference.OneWayAnova;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Numbers;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Range;
import shabanzadeh2025.util.RepeatedMeasuresFactorialANOVA;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Xlsx;

/**
 * @author Dene Ringuette
 */

public class MousePhysiologyAssessment 
{
	public static void main(String[] args0) throws FileNotFoundException, IOException
	{
		File src = Directory.PHYSIO;
		File xlsx = new File(src, "Physio.xlsx");
		Xlsx data = new Xlsx(xlsx);
		
		switch(1)
		{
			case 1:
			{
				data.setSheet("BC");
				
				List<List<Double>> all = data.getColumns(2);
				List<String> groups = data.getHeaders(0);
				List<String> headers = data.getHeaders(1);
				
				String[] colors = SvgColor.getDefaultHSVDistinct(6);
				
				for(int i = 0; i < groups.size(); i++)
				{
					String group = groups.get(i);
					List<String> header = headers.subList(i*6, (i+1)*6);
					List<List<Double>> d = all.subList(i*6, (i+1)*6);
					Syo.pl(group);
					Syo.pl(header);
					
					String[] h = new String[header.size()];
					double[][] a = new double[6][];
					List<double[]> b = new ArrayList<double[]>(6);
					for(int j = 0; j < 6; j++)
					{
						h[j] = headers.get(j);
						a[j] = Numbers.toDouble(d.get(j));
						b.add(Numbers.toDouble(d.get(j)));
					}
					
					OneWayAnova anova = new OneWayAnova();
					Syo.pl("p = " + anova.anovaPValue(b));			
					Syo.table(group, h, a);
					
					Range range = new Range();
					range.add(a);
					double[] tix = range.marksForPlot();
					Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, tix[tix.length-1]));
					pp.setAxisPrecision(0);
					pp.drawYAxis();
					pp.labelYCorrdinates(tix);
					pp.yTics(tix);
					pp.rendPtRad(1.5);
					pp.plotColumnScatter(a, colors);
					pp.plotColumnSems(a, 0.75);
					pp.setTextAngle(-45);
					pp.labelColumns(header);
					pp.setTextAngle(-90);
					pp.labelVertical(group);
					SVG.writeToFile(pp.getSVG(), new File(src, "BC-"+ i+ ".svg"));
					Syo.pl();
				}
			} 
			break;
		
			case 2:
			{
				String[] params = new String[] {"HR", "Diastolic", "Systolic", "MAP", "SpO2"};
				
				for(String param : params)
				{
					data.setSheet(param);
					
					List<String> groups = data.getHeaders(0);
					List<String> headers = data.getHeaders(1);
					List<List<Double>> all = data.getColumns(2);
				
					double[][][] across = new double[groups.size()][][];
					
					Syo.pl(param);
					Syo.pl(groups);
					
					String[] colors = SvgColor.getDefaultHSVDistinct(3);
					
					for(int i = 0; i < groups.size(); i++)
					{
						String group = groups.get(i);
						List<String> header = headers.subList(i*3, (i+1)*3);
						List<List<Double>> d = all.subList(i*3, (i+1)*3);
						
						double[][] a = new double[3][];
						for(int j = 0; j < 3; j++)
							a[j] = Numbers.toDouble(d.get(j));
						
						across[i] = Op.transpose(a);
						
						Range range = new Range();
						range.add(a);
						double[] tix = range.marksForPlot();
						Plot pp = new Plot(new Box(50,50,60,80), new Span(-0.5, 2.5, 0, tix[tix.length-1]));
						pp.setAxisPrecision(0);
						pp.drawYAxis();
						pp.labelYCorrdinates(tix);
						pp.yTics(tix);
						pp.rendPtRad(1.5);
						pp.plotColumnScatter(a, colors);
						pp.plotColumnSems(a, 0.75);
						pp.labelTop(group);
						pp.setTextAngle(-45);
						pp.labelColumns(header);
						pp.setTextAngle(-90);
						pp.labelVertical(param);
						SVG.writeToFile(pp.getSVG(), new File(src, param + "-"+ i+ ".svg"));
					}
					RepeatedMeasuresFactorialANOVA.p_value(across, true);
					Syo.pl();	
				}			
			} 
			break;
		}
	}
}
