package shabanzadeh2025.util;

import java.util.Map;
import java.util.Map.Entry;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;

/**
 * @author Dene Ringuette
 */

public class HyperStack 
{
	public static ImagePlus hyperstack(ImagePlus imp, int c, int z, int t, String order, String mode)
	{
		return HyperStackConverter.toHyperStack(imp, c, z, t, order, mode);	
	}
	
	public static int hyper(int i0, int i1, int i2, int n0, int n1)
	{
		return i0 + n0 * ((i1-1) + n1 * (i2-1));
	}
	
	public static int[] stack(int i, int n0, int n1)
	{
		int j = i - 1;
		return new int[]{j % n0 + 1, j / n0 % n1 + 1, j / (n0 * n1) + 1};
	}
	
	public static ImagePlus copyChannel(ImagePlus data, int channel)
	{	
		return new Duplicator().run(data, channel, channel, 1, data.getNSlices(), 1, data.getNFrames());	
	}
	
	public static ImagePlus copySlice(ImagePlus data, int slice)
	{	
		return new Duplicator().run(data, 1, data.getNChannels(), slice, slice, 1, data.getNFrames());	
	}
	
	public static ImagePlus copyFrame(ImagePlus data, int frame)
	{	
		return new Duplicator().run(data, 1, data.getNChannels(), 1, data.getNSlices(), frame, frame);	
	}
	
	public static ImagePlus copyChannelAndFrame(ImagePlus data, int channel, int frame)
	{	
		return new Duplicator().run(data, channel, channel, 1, data.getNSlices(), frame, frame);	
	}
	
	public static ImagePlus copyChannelAndSlice(ImagePlus data, int channel, int slice)
	{	
		return new Duplicator().run(data,channel, channel, slice, slice, 1, data.getNFrames());	
	}
	
	public static ImagePlus copySliceAndFrame(ImagePlus data, int slice, int frame)
	{	
		return new Duplicator().run(data, 1, data.getNChannels(), slice, slice, frame, frame);	
	}
	
	public static double[] voxelTimeSeries(ImagePlus plus, int channel, int t0, int t1, Voxel vox)
	{
		final int points = plus.getNFrames();
		if(t0 < 1)
			throw new IllegalArgumentException("Start frame must be one or higher.");
		if(points < t1)
			throw new IllegalArgumentException("Stop frame must be within number of frames.");
		if(t1 < t0)
			throw new IllegalArgumentException("Start frame must precede stop frame.");
		
		final double[] series = new double[t1-t0+1];
		
		for(int time = t0; time <= t1; time++)
		{
			plus.setPosition(channel, vox.z, time);
			ImageProcessor pro = plus.getProcessor();
			series[time-t0] = pro.getf(vox.x, vox.y);
		}
		return series;
	}

	public static double voxel(ImagePlus plus, int channel, int time, Voxel vox)
	{
		final int points = plus.getNFrames();
		if(time < 1)
			throw new IllegalArgumentException("Frame must be one or higher.");
		if(points < time)
			throw new IllegalArgumentException("Frame must be within number of frames.");
		
		plus.setPosition(channel, vox.z, time);
		return plus.getProcessor().getf(vox.x, vox.y);
	}
	
	public static void putAllVoxelTimeSeries(ImagePlus plus, int channel, int t0, int t1,  Map<Voxel, double[]> map)
	{
		for(Entry<Voxel, double[]> entry : map.entrySet())
			entry.setValue(voxelTimeSeries(plus, channel, t0, t1, entry.getKey()));
	}
	
	public static ImagePlus sliceProject(ImagePlus plus, String method)
	{
		return ZProjector.run(plus, method + " all");
	}
	
	public static ImagePlus frameProject(ImagePlus that, String method)
	{
		ImagePlus thus = flipSlicesAndFrames(that);
		thus = sliceProject(thus, method);
		flipSliceAndFrameDefinition(thus);
		return thus;
	}
	
	public static void flipSliceAndFrameDefinition(ImagePlus plus)
	{
		plus.setStack(plus.getStack(), plus.getNChannels(), plus.getNFrames(), plus.getNSlices());	
	}
	
	public static ImagePlus copyStructure(ImagePlus plus)
	{
		final int width = plus.getWidth();
		final int height = plus.getHeight();
		final int channels = plus.getNChannels();
		final int slices = plus.getNSlices();
		final int frames = plus.getNFrames();
		final int bits = plus.getBitDepth();
		
		return IJ.createHyperStack(plus.getTitle(), width, height, channels, slices, frames, bits);
	}
	
	public static ImagePlus flipSlicesAndFrames(ImagePlus that)
	{
		final int noChannels = that.getNChannels();
		final int noSlices = that.getNSlices();
		final int noFrames = that.getNFrames();
		
		ImagePlus thus = IJ.createHyperStack(
				that.getTitle(), 
				that.getWidth(), that.getHeight(), 
				noChannels, /*slices*/noFrames, /*frames*/noSlices, 
				that.getBitDepth());
		
		for(int channel = 1; channel <= noChannels; channel++)
			for(int slice = 1; slice <= noSlices; slice++)
				for(int frame = 1; frame <= noFrames; frame++)
				{
					that.setPosition(channel, slice, frame);
					thus.setPosition(channel, frame, slice);
					thus.setProcessor(that.getProcessor().duplicate());
					
				}
					
		return thus;		
	}	
}
