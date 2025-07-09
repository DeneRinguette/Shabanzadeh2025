package shabanzadeh2025.mri;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;
import shabanzadeh2025.util.HessFunc;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.NewtonOptimization;
import shabanzadeh2025.util.Optimization;
import shabanzadeh2025.util.Rois;
import shabanzadeh2025.util.Tensor;
import shabanzadeh2025.util.Voxel;

/**
 * Model of Ktrans and Vp based on measured Ct and Cp values.
 * Voxel Vp should loosely mirror network of large vessels.
 * 
 * Ct(t) = V * Cp(t) + K * int_O^t Cp(tau) d(tau), V = V_plasma and K = K_trans
 * 
 * Ct[i] = V * Cp[i] + K * sum_(j=O)^i Cp[j] = V * Cp[i] + K * Sp[i], Note: we merge Delta(tau) into K_trans
 * 
 * E = sum_i (V * Cp[i] + K * Sp[i] - Ct[i])^2 = sum_i (e_i^2)
 * 
 * dE/dV = sum_i (2 * e_i * Cp[i])
 * dE/dK = sum_i (2 * e_i * Sp[i])
 * 
 * dE^2/dV^2 = sum_i (2 * Cp[i]^2)
 * dE^2/dVK = sum_i (2 * Cp[i] * Sp[i])
 * dE^2/dK^2 = sum_i (2 * Sp[i]^2)
 * 
 * @author Dene Ringuette
 */

public class PatlakCompartmentModel
{	
	class VoxelFit extends HessFunc
	{
		final int length;
		final double[] plasma, tissue;
		Tensor grad;
		Tensor hess;
		
		public VoxelFit(double[] plasma, double[] tissue)
		{
			if(plasma.length != tissue.length)
				throw new IllegalArgumentException();
			this.length = plasma.length;
			
			this.plasma = plasma;
			this.tissue = tissue;
		}

		@Override
		public double apply(int depth, Tensor arg) 
		{
			double v = arg.rGet(0);
			double k = arg.rGet(1);
			double[] sumOfPlasma = new double[this.length];
			double[] e = new double[this.length];
			double error = 0;
			
			sumOfPlasma[0] = this.plasma[0];
			for(int i = 1; i < this.length; i++)
				sumOfPlasma[i] = this.plasma[i] + sumOfPlasma[i-1];
			
			for(int i = 0; i < this.length; i++)
				e[i] = v * this.plasma[i] + k * sumOfPlasma[i] - this.tissue[i];
			
			if(0 <= depth)
			{	
				for(int i = 0; i < length; i++)
					error += e[i] * e[i];
			}
			if(1 <= depth)
			{
				double dv = 0;
				double dk = 0;
				for(int i = 0; i < length; i++)
				{
					dv += 2 * e[i] * this.plasma[i];
					dk += 2 * e[i] * sumOfPlasma[i];
				}
				this.grad = Tensor.col(dv, dk);
			}
			if(2 <= depth)
			{
				double dvv = 0;
				double dkv = 0;
				double dkk = 0;
				for(int i = 0; i < length; i++)
				{
					dvv += 2 * this.plasma[i] * this.plasma[i];
					dkv += 2 * this.plasma[i] * sumOfPlasma[i];
					dkk += 2 * sumOfPlasma[i] * sumOfPlasma[i];
				}
				this.hess = Tensor.sqr(2, dvv, dkv, dkv, dkk);
			}
			return error;
		}

		@Override
		public Tensor hess() 
		{
			return this.hess;
		}

		@Override
		public Tensor grad() 
		{
			return this.grad;
		}		
	}
	
	final Map<Voxel, Double> v;
	final Map<Voxel, Double> k;
	
	public PatlakCompartmentModel(double[] plasma, Map<Voxel, double[]> tissue)
	{
		this.v = new TreeMap<Voxel, Double>();
		this.k = new TreeMap<Voxel, Double>();
		
		for(Entry<Voxel, double[]> voxel : tissue.entrySet())
		{
			VoxelFit fit = new VoxelFit(plasma, voxel.getValue());
			Tensor ks = NewtonOptimization.run(Optimization.MINIMIZE, fit, Tensor.col(1, 1), 1e-9);
			this.v.put(voxel.getKey(), ks.rGet(0));
			this.k.put(voxel.getKey(), ks.rGet(1));
		}
	}
	
	public static ImagePlus kTrans(ImagePlus mask, double[] plasma, Map<Voxel, double[]> tissue)
	{
		final int w = mask.getWidth();
		final int h = mask.getHeight();
		final int s = mask.getNSlices();
		
		if(mask.getWidth() != w || mask.getHeight() != h || mask.getNSlices() != s)
			throw new IllegalArgumentException("The mask spacial dimension must match that of the data.");
		
		PatlakCompartmentModel model = new PatlakCompartmentModel(plasma, tissue);
		ImagePlus vk = IJ.createHyperStack("Patlak", w, h, 2, s, 1, 32);
		
		for(Entry<Voxel, Double> v : model.v.entrySet())
		{
			Voxel voxel = v.getKey();
			Double value = v.getValue();
			vk.setPosition(1, voxel.z, 1);
			ImageProcessor pro = vk.getProcessor();
			pro.setf(voxel.x, voxel.y, value.floatValue());
		}		
		for(Entry<Voxel, Double> k : model.k.entrySet())
		{
			Voxel voxel = k.getKey();
			Double value = k.getValue();
			vk.setPosition(2, voxel.z, 1);
			ImageProcessor pro = vk.getProcessor();
			pro.setf(voxel.x, voxel.y, value.floatValue());
		}
		return vk;
	}
	
	public static double vkRatio(ImagePlus image, ImagePlus mask, ImagePlus vk)
	{
		TreeMap<Integer, Line> lines = Rois.byPosition(mask.getOverlay(), Line.class);
		ImageStack stk_t10 = image.getStack();
		MeanData left = new MeanData();
		MeanData right = new MeanData();
		
		for(Entry<Integer, Line> entry : lines.entrySet())
		{
			final int slice = entry.getKey();
			ImageProcessor pro_t10 = stk_t10.getProcessor(slice);
			
			Line line = entry.getValue();
			vk.setPosition(2, slice, 1);
			ImageProcessor pro = vk.getProcessor();
			double nx = line.y1d-line.y2d; 
			double ny = line.x2d-line.x1d;
			double ox = (line.x2d+line.x1d)*.5;
			double oy = (line.y2d+line.y1d)*.5;
			
			TreeMap<Integer, PolygonRoi> rois = Rois.byPosition(mask.getOverlay(), PolygonRoi.class);
			PolygonRoi roi = rois.get(slice);
			for(Point point : roi)
			{
				double side = (point.x-ox)*nx+(point.y-oy)*ny;
				float value = Math.max(0, pro.getf(point.x, point.y));
				float t10 = pro_t10.getf(point.x, point.y);
				if(1 < t10 && t10 < 4095)
				{
					if(side > 0) // right
						right.add(value);
					if(side < 0) // left
						left.add(value);
				}
			}
		}		
		return (right.mean()/left.mean()-1.0);	
	}
}