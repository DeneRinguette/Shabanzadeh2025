package shabanzadeh2025;

import java.io.File;
import java.io.IOException;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Err;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Syo;

/**
 * @author Dene Ringuette
 */

public class PF429242Concentrations 
{
	public static void main(String[] args) throws IOException
	{
		switch(1)
		{
			case 1: plasmaVsBrain(); break;
			case 2: brainLateralization(); break;
		}
	}	
	
	public static void plasmaVsBrain()  throws IOException
	{	
		double[] plasma30min = new double[] {157.7, 477.5, 272.1};
		double[] plasma150min = new double[] {29.2, 62.7, 50};
		double[] brain30min = new double[] {12.4, 13.9, 5.7};
		double[] brain150min = new double[] {12.8, 15.9, 16.2};	
		double[][] plasma = new double[][] {plasma30min, plasma150min};
		double[][] brain = new double[][] {brain30min, brain150min};
		
		Syo.pl(Err.mean(plasma30min));
		Syo.pl(Err.mean(brain30min));
		Syo.pl(Err.mean(plasma150min));
		Syo.pl(Err.mean(brain150min));
		Syo.pl();
		Syo.pl(Err.div(plasma30min, brain30min));
		Syo.pl(Err.div(plasma150min, brain150min));
		
		Plot pp = new Plot(new Box(50,50,80,80), new Span(-0.5, 1.5, 0, 500));		
		pp.drawYAxis();
		pp.yMarkup(0, 100, 0);		
		pp.rendFill("orange");
		pp.plotColumnScatter(plasma);		
		pp.rendFill("purple");
		pp.plotColumnScatter(brain);		
		pp.rendColor("black");
		pp.plotColumnSems(plasma, 0.4);
		pp.plotColumnSems(brain, 0.4);		
		pp.getGraphRender().text = pp.getGraphRender().text.changeTransform(new TextOrientation(-45));		
		pp.labelXCorrdinate(0, "30");
		pp.labelXCorrdinate(1, "150");		
		SVG.writeToFile(pp.getSVG(), new File(Directory.PFCONC, "plasma vs brain.svg"));
	}

	public static void brainLateralization()  throws IOException
	{	
		double[] brainLeft  = new double[] {252, 871, 554};
		double[] brainRight = new double[] {208, 532, 593};	
		Syo.pl(Err.mean(brainLeft));
		Syo.pl(Err.mean(brainRight));
		
		Plot pp = new Plot(new Box(50,50,40,80), new Span(-0.5, 1.5, -50, 50));			
		pp.drawYAxis();
		pp.yMarkup(0, 25, 0);			
		double[] zeros = new double[] {0, 0, 0};			
		double[] ratio = Op.compDiv(brainRight, brainLeft);
		Ip.sub(ratio, 1);
		Ip.mult(ratio, 100);			
		Syo.p("ratio");
		Syo.pl(ratio);
		Syo.pl(Err.mean(ratio));			
		double[][] ratioD = new double[][] {zeros, ratio};
		String[] color = new String[] {"blue", "red"};			
		pp.plotColumnSems(ratioD, 0.4);
		pp.plotColumnScatter(ratioD, color);			
		pp.getGraphRender().text = pp.getGraphRender().text.changeTransform(new TextOrientation(-45));
		SVG.writeToFile(pp.getSVG(), new File(Directory.PFCONC, "lateralization.svg"));
	}
}
