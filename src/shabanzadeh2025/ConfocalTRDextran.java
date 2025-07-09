package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.TTest;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Arrayz;
import shabanzadeh2025.util.Explicit;
import shabanzadeh2025.util.ImageFilter;
import shabanzadeh2025.util.Open;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class ConfocalTRDextran 
{
	public static void main(String[] args) throws IOException
	{
		final File CONFOCAL = Directory.CTRD;
		final File PROJECTED = new File(Directory.CTRD, "zproj");
		
		Explicit rgma = new Explicit();  // group 1
		Explicit h151a = new Explicit(); // group 2
		Explicit pbs = new Explicit();   // group 3
		
		int index = 0;
		
		for(File tif : PROJECTED.listFiles(ImageFilter.TIFF))
		{
			VarData red_data = new VarData();
			VarData grn_data = new VarData();
			VarData blu_data = new VarData();
			
			ImagePlus image = Open.image(tif);
			
			image.setPosition(2, 1, 1);
			ImageProcessor red = image.getProcessor().duplicate();
			image.setPosition(1, 1, 1);
			ImageProcessor grn = image.getProcessor().duplicate();
			image.setPosition(3, 1, 1);
			ImageProcessor blu = image.getProcessor().duplicate();
			
			red_data.add(red);
			grn_data.add(grn);
			blu_data.add(blu);
			
			double value = red_data.mean();
					
			if(index < 4)
				rgma.add(value);
			else if(index < 8)
				h151a.add(value);
			else
				pbs.add(value);
			
			index++;
		}
		
		String[] labels = new String[]{"PBS", "RGMa", "H151A"};
		double[][] data = new double[][]{pbs.values(), rgma.values(), h151a.values()}; 
		
		Syo.table("Mean intensity [a.u.]", labels, data);
		
		TTest test = new TTest();
		Syo.pl(test.homoscedasticTTest(rgma.values(), h151a.values()));
		Syo.pl(test.homoscedasticTTest(h151a.values(), pbs.values()));
		Syo.pl(test.homoscedasticTTest(pbs.values(), rgma.values()));
		
		Plot p = new Plot(new Box(100,100, 80, 80), Span.bar(3, 0, 320));
		
		p.setTickSize(3);
		p.rendPtRad(2);
		p.drawYAxis();
		p.yMarkup(0, 80, 0);
		
		p.plotColumnSems(data, 0.75);
		String[] c = SvgColor.getDefaultHSVDistinct(5);
		p.plotColumnScatter(data, Arrayz.subsample(c, 1, 0, 4));
		p.plotColumnPValues(data, new int[]{0,1,1,2}, false);
		
		p.setTextAngle(-45); 
		p.labelColumns(labels);
		SVG.writeToFile(p.getSVG(), new File(CONFOCAL, "graph.svg"));
	}
}
