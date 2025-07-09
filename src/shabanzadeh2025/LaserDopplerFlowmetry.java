package shabanzadeh2025;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Arrayz;
import shabanzadeh2025.util.Extension;
import shabanzadeh2025.util.Gen;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.LowPass;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.Meth;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Piper;
import shabanzadeh2025.util.Range;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * @author Dene Ringuette
 */

public class LaserDopplerFlowmetry
{
	public static class Recording
	{
		public int last = -1;
		public List<double[]> ch1, ch2;
		
		public Recording(File file) throws IOException
		{
			@SuppressWarnings("resource")
			BufferedReader reader = 
					new BufferedReader(
							new FileReader(file)
						);
			
			this.ch1 = new ArrayList<double[]>();
			this.ch2 = new ArrayList<double[]>();
			
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.equals("RAW DATA"))
				{
					List<Double> pu1 = new ArrayList<Double>();
					List<Double> pu2 = new ArrayList<Double>();
					
					line = reader.readLine();
					
					String[] header = line.split("\t");
					if(!header[3].equals("1 PU"))
						throw new IllegalArgumentException("Wrong 1-PU header");
					
					if(header.length >= 5)
						if(!header[4].equals("2 PU"))
							throw new IllegalArgumentException("Wrong 2-PU header");
					
						
					boolean done = false;
					while((line = reader.readLine()) != null && !done)
					{
						final String[] data = line.split("\t");
						if(data[0].matches("[0-9]+"))
						{
							pu1.add(Double.parseDouble(data[3]));
							if(header.length >= 5)
								pu2.add(Double.parseDouble(data[4]));
						}
						else
							done = true;
					}
					
					final double[] x1 = Arrayz.toDoubleArray(pu1); 
					final double[] x2 = Arrayz.toDoubleArray(pu2); 
					
					if(x1.length > 10000)
					{
						this.ch1.add(x1);
						if(x2.length > 10000)
							this.ch2.add(x2);
						this.last += 1;
					}
				}
			}
			reader.close();
		}
	}
		
	public static void main(String[] args) throws IOException
	{
		File GROUPS = Directory.LDF;
		
		Range ex = new Range();
		
		final int samplesPerHour = 116129;
		
		final double samplingPeriod = 0.031000; // in seconds
		
		final double[] unified_time = Gen.range(-1.0/3, samplingPeriod/3600*10, 1.0+1.0/12);
		
		Syo.pl(unified_time.length);
		
		for(File treatment : GROUPS.listFiles())
			for(File group : treatment.listFiles())
			{
				
				final VarData[] unified_data = new VarData[unified_time.length];
				for(int k = 0 ; k < unified_time.length; k++)
					unified_data[k] = new VarData();
				
				for(File data : group.listFiles())
					if(data.getName().endsWith(".txt"))
					{
						Syo.pl(data.getAbsolutePath());
						LaserDopplerFlowmetry.Recording r = new LaserDopplerFlowmetry.Recording(data);
						
						double[] trace1 = r.ch1.get(r.last);
						
						double[] time = Gen.fromBy(0.0, samplingPeriod/3600, trace1.length-1);
						
						double[] binned = Op.rollingMean(trace1, samplesPerHour);
						double left = (Stat.argMin(binned) * samplingPeriod)/3600;
						double right = left + 1.0;
						ex.add(left);
						ex.add(right);
						Ip.sub(time, left);
						
						double start = time[0];
						double end = time[time.length-1];
						int i = 0;
						while(time[i] < 0.0)
						{
							time[i] = (time[i])/(0.0-start)/3;
							i++;
						}
						
						i = time.length-1;
						while(time[i] > 1.0)
						{
							time[i] = (time[i]-1.0)/(end-1.0)/12+1.0;
							i--;
						}
						
						double[] values = trace1;
						values = LowPass.rcZeroPhase(samplingPeriod, Meth.rcfc(0.0167), values);
						
						i = 0;
						MeanData baseline = new MeanData();
						while(time[i] < -17.0/60)
						{
							baseline.add(values[i]);
							i++;
						}
						Ip.div(values, baseline.mean());
						
						double[] unified_values = Op.downsample(time, values, unified_time);
						for(int k = 0; k < unified_values.length; k++)
							unified_data[k].add(unified_values[k]);
						
						int factor = 10;
						values = Op.decimate(values, factor);
						time = Op.decimate(time, factor);
						
						Plot pp = new Plot(new Box(50, 50, 640, 480), new Span(-1.0/3, 1.0 + 1.0/12, 0, 1.2));
						pp.plotVerticalLine(0.0);
						pp.plotVerticalLine(1.0);
						pp.plotVerticalLine(-17.0/60);
						pp.rendColor("red");
						pp.plotCurve(time, values);
						pp.rendColor("blue");
						pp.plotCurve(unified_time, unified_values);
						SVG.writeToFile(pp.getSVG(), Extension.change(data, "svg"));
					}
				
				Plot plot = new Plot(new Box(50, 50, 150, 100), new Span(-1.0/3, 1.0 + 1.0/12, 0, 1.5));
				plot.rendColor("green");
				plot.plotVerticalLine(0.0);
				plot.plotVerticalLine(1.0);
				plot.rendFill(192, 192, 192);
				plot.rendColor("none");
				plot.plotRect(-20.0/60, 0.0, -17.0/60, 1.5);
				plot.rendFill("none");
				plot.rendColor("black");
				plot.drawYAxis();
				plot.drawXAxis();
				plot.setTickSize(3);
				plot.rendText(8);
				plot.yMarkup(0, 0.5, 1);
				plot.xMarkup(0, 1.0, 1);
				
				double[] unified_mean = new double[unified_time.length];
				for(int k = 0 ; k < unified_time.length; k++)
					unified_mean[k] = unified_data[k].mean();
				double[] unified_sem = new double[unified_time.length];
				for(int k = 0 ; k < unified_time.length; k++)
					unified_sem[k] = unified_data[k].sem();
				
				plot.setRound();
				plot.rendColor("blue");
				plot.plotCurve(unified_time, unified_mean);
				plot.rendColor("red");				
				plot.plotCurve(unified_time,	Op.sum(unified_mean, unified_sem));
				plot.plotCurve(unified_time, Op.diff(unified_mean, unified_sem));
				Piper.table(
						new File(group, "source file output.tab"), 
						group.getName(), 
						new String[]{"time (h)", "mean", "s.e.m."}, 
						new double[][] {unified_time, unified_mean, unified_sem}
					);
				SVG.writeToFile(plot.getSVG(), new File(group, "summary of group.svg"));
			}
		Syo.pl(ex);
	}
}
