package shabanzadeh2025.mri;

import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;
import shabanzadeh2025.util.HyperStack;
import shabanzadeh2025.util.Rois;
import shabanzadeh2025.util.Voxel;

/**
 * Concentration conversions based on T1 extracted from FLASH signal equations.
 * 
 * @author Dene Ringuette
 */

public class FlashSignalEquation
{

	public static final double ALPHA = 20.0 * Math.PI / 180; // 20 degrees to radians
	
	public static final double COS_ALPHA = Math.cos(ALPHA);
	public static final double SIN_ALPHA = Math.sin(ALPHA);
	public static final double TR = 4.63; // repetition time (ms)
	public static final double TE = 1.87; // time to echo (ms)
	public static final double R1 = 5.3; // 1/(s*mM)=1/(ms*M), gadovist-pm-en_0.pdf
	
	public final double t1_static;
	public final double signal_base;
	public final double[] signal;
		
	public FlashSignalEquation(ImagePlus image, ImagePlus mask, ImagePlus vibe, Voxel voxel, int t0, int t1)
	{
		this.t1_static = HyperStack.voxel(image, 1, 1, voxel);
		this.signal_base = HyperStack.voxel(mask, 1, 1, voxel);
		this.signal = HyperStack.voxelTimeSeries(vibe, 1, t0, t1, voxel);	
	}
	
	public double[] toConcentration()
	{
		return toConcentration(this.t1_static, this.signal_base, this.signal);
	}
		
	private static double[] toConcentration(
			final double t1_static,
			final double signal_base,
			final double[] signal
		)
	{	
		final int length = signal.length;
		
		final double e1_static = Math.exp(-TR/t1_static);
		final double s0 = signal_base/SIN_ALPHA/((1-e1_static)/(1-e1_static*COS_ALPHA));
		
		boolean regularize = false;
		final double[] t1_dynamic = new double[length];
		final double factorA = s0/SIN_ALPHA;
		final double factorB = COS_ALPHA/factorA;
		final double b = (1-e1_static)/(1-e1_static*COS_ALPHA); 
		for(int i = 0; i < length; i++)
		{
			final double a = (signal[i]-signal_base)/factorA;
			final double e1_dynamic = 
					regularize ? 
							(1-(a+b))/(1-(a+b)*COS_ALPHA) : 
							(1-(signal[i])/factorA)/(1-(signal[i])*factorB);
			t1_dynamic[i] = (-TR/Math.log(e1_dynamic))/1000; // sec
		}
		
		final double[] concentration = new double[length];
		for(int i = 0; i < length; i++)
			concentration[i] = (1/t1_dynamic[i]-1/t1_static)/R1;

		return concentration;
	}
	
	public static ImagePlus concentrationTimeSeries(ImagePlus image, ImagePlus mask, ImagePlus vibe, int t0, int t1)
	{
		final int width = vibe.getWidth();
		final int height = vibe.getHeight();
		final int channel = 1;
		final int slices = vibe.getNSlices();
		final int frames = t1-t0+1;
		final int bits = 32;
		
		ImagePlus imp = IJ.createHyperStack("Concentration", width, height, channel, slices, frames, bits);
		TreeMap<Integer, PolygonRoi> rois = Rois.byPosition(mask.getOverlay(), PolygonRoi.class);
		for(Entry<Integer, PolygonRoi> entry : rois.entrySet())
			for(Point point : entry.getValue())
			{
				Voxel voxel = new Voxel(point.x, point.y, entry.getKey());
				FlashSignalEquation flash = new FlashSignalEquation(image, mask, vibe, voxel, t0, t1);
				double[] concentration = flash.toConcentration();
				for(int frame = 1; frame <= frames; frame++)
				{
					imp.setPosition(channel, voxel.z, frame);
					ImageProcessor pro = imp.getProcessor();
					pro.setf(voxel.x, voxel.y, (float)concentration[frame-1]);
				}
			}
		
		return imp;
	}
	
	public static Map<Voxel, double[]> toConcentration(ImagePlus image, ImagePlus mask, ImagePlus vibe, int t0, int t1)
	{
		Map<Voxel, double[]> tissue = new TreeMap<Voxel, double[]>();
		TreeMap<Integer, PolygonRoi> rois = Rois.byPosition(mask.getOverlay(), PolygonRoi.class);
		for(Entry<Integer, PolygonRoi> entry : rois.entrySet())
			for(Point point : entry.getValue())
			{
				Voxel tissue_voxel = new Voxel(point.x, point.y, entry.getKey());
				FlashSignalEquation tissue_flash = new FlashSignalEquation(image, mask, vibe, tissue_voxel, t0, t1);
				tissue.put(tissue_voxel, tissue_flash.toConcentration());
			}
		return tissue;
	}
	
	public static double[] toConcentration(ImagePlus image, ImagePlus mask, ImagePlus vibe, int t0, int t1, Voxel voxel)
	{
		return new FlashSignalEquation(image, mask, vibe, voxel, t0, t1).toConcentration();
	}
}
