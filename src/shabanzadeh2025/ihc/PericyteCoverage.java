package shabanzadeh2025.ihc;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.stat.inference.TTest;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.ImageFilter;
import shabanzadeh2025.util.IntegerHistogram;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.Open;
import shabanzadeh2025.util.Pixel;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class PericyteCoverage 
{
	public static File SRC = new File(Directory.IHC, "Y66");
	
	public static File Control = new File(SRC, "Vehicle");
	public static File PF429242 = new File(SRC, "PF-429242");
	
	public static void main(String[] args) throws IOException
	{
		double[][] data = new double[2][5];
		
		int group = 0;
		for(File dir : new File[] {Control, PF429242})
		{
			
			int subject = 0;
			for(File tif : dir.listFiles(ImageFilter.TIFF))
			{
				ImagePlus img = Open.image(tif);
				final int w = img.getWidth();
				final int h = img.getHeight();
				Overlay overlay = img.getOverlay();
				Roi[] rois = overlay.toArray();
				img.setPosition(2, 0, 0);
				ImageProcessor pro_grn = img.getProcessor().duplicate();					
				img.setPosition(3, 0, 0);
				ImageProcessor pro_red = img.getProcessor().duplicate();
				
				Set<Pixel> vessels = new HashSet<Pixel>();
				for(Roi roi : rois)
					if(2 == roi.getType())
						for(Point point : roi.getContainedPoints())
							if(0 <= point.x && point.x < 1024 && 0 <= point.y && point.y < 1024)
								vessels.add(new Pixel(point.x, point.y, 0));
				
				IntegerHistogram parenc_grn = IntegerHistogram.uint8();
				IntegerHistogram parenc_red = IntegerHistogram.uint8();
				IntegerHistogram vessel_grn = IntegerHistogram.uint8();
				IntegerHistogram vessel_red = IntegerHistogram.uint8();
				
				MeanData inner_grn = new MeanData();
				MeanData outer_grn = new MeanData();
								
				for(int y = 0; y < h; y++)
					for(int x = 0; x < w; x++)	
					{
						Pixel pixel = new Pixel(x, y, 0);
						if(vessels.contains(pixel))
						{
							vessel_grn.count(pro_grn.getf(x, y));
							vessel_red.count(pro_red.getf(x, y));
							
							inner_grn.add(pro_grn.getf(x, y));
						}
						else
						{
							parenc_grn.count(pro_grn.getf(x, y));
							parenc_red.count(pro_red.getf(x, y));
							
							outer_grn.add(pro_grn.getf(x, y));
						}	
					}				
				double threshold = outer_grn.mean();
				double r = vessel_grn.upperFraction((int)threshold);
				data[group][subject] = r;
				subject++;
			}
			group++;
		}
		Syo.p("ratio = ");
		Syo.pl(Err.div(data[1], data[0]));
		Syo.pl("p = " + new TTest().homoscedasticTTest(data[0], data[1]));
		
		Ip.mult(data, 100);		
		String title = "Pericyte coverage";
		String[] labels = new String[] {"Vehicle", "PF-429242"}; 		
		Syo.table(title, labels, data);
		
		Plot p = new Plot(new Box(50,50,60,80), Span.bar(2, 0, 100));
		p.rendWidth(1);
		p.drawYAxis();
		p.setTickSize(3);
		p.rendText(8);
		p.yMarkup(0, 20, 0);
		p.setTextAngle(-45);
		p.labelColumns("Vehicle", "Pf429242");
		p.rendPtRad(2.0);
		p.setTextAngle(0);
		p.plotColumnPValues(data, new int[]{1,0}, false);
		p.plotColumnSems(data, 0.75);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		SVG.writeToFile(p.getSVG(), new File(SRC, "graph.svg"));		
	}
}