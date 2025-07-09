package shabanzadeh2025.sc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntPredicate;

import shabanzadeh2025.Directory;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Syo;

/**
 * Analysis and graphing of scRNA-seq EC data.
 * 
 * @author Dene Ringuette
 */

public class EndoScRnaSeq 
{
	public static final File DATA = Directory.SCRNASEQ;
	
	public static final File DIR_FOR_VIOLIN = new File(DATA, "violin_plots");
		
	static String[] reverse_rise = new String[] {"Bsg", "Cxcl12", "Igf1r"};
	
	static String[] reverse_fall = new String[] {"Cd300lg", "Cfh", "Cdh13"};
	
	static String[][] tsnes_1b = new String[][]{
			{"Pecam1"}, 
			{"Cdh5"}, 
			{"Pdgfrb"},
		};
		
	static String[][] tsnes_1c = new String[][]{
			{"Pecam1", "Cdh5", "Ntn4", "Fbln2", "Cdh13"},
			{"Pecam1", "Cdh5", "Malat1", "Ifitm2", "Rpl41", "Rps29", "Cd300lg", "Plscr2"},
			{"Pecam1", "Cdh5", "Malat1", "Ifitm2", "Rpl41", "Rps29", "Cfh"},
		};
		
	static String[][] tsnes_1d = new String[][]{	
			{"Pecam1", "Cdh5", "Vwf", "Vcam1", "Fbln5", "Cytl1"}, 
			{"Pecam1", "Cdh5", "Mfsd2a", "Rgcc"},
			{"Pecam1", "Cdh5", "Vwf", "Vcam1", "Lcn2"},
		};
	
	static String[][] tsnes_1e = new String[][]{
			{"Pecam1", "Cdh5", "Cxcl12"},
			{"Pecam1", "Cdh5", "Bsg"}, 
			{"Pecam1", "Cdh5", "Igf1r"},
		};
		
	static String[][] tsnes_1f = new String[][]{
			{"Pecam1", "Cdh5", "Bsg", "Cxcl12", "Igf1r", "Ccdc141", "Fnbp1l", "Id1", "Jcad", "Sema3c", "Slc39a10", "Slco1a4", "Slco1c1", "Spock2"},
			{"Pecam1", "Cdh5", "Mfsd2a", "Rgcc"},
			{"Pecam1", "Cdh5", "Tfrc", "Car4"},
		};
		
	static String[][] tsnes_1g = new String[][]{
			{"Pecam1", "Cdh5", "Glul", "Tgfb2"},
			{"Pecam1", "Cdh5", "Mfsd2a", "Rgcc"},
			{"Pecam1", "Cdh5", "Tfrc", "Car4"},
		};	
	
	static String[] volcano = new String[]{
			"Ntn4", "Fbln2", "Cdh13", "Cfh",
			"Bsg", "Cxcl12", "Igf1r", "Id1", "Jcad", "Spock2", 
			"Cldn5", "Il1r1",
			"Slc39a10", "Slco1a4", "Slco1c1",
			"Car4",
		};
		
	static String[] circle = new String[]{
			"Pecam1", "Cdh5",
			"Lcn2", "Mt1", "Rps29", "Ifitm2", "Rpl41", "Il1r1", "Cd300lg", "Malat1", "Ntn4", "Cfh", "Cdh13", "Plscr2", "Car8", "Fbln2", "Bmx", "Xbp1", "Cd109", 
			"Esm1", "Bsg", "Slco1a4", "Spock2", "Cxcl12", "Slco1c1", "Car4", "Slc39a10", "Slc22a8", "Mfsd2a", "Jcad", "Igf1r", "Ccdc141", "Sema3c", "Id1", "Fnbp1l",
		};
	
	static String[] violin = new String[]{
			"Pecam1", "Cdh5", 
			"Bsg", "Cxcl12", "Igf1r", 
			"Cd300lg", "Cfh" , "Cdh13", 
			"Mfsd2a", "Lcn2", 
			"Mt1", "Esm1", 
			"Car4", "Car8",
		};
			
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		ScRnaSeq experiment = new ScRnaSeq(DATA, ScRnaSeq.Device.CHROMIUM,
				"R-Sham",     "Monnier_Sam__Sham_Ipsi_MouseBrainECs_3pr_V3_1",
				"L-Vehicle",  "Monnier_Sam__MCAO_Contra_MouseBrainECs_3pr_V3_1",
				"R-Vehicle",  "Monnier_Sam__MCAO_Ipsi_MouseBrainECs_3pr_V3_1",
				"L-PF429242", "Monnier_Sam__MCAO_Left_Hemisphere_PF_MouseBrainECs_3pr_V3_1",
				"R-PF429242", "Monnier_Sam__MCAO_Right_Hemisphere_PF_MouseBrainECs_3pr_V3_1"
			);
		
		experiment.load_clusters(6);
			
		Map<String, IntPredicate> inclusion_signature = new TreeMap<String, IntPredicate>();
		
		for(String sample_summary : experiment.summary())
			System.out.println(sample_summary);
		System.out.println();
		
		IntPredicate any = (int x) -> x > 0;
		
		int cell_type = 2;
		switch(cell_type)
		{
			case 1 : System.out.println("All cell analysis."); 
				break;
			case 2: System.out.println("EC cells only analysis.");
				inclusion_signature.put("Pecam1", any);
				inclusion_signature.put("Cdh5", any);				
				break;
			case 3: System.out.println("Pericyte only analysis.");
				inclusion_signature.put("Pdgfrb", any); 
				break;
		}
					
		experiment.reductToSignature(inclusion_signature);
		
		for(String sample_summary : experiment.summary())
			Syo.pl(sample_summary);
		Syo.pl();
				 
		switch(1)
		{
			case 1: // should be EC only analysis
				switch(1)
				{
					case 1: experiment.anova(); break;
					case 2: experiment.enrichment(3); break;
				}
				break;
			case 2: // should be EC only analysis
					experiment.printCorrelations(reverse_rise);
					experiment.printCorrelations(reverse_fall); break;
			case 3: // requires all cell analysis
				switch(1)
				{
					case 1: experiment.lowDimensionalRGB(true, tsnes_1b); break;
					case 2: experiment.lowDimensionalRGB(true, tsnes_1c); break;
					case 3: experiment.lowDimensionalRGB(true, tsnes_1d); break;
					case 4: experiment.lowDimensionalRGB(true, tsnes_1e); break;
					case 5: experiment.lowDimensionalRGB(true, tsnes_1f); break;
					case 6: experiment.lowDimensionalRGB(true, tsnes_1g); break;
				} 
				break;
			case 4: // requires all cell analysis
				switch(4)
				{
					case 1: 
						experiment.summary(Op.append(tsnes_1b[0], tsnes_1b[1])); break;
					case 2: 
						experiment.summary(tsnes_1c[0]);
						experiment.summary(tsnes_1c[1]);
						experiment.summary(tsnes_1c[2]); break;
					case 3: 
						experiment.summary(tsnes_1f[0]); break;
					case 4:	
						experiment.summary(Op.append(tsnes_1f)); break;
				}	
			case 5: // should be EC only analysis
					experiment.volanos(volcano, new int[] {1,3}, 1.0, 4, 20, 120, true); 
					experiment.volanos(volcano, new int[] {2,3}, 1.0, 4, 20, 60, true);
					experiment.volanos(volcano, new int[] {5,3}, 1.0, 6, 20, 300, true);
				break;
			case 6: // should be EC only analysis
					experiment.circlePlot(1, circle); 
					experiment.circlePlot(2, circle);
				break;
			case 7: // should be EC only analysis
					experiment.violinPlotsGeneAdjecent(violin, 0); break;
		}
	}
}