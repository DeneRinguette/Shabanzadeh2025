package shabanzadeh2025.mri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;

import ij.ImagePlus;
import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Filter;
import shabanzadeh2025.util.HyperStack;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Open;
import shabanzadeh2025.util.Save;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Voxel;

/**
 * Coordinating class for MRI analysis.
 * 
 * @author Dene Ringuette
 */

public class Rabbit 
{
	public static final File SRC = Directory.MRI;
	
	private final String group;
	private final File dir;

	private final String trace_tag;
	private final String t10_tag;
	private final String vibe_tag;
	private final int base_stop;
	private final Voxel plasma_voxel;
	private final int signal_start;
	private final int signal_end;
	
	private ImagePlus image;
	private ImagePlus mask;	
	private ImagePlus vibe;

	private String vibe_name;
	
	private ImagePlus resolve;
	
	private ImagePlus vk;
	private ImagePlus conc;
	private Map<Voxel, double[]> tissue_conc;
	private double[] plasma_conc;
	
	public Rabbit(
			String group, 
			String dir, 
			String trace_tag, 
			String t10_tag, 
			String vibe_tag, 
			int base_stop, 
			Voxel plasma_voxel, 
			int signal_start
		)
	{	
		this.group = group;
		this.dir = new File (SRC, dir);
		this.trace_tag = trace_tag;
		this.t10_tag = t10_tag;
		this.vibe_tag = vibe_tag;
		this.base_stop = base_stop;
		this.plasma_voxel = plasma_voxel;
		this.signal_start = signal_start;
		this.signal_end = 144;
	}
	
	public void load()
	{
		File trace_file = new File(this.dir, "RESOLVE_1MM_TRACEW_" + trace_tag + "--scaled.tif");
		this.resolve = Open.image(trace_file);		
		this.image = Open.image(new File(this.dir, "T1_IMAGES_" + t10_tag + ".tif"));
		Filter.lowpass(this.image, 3);
		this.vibe_name = "T1_VIBE_DYNAMIC_" + vibe_tag;
		this.mask = Open.image(new File(this.dir, vibe_name + "--tProjAvg2-"+ base_stop +".tif"));
		if(mask.getNSlices() < mask.getNFrames())
			HyperStack.flipSliceAndFrameDefinition(this.mask);
		this.vibe = Open.image(new File(this.dir, vibe_name + ".tif"));
	}
	
	public void infarct()
	{
		int points = InfractVolume.make(this.resolve);	
		Syo.pl("Infract Area: " + points);
	}
	
	public void conc() throws IOException
	{
		this.conc = FlashSignalEquation.concentrationTimeSeries(this.image, this.mask, this.vibe, this.signal_start, this.signal_end);
		Save.tiff(new File(this.dir, vibe_name + "--conc.tif"), this.conc);
		this.tissue_conc = FlashSignalEquation.toConcentration(this.image, this.mask, this.vibe, this.signal_start, this.signal_end);
		this.plasma_conc = FlashSignalEquation.toConcentration(this.image, this.mask, this.vibe, this.signal_start, this.signal_end, this.plasma_voxel);
	}
	
	public void patlak()
	{
		this.vk = PatlakCompartmentModel.kTrans(this.mask, this.plasma_conc, this.tissue_conc);
		double ratio = PatlakCompartmentModel.vkRatio(this.image, this.mask, this.vk);
		Save.tiff(new File(this.dir, vibe_name + "--vk.tif"), this.vk);
		Syo.pl("kTransRatio = " + ratio);
	}
	
	public void unload()
	{
		this.resolve = null;
		this.image = null;
		this.mask = null;
		this.vibe = null;
		this.conc = null;
		this.tissue_conc = null;
		this.plasma_conc = null;	
		this.vk = null;
	}
	
	public static void make() throws IOException
	{	
		List<Rabbit> rabbits = new ArrayList<Rabbit>();
		rabbits.add(new Rabbit("Vehicle",   "003_280423", "0009", "0022", "0023", 19, new Voxel(  95, 61,  8), 25));
		rabbits.add(new Rabbit("Vehicle",   "073_250123", "0008", "0021", "0022", 19, new Voxel(  97, 84, 14), 28));
		rabbits.add(new Rabbit("Vehicle",   "064_131022", "0013", "0018", "0023", 17, new Voxel(  97, 70,  8), 24));
		rabbits.add(new Rabbit("Vehicle",   "070_231222", "0007", "0020", "0021", 19, new Voxel(  95, 86,  5), 26));
		rabbits.add(new Rabbit("Vehicle",   "077_130423", "0007", "0022", "0023", 19, new Voxel( 160, 67,  5), 25));
		rabbits.add(new Rabbit("Pf-429242", "004_190423", "0000", "0028", "0000", 19, new Voxel(  99, 61,  9), 25));
		rabbits.add(new Rabbit("Pf-429242", "006_210423", "0000", "0020", "0000", 19, new Voxel(  95, 66,  5), 24));
		rabbits.add(new Rabbit("Pf-429242", "017_010623", "0007", "0020", "0021", 19, new Voxel(  96, 64,  4), 26));
		rabbits.add(new Rabbit("Pf-429242", "068_161122", "0009", "0018", "0023", 19, new Voxel(  95, 86, 10), 28));
		rabbits.add(new Rabbit("Pf-429242", "013_170523", "0008", "0021", "0022", 19, new Voxel(  94, 69, 10), 25));
		rabbits.add(new Rabbit("Pf-429242", "010_110523", "0007", "0020", "0021", 19, new Voxel(  93, 58,  5), 30));
		rabbits.add(new Rabbit("Pf-429242", "008_030523", "0007", "0020", "0021", 19, new Voxel(  98, 61,  5), 25));
		
		for(Rabbit rabbit : rabbits)
		{
			Syo.pl(rabbit.group + " " + rabbit.dir.getName());
			rabbit.load();
			rabbit.infarct();
			rabbit.conc();
			rabbit.patlak();
			rabbit.unload();
			Syo.pl();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		switch(1)
		{
			case 1: 
				make(); break;
			case 2: 
				statsLeak(); 
				statsInfarct();
				statsTTC();
				break;
		}	
		
	}
	
	public static void statsLeak() throws IOException
	{
		double[] ctrl = new double[]{
				0.04820724194599646,
				0.1963268710518591,
				0.06587847207814135,
				0.010290787039974214,
				0.18299731119400575, 
			};		
		double[] pf42 = new double[]{	
				0.016035944604638086,
				-0.024737840723439986,
				-0.06569740712812033,
				0.05645369656157473,
				-0.04474265123967358,
				-0.04897021809850144,
				-0.05148770049273488
			};
		
		Ip.mult(ctrl, 100);
		Ip.mult(pf42, 100);
		
		Syo.pl("p = "+ new TTest().homoscedasticTTest(ctrl, pf42));		
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.75, 1.75, -15, 25));
		double[][] data = new double[][] {ctrl, pf42};
		Syo.table("leak", new String[]{"Vehicle", "PF-429242"}, data);
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(0, 10, 0);
		p.plotColumnSems(data, 0.75);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		p.plotColumnPValues(data, new int[] {0,1}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns("Vehicle", "PF-429242");
		SVG.writeToFile(p.getSVG(), new File(SRC, "leakage.svg"));
		Syo.pl();
	}

	public static void statsInfarct() throws IOException
	{
		
		double[] ctrl = new double[] {153, 277, 108, 406, 391};
		double[] pf42 = new double[] {61, 12, 22, 112, 0, 0, 35};	
		Ip.div(ctrl, 100);
		Ip.div(pf42, 100);
		Syo.pl("p = "+ new TTest().homoscedasticTTest(ctrl, pf42));
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.75, 1.75, -0.50, 4.50));
		double[][] data = new double[][] {ctrl, pf42};
		Syo.table("infarct", new String[]{"Vehicle", "PF-429242"}, data);
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(0, 1, 1);
		p.plotColumnSems(data, 0.75);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		p.plotColumnPValues(data, new int[] {0,1}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns("Vehicle", "PF-429242");
		SVG.writeToFile(p.getSVG(), new File(SRC, "infarct.svg"));
		Syo.pl();
	}
	
	public static void statsTTC() throws IOException
	{
		double[] ctrl = new double[] {0.165826572, 0.227628697, 0.242023523, 0.12276775, 0.100314397};		
		double[] pf42 = new double[] {0.108010029, 0.088413715, 0.008260275, 0.016436185, 0.084770664, 0.02328518};
		
		Ip.mult(ctrl, 100);
		Ip.mult(pf42, 100);
		
		Syo.pl("p = "+ new TTest().homoscedasticTTest(ctrl, pf42));
		
		Plot p = new Plot(new Box(50,50,60,80), new Span(-0.75, 1.75, 0, 30));		
		double[][] data = new double[][] {ctrl, pf42};
		Syo.table("TTC", new String[]{"Vehicle", "PF-429242"}, data);
		Text label = new Text(8);
		p.drawYAxis();
		p.yMarkup(0, 10, 0);
		p.plotColumnSems(data, 0.75);
		p.rendPtRad(2.0);
		p.plotColumnScatter(data, new String[] {"red", "blue"});
		p.plotColumnPValues(data, new int[] {0,1}, false);
		p.getGraphRender().text = label.changeTransform(new TextOrientation(-45));
		p.labelColumns("Vehicle", "PF-429242");
		SVG.writeToFile(p.getSVG(), new File(SRC, "ttc.svg"));
		Syo.pl();
	}
}
