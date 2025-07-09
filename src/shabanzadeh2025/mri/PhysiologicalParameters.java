package shabanzadeh2025.mri;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.Directory;
import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.VarData;

/**
 * Graphing recorded physiological parameters.
 * 
 * @author Dene Ringuette
 */

public class PhysiologicalParameters 
{
	private static File SRC = new File(Directory.MRI, "Physiology Monitoring");
	
	private static String[] TIMES = new String[] {"-45", "-30", "-15", "0", "+15", "+30"};
	
	public static void pressure(String name, double[][] data) throws IOException
	{
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, 100));
		double[] time = new double[] {0,1,2,3,4,5};
		pp.setTickSize(3);
		pp.yMarkup(0, 50, 0);
		pp.drawYAxis();
		pp.labelColumns(TIMES);
		double[][] ctrl = Op.transpose(Op.subset(Op.transpose(data), new int[] {0,1,2}));			
		double[][] test = Op.transpose(Op.subset(Op.transpose(data), new int[] {3,4,5,6,7}));
		VarData[] ctrl2 = VarData.rows(ctrl);
		VarData[] test2 = VarData.rows(test);
		pp.plotSignificance(time, false, 0, ctrl2, test2);
		Syo.table(name, null, data);
		pp.rendColor("red");
		pp.plotError(time, ctrl2);
		pp.rendColor("blue");
		pp.plotError(time, test2);
		SVG.writeToFile(pp.getSVG(), new File(SRC, name + ".svg"));
		Syo.pl();
	}
	
	public static void rate(String name, double[][] data) throws IOException
	{
		boolean fast = name.equals("HR");
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 0, fast ? 300 : 45));
		double[] time = new double[] {0,1,2,3,4,5};
		pp.setTickSize(3);
		pp.yMarkup(0, fast ? 100 : 15, 0);
		pp.drawYAxis();
		pp.labelColumns(TIMES);
		double[][] ctrl = Op.transpose(Op.subset(Op.transpose(data), fast ? new int[] {0,1,2,3,4} : new int[] {0,1,2,3}));			
		double[][] test = Op.transpose(Op.subset(Op.transpose(data), fast ? new int[] {5,6,7,8,9} : new int[] {4,5,6,7,8}));
		VarData[] ctrl2 = VarData.rows(ctrl);
		VarData[] test2 = VarData.rows(test);
		pp.plotSignificance(time, false, 0, ctrl2, test2);
		Syo.table(name, null, data);
		pp.rendColor("red");
		pp.plotError(time, ctrl2);
		pp.rendColor("blue");
		pp.plotError(time, test2);	
		SVG.writeToFile(pp.getSVG(), new File(SRC, name + ".svg"));
		Syo.pl();
	}
	
	public static void oxy(String name, double[][] data) throws IOException
	{
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 88, 100));
		double[] time = new double[] {0,1,2,3,4,5};
		pp.setTickSize(3);
		pp.yMarkup(96, 4, 0);
		pp.drawYAxis();
		pp.labelColumns(TIMES);
		double[][] ctrl = Op.transpose(Op.subset(Op.transpose(data), new int[] {0,1,2,3,4}));			
		double[][] test = Op.transpose(Op.subset(Op.transpose(data), new int[] {5,6,7,8,9,10}));
		VarData[] ctrl2 = VarData.rows(ctrl);
		VarData[] test2 = VarData.rows(test);
		pp.plotSignificance(time, false, 0, ctrl2, test2);
		Syo.table(name, null, data);
		pp.rendColor("red");
		pp.plotError(time, ctrl2);
		pp.rendColor("blue");
		pp.plotError(time, test2);	
		SVG.writeToFile(pp.getSVG(), new File(SRC, name + ".svg"));
		Syo.pl();
	}
	
	public static void temp(String name, double[][] data) throws IOException
	{		
		Plot pp = new Plot(new Box(50,50,120,80), new Span(-0.5, 5.5, 28, 40));
		double[] time = new double[] {0,1,2,3,4,5};
		pp.setTickSize(3);
		pp.yMarkup(32, 4, 0);
		pp.drawYAxis();
		pp.labelColumns(TIMES);
		double[][] ctrl = Op.transpose(Op.subset(Op.transpose(data), new int[] {0,1,2,3,4}));			
		double[][] test = Op.transpose(Op.subset(Op.transpose(data), new int[] {5,6,7,8,9}));
		VarData[] ctrl2 = VarData.rows(ctrl);
		VarData[] test2 = VarData.rows(test);
		pp.plotSignificance(time, false, 0, ctrl2, test2);
		Syo.table(name, null, data);
		pp.rendColor("red");
		pp.plotError(time, ctrl2);
		pp.rendColor("blue");
		pp.plotError(time, test2);
		SVG.writeToFile(pp.getSVG(), new File(SRC, name + ".svg"));
		Syo.pl();
	}
	
	public static void main(String[] args) throws IOException
	{
		final double[][] sbp = Parser.getDoubles(new File(SRC, "SBP.txt"), 0, new int[]{1,2,3,4,5,6});
		final double[][] dbp = Parser.getDoubles(new File(SRC, "DBP.txt"), 0, new int[]{1,2,3,4,5,6});
		final double [][] map = Op.div(Op.sum(sbp, Op.mult(2, dbp)), 3);
		pressure("SBP", sbp);
		pressure("DBP", dbp);
		pressure("MAP", map);
		final double[][] hr = Parser.getDoubles(new File(SRC, "HR.txt"), 0, new int[]{1,2,3,4,5,6});
		final double[][] rr = Parser.getDoubles(new File(SRC, "RR.txt"), 0, new int[]{1,2,3,4,5,6});		
		rate("HR", hr);
		rate("RR", rr);		
		final double[][] o2 = Parser.getDoubles(new File(SRC, "O2.txt"), 0, new int[]{1,2,3,4,5,6});		
		oxy("O2", o2);		
		final double[][] temp = Parser.getDoubles(new File(SRC, "Temp.txt"), 0, new int[]{1,2,3,4,5,6});		
		temp("Temp", temp);		
	}
}