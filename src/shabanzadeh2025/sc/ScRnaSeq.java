package shabanzadeh2025.sc;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.inference.TTest;

import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.utils.TSneUtils;

import shabanzadeh2025.rend.*;
import shabanzadeh2025.util.Addable;
import shabanzadeh2025.util.ArgMax;
import shabanzadeh2025.util.Arrayz;
import shabanzadeh2025.util.CSV;
import shabanzadeh2025.util.Circle;
import shabanzadeh2025.util.CovData;
import shabanzadeh2025.util.DoubleHistogram;
import shabanzadeh2025.util.Explicit;
import shabanzadeh2025.util.Indexed;
import shabanzadeh2025.util.Ip;
import shabanzadeh2025.util.Label;
import shabanzadeh2025.util.LowPass;
import shabanzadeh2025.util.Maps;
import shabanzadeh2025.util.MeanData;
import shabanzadeh2025.util.Meth;
import shabanzadeh2025.util.Op;
import shabanzadeh2025.util.Parser;
import shabanzadeh2025.util.Piper;
import shabanzadeh2025.util.Range;
import shabanzadeh2025.util.Stat;
import shabanzadeh2025.util.Syo;
import shabanzadeh2025.util.Updatable;
import shabanzadeh2025.util.VarData;

/**
 * Utility class for scRNA-seq analysis.
 * 
 * @author Dene Ringuette
 */

public class ScRnaSeq
{
	
	public final File src;
	
	public final Map<Integer, Sample> samples;
	
	public final File gene_lists;
	
	public final File violin_plots;
	
	public final Map<String, Range> gene_ranges;
	
	public final Device device;
	
	public enum Device
	{
		CHROMIUM("");
		
		private final String tag;
		
		private Device(String arg)
		{
			this.tag = arg;
		}
		
		public String getTag()
		{
			return this.tag;
		}
	}
	
	public class Sample implements Serializable
	{
		private static final long serialVersionUID = 3937486044305229864L;
		
		private final String gz;
		
		final ScRnaSeq experiment;
		final String name;
		final int nameHashCode;
		final File dir;
		final File outs_folder;
		final File filtered_folder;
		final File sctransform_folder;
		final File analysis_folder;
		final File cluster_folder;
		final File corrdinate_folder;
		
		final Map<Integer, String> gene_names;
		final Map<String, Integer> gene_numbers;
		
		final Map<Integer, Cell> cells;
		final Map<String, Integer> cell_numbers;
		
		//final Map<String, Point2D.Double> coordinates;
		
		boolean tsne, annotated;
			
		int k = -1;
		File kmeans_folder;
		Map<String, Integer> clusters;
		Map<Integer, Set<String>> cluster_barcode_sets;
		Map<Integer, Set<Integer>> cluster_cell_number_sets;
		File diffexp_folder;
		Map<String, Map<Integer, DiffExp>> diffexp;
		
		final File pca_folder;
		final Map<String, double[]> projections;
		String[] colors;
		
		Map<String, Range> range;
		Map<String, MeanData> mean;
		Map<String, DoubleHistogram> histogram;
		
		public Circle circle_of_interest;
		
		public int cluster_of_interest;
		
		public Sample(ScRnaSeq experiment, String name, File directory) throws IOException
		{
			final String tag = experiment.device.tag;
			
			this.experiment = experiment;
			this.name = name;
			this.nameHashCode = name.hashCode();
			this.dir = directory;
			this.outs_folder = new File(this.dir, "outs");
			this.analysis_folder = new File(this.outs_folder, "analysis");
			this.filtered_folder = new File(this.outs_folder, "filtered_feature_bc_matrix");
			
			this.gz = new File(this.filtered_folder, "features.tsv").exists() ? "" : ".gz";
			
			this.gene_names = this.gene_names(new File(this.filtered_folder, "features.tsv"+gz));
			this.gene_numbers = this.gene_numbers(new File(this.filtered_folder, "features.tsv"+gz));
			
			this.cell_numbers = this.cell_numbers(new File(this.filtered_folder, "barcodes.tsv"+gz));
			this.cells = this.cells();
			
			this.tsne = true;
			
			this.cluster_folder = new File(analysis_folder, tsne ? "tsne" : "umap");
			this.corrdinate_folder = new File(cluster_folder, tag+"2_components");
			this.assign_coordinates(new File(corrdinate_folder, "projection.csv"));
			this.pca_folder = new File(analysis_folder, "pca\\"+tag+"10_components");
			this.projections = null; 
					ScRnaSeq.projections(new File(pca_folder, "projection.csv"));
			
			this.sctransform_folder = new File(this.filtered_folder, "sctransform");
			
		}
		
		/**
		 * Cluster to differential expression map. 
		 * 
		 * @param gene
		 * @return
		 */
		
		public Map<Integer, DiffExp> diffExp(String gene)
		{
			return this.diffexp.get(gene);
		}
		
		public DiffExp diffExp(String gene, int cluster)
		{
			Map<Integer, DiffExp> map = this.diffExp(gene);
			if(map == null)
				return null;
			return map.get(cluster);
		}
		
		public String[] genes()
		{
			String[] genes = new String[this.gene_names.size()];
			for(Entry<Integer, String> entry : this.gene_names.entrySet())
				genes[entry.getKey()-1] = entry.getValue();
			return genes;
		}
		
		public class DiffExp
		{
			/**
			 * Sample to which differential expression corresponds.  
			 * For back referencing.
			 */
			
			private final Sample sample;
			
			private final int cluster;
			private final double mean;
			private final double fold_change; 
			private final double p_adjusted; 
			
			public DiffExp(Sample sample, int cluster, double mean, double fold, double p)
			{
				this.sample = sample;
				this.cluster = cluster;
				this.mean = mean;
				this.fold_change = fold;
				this.p_adjusted = p;		
			}

			public double mean() 
			{
				return this.mean;
			}

			public double foldChange() 
			{
				return this.fold_change;
			}

			public double pAdjusted() 
			{
				return this.p_adjusted;
			}
			
			public double negLogLikelihood()
			{
				return -Math.log10(this.pAdjusted());
			}

			public int cluster() 
			{
				return this.cluster;
			}

			public Sample sample() 
			{
				return this.sample;
			}
						
			public double pi()
			{
				return this.foldChange() * this.negLogLikelihood();
			}
		}
		
		public class Cell
		{
			/**
			 * Sample to which cell corresponds.  
			 * For back referencing.
			 */
			
			private final Sample sample;
			
			/**
			 * Nucleotide barcode used for cell identification in Next Gen Sequencing.  
			 */
			
			private final String barcode;
			
			private final int barcodeHashCode;
			
			/**
			 * Index number for this cell from sample. 
			 */
			
			private final Integer cell_number;
			
			/**
			 *  updatable expression data (gene index -> transcript count)
			 */
			
			private final Map<Integer, Integer> expression;
			
			/**
			 * Coordinate from reduced dimensional embedding 
			 */
			
			public Point2D.Double embedded_corrdinate;
			
			/**
			 * Position relative to histology.
			 */
						
			/**
			 * Sole constructor with identifiers.
			 * 
			 * @param sample -- back reference to sample
			 * @param barcode -- nucleotide identifier
			 * @param cell_number -- cell index number
			 */
			
			public Cell(Sample sample, String barcode, int cell_number)
			{
				this.sample = sample;
				this.barcode = barcode;
				this.barcodeHashCode = barcode.hashCode();
				
				if(cell_number < 1)
					throw new IllegalArgumentException("Cell numbers must be positive and index starting from 1.");
				this.cell_number = cell_number;
				
				this.expression = new TreeMap<Integer, Integer>();
			}
			
			public Sample sample()
			{
				return this.sample;
			}
			
			/**
			 * 
			 * @return
			 */
			
			public String barcode()
			{
				return this.barcode;
			}
			
			/**
			 * 
			 * @return
			 */
			
			public Integer cellNumber()
			{
				return this.cell_number;
			}
			
			/**
			 * 
			 * @return
			 */
			
			public Map<Integer, Integer> expression()
			{
				return this.expression;
			}
			
			public boolean hasGene(Integer gene_number)
			{
				return this.expression.containsKey(gene_number);
			}
			
			public boolean hasGene(String gene)
			{
				return this.hasGene(this.sample.geneNumber(gene));
			}
						
			/**
			 * 
			 * @param gene_number
			 * @param transcripts
			 */
			
			public void setCount(final Integer gene_number, final Integer transcripts)
			{
				if(gene_number < 1)
					throw new IllegalArgumentException("Gene numbers must be positive and index starting from 1.");
			
				if(transcripts < 1)
					throw new IllegalArgumentException("Transcript counts must be positive integers.");
				
				this.expression.put(gene_number, transcripts);
			}
						
			/**
			 * 
			 * @param gene
			 * @param transcripts
			 */
			
			public void setCount(final String gene, final Integer transcripts)
			{
				this.setCount(this.sample.geneNumber(gene), transcripts);	
			}
			
			/**
			 * 
			 * @param gene_number
			 * @return
			 */
			
			public Integer getCount(final Integer gene_number)
			{
				return this.expression.get(gene_number);
			}
			
			public Integer getCount(final String gene)
			{
				return this.getCount(this.sample.geneNumber(gene));
			}			
			
			/**
			 * 
			 * @return
			 */
			
			public long totalTranscripts()
			{
				long total = 0;
				for(Integer count : this.expression.values())
					total += count;
				return total;
			}
			
			@Override
			public int hashCode() 
			{
				final int PRIME = 31;
				int result = 1;
				result = PRIME * result + this.sample.hashCode();
				result = PRIME * result + this.barcodeHashCode;
				return result;
			}
		}
		
		public Integer geneNumber(String gene)
		{
			return this.gene_numbers.get(gene);
		}
		
		public Integer cellNumber(String barcode)
		{
			return this.cell_numbers.get(barcode);
		}
		
		public int cellCount()
		{
			return this.cells.size();
		}
		
		public int geneCount()
		{
			return this.gene_numbers.size();
		}
		
		
		public Cell cell(Integer cell_number)
		{
			return this.cells.get(cell_number);
		}
		
		public Cell cell(String cell_barcode)
		{
			return this.cell(this.cellNumber(cell_barcode));
		}
		
		public Set<Integer> geneNumbers(Set<String> genes)
		{
			final Set<Integer> numbers = new TreeSet<Integer>();
			for(String gene : genes)
				numbers.add(this.geneNumber(gene));
			return numbers;
		}
		
		public Set<Integer> cellNumbers(Set<String> barcodes)
		{
			final Set<Integer> numbers = new TreeSet<Integer>();
			for(String barcode : barcodes)
				numbers.add(this.cellNumber(barcode));
			return numbers;
		}
		
		public String geneName(Integer gene)
		{
			return this.gene_names.get(gene);
		}		
		
		public int hashCode()
		{
			return this.nameHashCode;
		}
		
		public boolean equals(Object obj)
		{
			if(obj instanceof Sample)
			{
				Sample that = (Sample)obj;
				return this.name.equals(that.name);
			}
			else
				return false;
		}
		
		private Map<String, Integer> cell_numbers(File file) throws IOException
		{
			List<String> list = Parser.toList(file);
			Map<String, Integer> barcodes = new TreeMap<String, Integer>();
			int i = 1;
			for(String barcode : list)
				barcodes.put(barcode, i++);
			return barcodes;
		}
		
		
		private Map<String, Integer> gene_numbers(File features) throws IOException
		{
			List<String> data = Parser.toList(features);
			Map<String, Integer> names = new TreeMap<String, Integer>();
			int i = 1;
			for(String line : data)
			{
				String[] parts = line.split("\t");
				names.put(parts[1], i++);
			}
			return names;
		}
		
		private Map<Integer, String> gene_names(File features) throws IOException
		{
			List<String> data = Parser.toList(features);
			Map<Integer, String> names = new TreeMap<Integer, String>();
			int i = 1;
			for(String line : data)
			{
				String[] parts = line.split("\t");
				names.put(i++, parts[1]);
			}
			return names;
		}
		
		private void assign_coordinates(File tsne_data) throws FileNotFoundException, IOException
		{
			Map<Integer, List<String>> tnse = CSV.columns(tsne_data, new int[]{0, 1, 2}, new TreeMap<Integer, List<String>>());
			
			List<String> id = tnse.get(0);
			List<String> xs = tnse.get(1);
			List<String> ys = tnse.get(2);
			
			final int n = id.size();
			if(xs.size() != n || ys.size() != n)
				throw new IllegalArgumentException("Columns not equal size.");
			
			for(int row = 1; row < n; row++)
			{
				final String cell_barcode = id.get(row);
				
				final int cell_number = 
						cell_barcode.matches("[ATCG].*") ? 
								this.cellNumber(cell_barcode): 
								Integer.parseInt(cell_barcode);
				
				final double x = Double.parseDouble(xs.get(row));
				final double y = Double.parseDouble(ys.get(row));
				this.cell(cell_number).embedded_corrdinate = new Point2D.Double(x, y);
			}
		}
		
		private Map<Integer, Cell> cells() throws IOException
		{
			final Map<Integer, Cell> cells = new TreeMap<Integer, Cell>();
			
			List<String> barcodes = Parser.toList(new File(this.filtered_folder, "barcodes.tsv"+this.gz));
			int cellNumber = 1;
			for(String barcode : barcodes)
				cells.put(cellNumber, new Cell(this, barcode, cellNumber++));
			
			File counts = new File(this.filtered_folder, "matrix.mtx"+this.gz);
			if(counts.exists())
			{
				List<String> data = Parser.toList(counts);
				
				final int n = data.size();
				for(int i = 3; i < n; i++)
				{
					String[] entry = data.get(i).split(" ");
					
					Cell cell = cells.get(Integer.parseInt(entry[1]));
					if(cell == null)
						throw new IllegalArgumentException("Matrix cell number found without corresponding barcode.");
					
					cell.setCount(Integer.parseInt(entry[0]), Integer.parseInt(entry[2]));
				}
			}
			return cells;
		}
		
		public Map<Integer, Cell> copyCells()
		{
			Map<Integer, Cell> copy = new TreeMap<Integer, Cell>();
			copy.putAll(this.cells);
			return copy;
		}
		
		public String name()
		{
			return this.name;
		}
		
		public void compute_range()
		{
			this.range = this.geneRange();
		}
		
		public void compute_mean()
		{
			this.mean = this.geneMean();
		}
		
		public void compute_histogram()
		{
			this.histogram = this.geneHistogram();
		}
		
		public void load_clusters(int k) throws FileNotFoundException, IOException
		{
			this.k = k;
			
			final String tag = this.experiment.device.tag; 
			
			this.kmeans_folder = new File(this.analysis_folder, "clustering\\"+tag+"kmeans_"+k+"_clusters");
			this.clusters = ScRnaSeq.clusters(new File(this.kmeans_folder, "clusters.csv"));
			this.cluster_barcode_sets = Maps.groupFromValue(clusters);
	
			this.diffexp_folder = new File(this.analysis_folder, "diffexp\\"+tag+"kmeans_" + k + "_clusters");
			this.diffexp = this.diffexp(new File(this.diffexp_folder, "differential_expression.csv"));
			
			this.colors = SvgColor.getDistinct(k, 0.0f, 0.6f, 0.7f);
		}
		
		public File dir()
		{
			return this.dir;
		}
		
		public void reGraphTsne(double perplexity, int iterations) throws IOException
		{
			String name = this.dir().getName(); 
			Syo.pl(name);
			
			Map<Integer, Point2D.Double> tsne = cellid_coordinates(
					new File(this.dir, "tsne--perplexity=" + perplexity + "--iterations=" + iterations + ".csv")
				);
					
			Range rangeX = new Range();
			Range rangeY = new Range();
			for(Point2D.Double xy : tsne.values())
			{
				rangeX.add(xy.x);
				rangeY.add(xy.y);
			}
						
			final int gap = 50;
			final double buffer = 0.05;
			
			Plot pp = new Plot(
					new Box(gap, gap, 1920-2*gap, 1920-2*gap), 
					new Span(rangeX.min(buffer), rangeX.max(buffer), rangeY.min(buffer), rangeY.max(buffer))
				);
			
			pp.drawBox();
			pp.rendPtRad(2);
			pp.rendText(12);
			
			for(Entry<Integer, Point2D.Double> entry : tsne.entrySet())
			{
				pp.plotPoint(entry.getValue());
			}
			
			SVG.writeToFile(pp.getSVG(), 
					new File(this.dir, "replot--perplexity=" + perplexity + "--iterations=" + iterations + ".svg")
				);
		}
		
		public void reComputeTsne(double perplexity, int iterations, String[] names_of_discarded) throws IOException
		{
			Syo.pl(this.dir().getName());
			
			int no_genes = this.gene_names.size();
					this.geneCount();
			Syo.pl(no_genes);
			int no_cells = this.cellCount();

			final int total_cells = no_cells;
			
			double[][] expression_matrix = ScRnaSeq.toMatrix(no_cells, no_genes, this.cells);
			
			Set<Integer> discard_cells = new TreeSet<Integer>();
			Set<Integer> discard_genes = new TreeSet<Integer>();
			{

				for(String name_to_discard : names_of_discarded)
					discard_genes.add(this.geneNumber(name_to_discard));
				
				for(Entry<Integer, Cell> entry : cells.entrySet())
				{
					TreeSet<Integer> inter = new TreeSet<Integer>();
					inter.addAll(entry.getValue().expression().keySet());
					inter.retainAll(discard_genes);
					
					if(inter.size() > 0)
						discard_cells.add(entry.getKey());
				}	
				cells.keySet().removeAll(discard_cells);
				no_cells -= discard_cells.size();
			}
			
			// Remove from matrix
			int[] array_discard = new int[discard_cells.size()];
			{
				int k = 0;
				for(Integer remove : discard_cells)
					array_discard[k++] = remove-1;
				expression_matrix = Op.remove(expression_matrix, array_discard);
			}
			
			final int[] originalIndex = ScRnaSeq.originalIndex(total_cells, array_discard);
			
			BHTSne t = new BHTSne();
			
			TSneConfiguration c = TSneUtils.buildConfig(expression_matrix, 2, no_genes, perplexity, iterations); 
			 
			double[][] tsne_xys = t.tsne(c);
			
			File tsne = new File(this.dir(), "tsne--perplexity=" + perplexity + "--iterations=" + iterations + ".csv");
			
			PrintWriter out = Piper.printer(tsne);
			out.println("Cell-ID,TSNE-1,TSNE-2");
			for(int i = 0; i < tsne_xys.length; i++)
			{
				final double[] xy = tsne_xys[i];
				out.println((originalIndex[i]+1) + ","+ xy[0] + ","+ xy[1]);
			}
			out.close();
		}
		
		public void geneCorrelation() throws FileNotFoundException, IOException
		{
			Syo.pl(this.dir().getName());
			
			Map<String, CovData> correlation = new TreeMap<String, CovData>();
			for(String gene_name : gene_names.values())
				correlation.put(gene_name, new CovData());
			
			String target = //TODO
					//"Cxcl14";
					//"Cd14";
					//"Ly6g"; 
					//"Cd101";
					//"Cxcr2"; 
					"Itgam";
			
			for(Cell cell : this.cells.values())
			{
				Integer target_value = cell.getCount(target);
				if(target_value != null)
				{
					for(Map.Entry<Integer, Integer> e : cell.expression().entrySet())
						correlation.get(gene_names.get(e.getKey())).add(Meth.log2(target_value), Meth.log2(e.getValue()));
				}
			}
			
			for(Map.Entry<String, CovData> e : correlation.entrySet())
			{
				CovData data = e.getValue();
				final double z = Math.abs(data.zFisher());
				final int s = data.size();
				if(z > 35 && s > 50)
					Syo.pl(e.getKey() +" z=" + z+ " r=" + data.r() + " n=" + s);
			}	
		}
		
		public void annotateMouse(Map<String, String[]> signatures) throws IOException
		{
			Syo.pl(this.name);
			PrintWriter out = new PrintWriter(new FileWriter(new File(this.analysis_folder, "cell-types--post.tab")), true);
			for(Cell cell : this.cells.values())
			{
				String[] ordered_genes = ScRnaSeq.cell_gene_order(cell.expression(), this.gene_names);
				
				String max_type = null;
				double max_similarity = -1.0;
				for(Map.Entry<String, String[]> e : signatures.entrySet())
				{
					double similarity = ScRnaSeq.compareOrder(ordered_genes, e.getValue());
					if(max_similarity < similarity)
					{
						max_similarity = similarity;
						max_type = e.getKey();
					}
				}
				
				String out_line = cell.barcode() +"\t"+ max_similarity + "\t"+ max_type;
				Syo.pl(out_line);
				out.println(out_line);
			}
			out.close();
		}
				
		public void summary(String[] target) throws FileNotFoundException, IOException
		{
			Syo.p(this.name());
			VarData ensemble = new VarData();
			for(Map.Entry<Integer, Cell> point : this.cells.entrySet())
			{
				Cell cell = point.getValue();
				
				if(cell != null)
				{
					final int[] expression = new int[target.length];
					for(int i = 0; i < target.length; i++)
					{	
						Integer e = cell.getCount(target[i]);
						if(e != null)
							expression[i] = e;
					}
					boolean match = true;
					for(int e : expression)
						if(e < 1)
							match = false;
					if(match)
						ensemble.add(Meth.log2(expression[0]));
				}		
			}
			int denom = this.cells.size();
			Syo.pl(", " + ensemble.weight() +"/"+ denom + "=" + ensemble.weight() / denom + ", " + ensemble.mean() + "(log2)");
		}
		
		public void updateRange(String[][] target, Range[] range)
		{
			final int n = target.length;
			
			if(n != range.length)
				throw new IllegalArgumentException("Input arrays must be of same length.");
			
			//for(String barcode : coordinates.keySet())
			for(Cell cell : this.cells.values())
			{
				final double[] expression = new double[3];
				for(int i = 0; i < n; i++)
					expression[i] = 1;
				
				for(int i = 0; i < n; i++)
				{
					for(String gene : target[i])
					{
						Integer value = cell.getCount(gene);
						expression[i] *= value == null ? 0 : Meth.log2(1.0+value.intValue());
					}
					expression[i] = Math.pow(expression[i], 1.0/target[i].length);
				}
				
				for(int i = 0; i < n; i++)
					range[i].add(expression[i]);
			}
		}
		
		public void lowDimensionalRGB(String[][] target, Range[] range) throws FileNotFoundException, IOException
		{
			Syo.p(this.dir().getName());
						
			Plot pp = ScRnaSeq.plotFormat(this.tsne);
			
			pp.rendWidth(1.0);
			pp.rendFill("black");
			pp.rendColor("none");
			pp.drawBox();
			pp.rendFill("none");
			pp.rendColor("black");
			pp.noStroke();
			pp.rendPtRad(.5);
			pp.rendText(6);
			
			if(range == null)
			{
				range = new Range[3];
				for(int i = 0; i < 3; i++)
					range[i] = new Range();
				
				this.updateRange(target, range);
			}
			
			List<Label<Double>> list = new ArrayList<Label<Double>>();
			for(Cell cell : this.cells.values())
			{	
				final double[] expression = new double[3];
				for(int i = 0; i < 3; i++)
					expression[i] = 1;
				for(int i = 0; i < 3; i++)
				{
					for(String gene : target[i])
					{
						Integer value = cell.getCount(gene);
						expression[i] *= value == null ? 0 : Meth.log2(1.0+value.intValue());
					}
					expression[i] = Math.pow(expression[i], 1.0/target[i].length);
				}
			
				Range r = new Range();
				for(int i = 0; i < 3; i++)
					r.add(range[i].normed(expression[i]));
				final double value = r.max();
				
				list.add(new Label<Double>(value, cell.barcode()));
			}
			
			Collections.sort(list);
			
			PrintWriter writer = Piper.printer(new File(this.dir(), this.name + "--rgb.tab"));
			writer.println("Barcode\tRed\tGreen\tBlue");
			for(Label<Double> point : list)
			{
				Cell cell = this.cell(point.label);
				
				final double[] expression = new double[3];
				for(int i = 0; i < 3; i++)
					expression[i] = 1;
				for(int i = 0; i < 3; i++)
				{
					for(String gene : target[i])
					{
						Integer value = cell.getCount(gene);
						expression[i] *= value == null ? 0 : Meth.log2(1.0+value.intValue());
					}
					expression[i] = Math.pow(expression[i], 1.0/target[i].length);
				}
			
				pp.rendFillAlpha(0.4 + 0.6 * point.value);
				
				double red_r = range[0].normed(expression[0]);
				double grn_r = range[1].normed(expression[1]);
				double blu_r = range[2].normed(expression[2]);
				
				double a = 0.2;
				double b = 0.8;
				
				double red_ = a + b * red_r;
				double grn_ = a + b * grn_r;
				double blu_ = a + b * blu_r;
				
				long red = Math.round(Math.min(255.0, 255.0*(blu_/4 + red_)));
				long grn = Math.round(Math.min(255.0, 255.0*(blu_/4 + grn_)));
				long blu = Math.round(255.0*blu_);
				
				writer.println(cell.barcode() + "\t" + red_r + "\t" + grn_r + "\t" + blu_r);
				
				pp.rendFill((int)red, (int)grn, (int)blu);
				
				if(red != 0 || grn != 0 || blu != 0)
					pp.plotPoint(cell.embedded_corrdinate);
			}	
			writer.close();
			SVG.writeToFile(pp.getSVG(), new File(this.dir(), this.name + "--rgb.svg"));
		}
		
		public <T extends Updatable> Map<String, T> sampleExpression(Supplier<T> factory, DoubleUnaryOperator func)
		{
			return this.sampleExpression(this.cells, factory, func);
		}
		
		public Map<String, Integer> zeroExpressionCellCount()
		{
			final Map<String, MeanData> sum_express = this.sampleExpression(() -> new MeanData(), (double x) -> x);
			
			final int total_cells =  this.cellCount();
			
			final Map<String, Integer> zeros = new TreeMap<String, Integer>();
			
			for(String gene : this.gene_numbers.keySet())
			{
				MeanData data = sum_express.get(gene);
				zeros.put(gene, total_cells - (data == null ? 0 : data.size()));
			}
			
			return zeros;
		}
		
		public <T extends Updatable> Map<String, T> sampleExpression(Map<Integer, Cell> expression_mapping, Supplier<T> factory, DoubleUnaryOperator func)
		{
				
			final Map<String, T> sum_express = new TreeMap<String, T>();
				
			for(Cell cell : expression_mapping.values())
			{
				for(Entry<Integer, Integer> expression : cell.expression().entrySet())
				{
					final String gene_name = this.geneName(expression.getKey());
					T rolling_mean = sum_express.get(gene_name);
					if(rolling_mean == null)
					{
						rolling_mean = factory.get();
						sum_express.put(gene_name, rolling_mean);
					}
					rolling_mean.add(func.applyAsDouble(expression.getValue()));
				}
			}
			return sum_express;
		}

		public <T extends Updatable> Map<String, T> subSampleExpression(Set<Integer> cells, Supplier<T> factory, DoubleUnaryOperator func)
		{
			return this.subSampleExpression(cells, this.cells, factory, func);
		}
		
		public <T extends Updatable> Map<String, T> subSampleExpression(Set<Integer> cells, Map<Integer, Cell> expression_mapping, Supplier<T> factory, DoubleUnaryOperator func)
		{	
			final Map<String, T> sum_express = new TreeMap<String, T>();
				
			for(Integer cell : cells)
			{
				for(Entry<Integer, Integer> expression : expression_mapping.get(cell).expression().entrySet())
				{
					final String gene_name = this.geneName(expression.getKey());
					T rolling_mean = sum_express.get(gene_name);
					if(rolling_mean == null)
					{
						rolling_mean = factory.get();
						sum_express.put(gene_name, rolling_mean);
					}
					rolling_mean.add(func.applyAsDouble(expression.getValue()));
				}
			}
			return sum_express;
		}
		
		public void examine() throws FileNotFoundException, IOException
		{
			File cells_file = new File(this.dir(), "PooledFocus2.txt");
			final int[] cell_array = Parser.getIntegers(cells_file);
			Set<Integer> cells = new TreeSet<Integer>();
			for(int cell_id : cell_array)
				cells.add(cell_id);
			
			Map<Integer, Cell> express_in = this.cells();
			express_in.keySet().retainAll(cells);
			Map<String, VarData> gene_var_inside = 
					this.sampleExpression(express_in, () -> new VarData(), (double x) -> Math.log10(1.0 + x));	
			
			Map<Integer, Cell> express_out = this.cells();
			express_out.keySet().removeAll(cells);
			Map<String, VarData> gene_var_outside = 
					this.sampleExpression(express_out, () -> new VarData(), (double x) -> Math.log10(1.0 + x));
			
			Syo.pl(" " + express_in.size());
			for(Entry<String, VarData> gene : gene_var_inside.entrySet())
			{
				double gene_mean_inside = gene.getValue().mean();
				VarData gene2 = gene_var_outside.get(gene.getKey());
				double gene_mean_outside = gene2 == null ? 0: gene2.mean();
				//if(0.6 < gene_mean && gene_mean <= 0.8)
					//So.pl(gene.getKey() + " " + gene.getValue().mean() + " " + gene.getValue().size());
				double diff = gene_mean_inside - gene_mean_outside;
				
				if(-20.0  < diff && diff <= -0.5 && gene.getValue().size() > 20)
					//So.pl(gene.getKey() + " " + diff);
					Syo.pl(gene.getKey());
				
				// H1KO         Cryaa=2    Cryba2=1
				// MastCellDef  Adamts1=1  Ccn1=1   Actb=1222 Lyz2=1116
				// WT           Ptgds=17   Ttr=19
			}
		}
		
		public void differentialExpression() throws FileNotFoundException, IOException
		{
			Syo.pl(this.dir().getName());
	
			//So.pl(" " + Stat.argMax(diffexp.get("Kit")) + " "+ Stat.argMax(diffexp.get("Fcer1g")));
			// confirms MastCellDeficiency
			
			Map<String, Integer> group = new TreeMap<String, Integer>();
			Map<Integer, Map<String, Double>> sets = new TreeMap<Integer, Map<String, Double>>();
			for(int i = 1; i <= this.k; i++)
				sets.put(i, new TreeMap<String, Double>());
			
			for(Map.Entry<String, Map<Integer, DiffExp>> gene : this.diffexp.entrySet())
			{
				int max = Stat.argMin(gene.getValue(), (DiffExp e) -> e.pAdjusted());
				group.put(gene.getKey(), max);
				sets.get(max).put(gene.getKey(), gene.getValue().get(max).pAdjusted());
			}
			
			for(Map.Entry<Integer, Map<String, Double>> genes_in_group : sets.entrySet())
				Syo.pl(genes_in_group.getKey() +" "+ Stat.argMin(genes_in_group.getValue())+" "+ genes_in_group.getValue().get(Stat.argMin(genes_in_group.getValue())));
			
			//for(String gene : genes)
			//	So.p("\t" + Stat.argMin(diffexp.get(gene)));
			
			Syo.pl();
			// Measurement Imports
			
			//File filtered_folder = new File(outs_folder, "filtered_feature_bc_matrix");
			
			//Map<Integer, String> gene_names = names(new File(filtered_folder, "features.tsv"));
			//Map<String, Integer> gene_numbers = numbers(new File(filtered_folder, "features.tsv"));
		}
		

		
		public void reduceToSubset(String[] include, String[] exclude)
		{
			final Set<Integer> cellsToDrop = new TreeSet<Integer>();
			for(Entry<Integer, Cell> entry : this.cells.entrySet())
			{
				Integer cell_number = entry.getKey();
				Cell cell = entry.getValue();
				
				for(String gene : include)
					if(!cell.hasGene(gene))
						cellsToDrop.add(cell_number);
				
				for(String gene : exclude)
					if(cell.hasGene(gene))
						cellsToDrop.add(cell_number);
			}
			this.cells.keySet().removeAll(cellsToDrop);
		}
		
		public Map<String, DoubleHistogram> geneHistogram()
		{
			return this.sampleExpression(
						() -> new DoubleHistogram(-5, 25, 180, true), 
						(double x) -> Meth.log2(x)
					);		
		}
		
		public Map<String, Range> geneRange()
		{
			return this.geneStat(() -> new Range());		
		}
		
		public Map<String, MeanData> geneMean()
		{
			return this.geneStat(() -> new MeanData());		
		}
		
		public <T extends Updatable> Map<String, T> geneStat(Supplier<T> data)
		{
			return this.sampleExpression(data, (double x) -> Meth.log2(x));		
		}
		
		public void reductToSignature(Map<String, IntPredicate> signature)
		{
			this.cells.keySet().retainAll(this.matching(signature));
		}
		
		public Set<Integer> matching(Map<String, IntPredicate> signature)
		{
			Set<Integer> matching = new TreeSet<Integer>();
			
			for(Entry<Integer, Cell> cell_entry : this.cells.entrySet())
			{
				Cell cell = cell_entry.getValue();
				boolean matches = true;
				for(Entry<String, IntPredicate> entry : signature.entrySet())
				{
					Integer value = cell.getCount(entry.getKey());
					if(value == null)
						value = 0;
					matches &= entry.getValue().test(value);
				}
				if(matches)
					matching.add(cell_entry.getKey());
			}
			
			return matching;
		}
		
		public String counts()
		{
			final int current = this.cells.size();
			final int all = this.cells.size();
			return current + "/" + all + "=" + ((double)current/all);
		}
		
		public double[] geneValues(String gene_name)
		{
			final int gene_number = this.geneNumber(gene_name);
			
			List<Integer> list = new ArrayList<Integer>();
			for(Cell cell : this.cells.values())
			{
				Integer value = cell.expression().get(gene_number);
				if(value != null)
					list.add(value);
			}
			double[] values = new double[list.size()];
			for(int i = 0; i < values.length; i++)
				values[i] = list.get(i);
			return values;
		}
		
		public void geneCorrelation(String gene_x, String gene_y) throws IOException
		{
			Plot pp = new Plot(new Box(20, 20, 80, 80), new Span(-.5, 10, -.5, 10));
			
			pp.rendText(6);
			pp.drawXAxis();
			pp.drawYAxis();
			pp.yMarkup(0, 5, 0);
			pp.xMarkup(0, 5, 0);
			pp.rendColor("none");
			pp.rendFill("black");
			pp.rendFillAlpha(0.1);
			
			pp.rendPtRad(1);
			final int x = this.geneNumber(gene_x);
			final int y = this.geneNumber(gene_y);
			
			CovData c = new CovData();
			
			for(Entry<Integer, Cell> cell : this.cells.entrySet())
			{
				Cell value = cell.getValue();
				Integer value_x = value.expression().get(x);
				if(value_x == null)
					value_x = 0;
				
				Integer value_y = value.expression().get(y);
				if(value_y == null)
					value_y = 0;
				
				double c_x = Meth.log2(value_x+1.0);
				double c_y = Meth.log2(value_y+1.0); 
				
				pp.plotPoint(c_x, c_y);
				
				c.add(c_x, c_y);
			};
			
			pp.rendFillAlpha(1.0);
			
			double interceptYatX = c.interceptYatX();
			double slopeYatX = c.slopeYatX();
			
			pp.rendColor("blue");
			pp.plotLine(0, interceptYatX, 10, slopeYatX * 10 + interceptYatX);
			
			double interceptXatY = c.interceptXatY();
			double slopeXatY = c.slopeXatY();
			
			pp.rendColor("red");
			pp.plotLine(interceptXatY, 0, slopeXatY * 10 + interceptXatY, 10);
			
			pp.rendFill("black");
			pp.plotText(10, 10, Format.decimals(c.r(), 3));
			
			
			SVG.writeToFile(pp.getSVG(), new File(dir, 
						//"correlation--"+ gene_x +"-v-"+ gene_y +".svg"
						this.name + "--corr.svg"
					)
				);
		}
		
		public void genesWithHighCellCount()
		{
			Map<String, VarData> summary = this.geneStat(()-> new VarData());
			final int n = this.cells.size();
			for(Entry<String, VarData> entry : summary.entrySet())
			{
				String gene = entry.getKey();
				VarData stat = entry.getValue();
				if(stat.weight()/n > 0.95)
					Syo.pl("\t" + gene);
			}
		}
		
		public Map<String, Point2D.Double> volcanoCoordinates(Sample that, boolean full)
		{
			Map<String, Point2D.Double> coordinates = new TreeMap<String, Point2D.Double>();
			
			Map<String, Explicit> a = this.sampleExpression(() -> new Explicit(), (double x) -> x);
			Map<String, Explicit> b = that.sampleExpression(() -> new Explicit(), (double x) -> x);
			
			TTest test = new TTest();
			
			for(Entry<String, Explicit> entry : a.entrySet())
			{
				String gene = entry.getKey();
				Explicit a_i = entry.getValue();
				Explicit b_i = b.get(gene);
				if(b_i == null)
					b_i = new Explicit();
				
				int a_z = (full ? this.cell_numbers.size() : this.cells.size()) - a_i.size();
				for(int i = 0; i < a_z; i++)
					a_i.add(0.0);
				for(int i = 0; i < 10; i++)
					a_i.add(1.0);
				
				int b_z = (full ? that.cell_numbers.size() : that.cells.size())	- b_i.size();
				for(int i = 0; i < b_z; i++)
					b_i.add(0.0);
				for(int i = 0; i < 10; i++)
					b_i.add(1.0);
				b_i.add(1.0);
				
				final double fold_change = Meth.log2(a_i.varData().mean()/b_i.varData().mean());
				
				final double p = -Math.log10(test.tTest(a_i.values(), b_i.values()));
				
				coordinates.put(gene, new Point2D.Double(fold_change, p));
			}
			return coordinates;
		}
		
		public void volcanoPlot(String tag, Sample base, double fold_flag, double p_flag, boolean full) throws IOException
		{
			Set<String> highlight = new TreeSet<String>();
			
			Map<String, Point2D.Double> coordinates = this.volcanoCoordinates(base, full);
			
			double fold_max = 0;
			double p_max = 0;
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				p_max = Math.max(p_max, c.y);
				fold_max = Math.max(fold_max, Math.abs(c.x));
			}
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				if(fold_flag < Math.abs(c.x) && p_flag < c.y)
					highlight.add(coordinate.getKey());
			}
			
			Syo.pl(highlight.size());
			
			Plot pp = new Plot(new Box(50, 50, 120, 120), new Span(-fold_max, fold_max, -p_max/20, p_max));
			
			pp.rendText(6);
			pp.rendPtRad(1);
			pp.drawXAxis();
			pp.drawYAxis();
			pp.setTickSize(3);
			pp.yMarkup(0, p_max/3, 0);
			pp.xMarkup(0, fold_max/2, 0);
			pp.rendColor("none");
			pp.rendFill("black");
			
			pp.rendPtRad(1);
			
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				if(c.y < p_max)
				{
					
					if(c.y > p_flag && Math.abs(c.x) > fold_flag)
					{
						pp.rendFillAlpha(1.0);
						if(0 < c.x)
							pp.rendFill("red");
						else if(c.x < 0)
							
							pp.rendFill("blue");
					}
					else
					{
						pp.rendFill("black");
					}
					pp.rendFillAlpha(0.5);
					pp.plotPoint(c);
				}
				
				
			};
			
			pp.rendFillAlpha(1.0);
			pp.rendFill("green");
			pp.getGraphRender().text = pp.getGraphRender().text.changeSize(5); 
			
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				if(highlight.contains(coordinate.getKey()))
				{
					pp.plotText(c.x + (0 < c.x ? 0.5 : -0.5), c.y, 0 < c.x ? TextAlignment.RIGHT : TextAlignment.LEFT, coordinate.getKey());
				}
			
			}
			
			SVG.writeToFile(pp.getSVG(), new File(dir, "volcano--"+ tag + "--" + this.name +"-v-"+ base.name +".svg"));
		}
		
		public void volcanoPlot(String[] list, Sample base, double fold_flag, double fold_max, double p_flag, double p_max, boolean full) throws IOException
		{
			Map<String, Point2D.Double> coordinates = this.volcanoCoordinates(base, full);
			
			File save = new File(dir, "volcano--"+ this.name +"-v-"+ base.name +".tab");
			PrintWriter writer = Piper.printer(save);
			writer.println("Gene name\tlog2(fold change)\tnegative log10(p-value)");
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				String gene = coordinate.getKey();
				Point2D.Double point = coordinate.getValue();
				writer.println(gene + "\t" + point.x + "\t"+ point.y);
			}
			writer.close();
			
			Plot pp = new Plot(new Box(50, 50, 120, 120), new Span(-fold_max, fold_max, -p_max/20, p_max));
			
			pp.rendText(8);
			pp.rendPtRad(1);
			pp.drawXAxis();
			pp.drawYAxis();
			pp.setTickSize(3);
			pp.yMarkup(0, p_max/3, 0);
			pp.xMarkup(0, fold_max/2, 0);
			pp.rendColor("none");
			pp.rendFill("black");
			
			Set<String> highlight = new TreeSet<String>();
			for(String gene : list)
				highlight.add(gene);
			
			pp.rendPtRad(1);
			
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				if(c.y < p_max)
				{
					
					if(c.y > p_flag || Math.abs(c.x) > fold_flag)
					{
						pp.rendFillAlpha(1.0);
						if(0 < c.x)
							pp.rendFill("red");
						else if(c.x < 0)
							
							pp.rendFill("blue");
					}
					else
					{
						pp.rendFill("black");
					}
					pp.rendFillAlpha(0.5);
					pp.plotPoint(c);
				}
				
				
			};
			
			pp.rendFillAlpha(1.0);
			pp.rendFill("green");
			
			for(Entry<String, Point2D.Double> coordinate : coordinates.entrySet())
			{
				Point2D.Double c = coordinate.getValue();
				if(highlight.contains(coordinate.getKey()))
				{
					pp.plotText(c.x + (0 < c.x ? 0.5 : -0.5), c.y, 0 < c.x ? TextAlignment.RIGHT : TextAlignment.LEFT, coordinate.getKey());
					pp.plotPoint(c);
				}
			}
			SVG.writeToFile(pp.getSVG(), new File(dir, "volcano--"+ this.name +"-v-"+ base.name +".svg"));
		}
		
		public List<String> mostCorrelated(String geneX, int size)
		{
			Map<Integer, CovData> correlations = this.correlations(this.geneNumber(geneX));
			List<Indexed> list = new ArrayList<Indexed>();
			for(Entry<Integer, CovData> correlation : correlations.entrySet())
			{
				CovData cov = correlation.getValue();
				if(cov.meanY() > 1.0 && cov.r() < 0.9)
					list.add(new Indexed(correlation.getKey(), cov.r()));
			}
			Collections.sort(list, Indexed.reverse());
			Syo.pl(correlations.get(this.geneNumber("Bsg")).r());
			list = list.subList(0, size);
			List<String> genes = new ArrayList<String>();
			for(Indexed i : list)
				genes.add(this.geneName(i.index()));
			return genes;
		}
		
		public String mostCorrelated(String geneX)
		{
			final int geneX_index = this.geneNumber(geneX);
			Map<Integer, CovData> correlations = this.correlations(geneX_index);
			correlations.remove(geneX_index);
			ArgMax<Integer> max = new ArgMax<Integer>(false);
			max.storeDegenerates();
			
			for(Entry<Integer, CovData> c : correlations.entrySet())
			{
				double r = c.getValue().r();
				if(0.5 < r && r < 2.0)
					max.update(c.getKey(), r);
			}
			Syo.pl(max);
			return this.geneName(max.get());
		}
		
		public Map<Integer, Map<Integer, Float>> correlationz()
		{
			Map<Integer, Map<Integer, Float>> cor = new TreeMap<Integer, Map<Integer, Float>>();
			for(Integer geneX_index : this.gene_names.keySet())
			{
				if(geneX_index%1000 == 0)
					Syo.p(" "+(geneX_index/1000)+"k");
				Map<Integer, Float> map = correlationz(geneX_index);
				if(map != null)
					cor.put(geneX_index, map);
			}
			Syo.pl();
			return cor;
		}
		
		public Map<Integer, Float> correlationz(Integer geneX_index)
		{
			Map<Integer, CovData> corr = correlations(geneX_index);
			if(corr == null)
				return null;
			Map<Integer, Float> r_map = new TreeMap<Integer, Float>();
			for(Entry<Integer, CovData> entry : corr.entrySet())
				r_map.put(entry.getKey(), (float)entry.getValue().r());
			return r_map;
		}
		
		public Map<Integer, CovData> correlations(String geneX_name)
		{
			return this.correlations(this.geneNumber(geneX_name));
		}
		
		public File dir(String fileName)
		{
			return new File(this.dir(), fileName);
		}
		
		public PrintWriter out(String fileName) throws IOException
		{
			return Piper.printer(dir(fileName));
		}
		
		public void printCorrelations(String geneX_name) throws IOException
		{
			PrintWriter out = out("correlations--"+geneX_name+".tab");
			Integer geneX_index = this.geneNumber(geneX_name);
			Map<Integer, CovData> cor =  this.correlations(geneX_index);
			out.println("gene\tn\tr\tz");
			for(Entry<Integer, CovData> entry : cor.entrySet())
			{
				Integer geneY_index = entry.getKey();
				String geneY_name = this.geneName(geneY_index);
				CovData cov = entry.getValue();
				out.println(geneY_name + "\t" + cov.weight() + "\t" + cov.r() +"\t"+ cov.zFisher());
			}
			out.close();
		}
			
		public Map<Integer, CovData> correlations(Integer geneX_index)
		{		
			Map<Integer, CovData> corr = new TreeMap<Integer, CovData>();
			
			for(Cell cell : this.cells.values())
			{
				Integer geneX_value = cell.expression().get(geneX_index);
				if(geneX_value == null)
					geneX_value = 0;
				final double x = Meth.log2(geneX_value+1.0);
				
				for(Entry<Integer, Integer> express : cell.expression().entrySet())
				{
					final int geneY_index = express.getKey();
					int geneY_value = express.getValue();
					final double y = Meth.log2(geneY_value+1.0);
				
					CovData data = corr.get(geneY_index);
					if(data == null)
					{
						data = new CovData();
						corr.put(geneY_index, data);
					}
					data.add(x, y);
				}
			}
			
			if(corr.isEmpty())
				return null;
			
			return corr;
		}
		
		public void updateExperimentRanges()
		{
			for(Cell cell : this.cells.values())
				for(Entry<Integer, Integer> express : cell.expression().entrySet())
				{
					String gene = this.geneName(express.getKey());
					Range range = this.experiment.gene_ranges.get(gene);
					if(range == null)
					{
						range = new Range();
						this.experiment.gene_ranges.put(gene, range);
					}
					range.add(express.getValue());
				}
		}
		
		public MeanData[] means(String... genes)
		{
			final int n = genes.length;
			final MeanData[] means = new MeanData[n];
			for(int i = 0; i < n; i++)
			{
				MeanData data = this.mean.get(genes[i]);
				means[i] = data == null ? new MeanData(0) : data;
			}
			return means;	
		}
		
		public void printRanges()
		{
			this.compute_range();
			ArgMax<String> argmax = new ArgMax<String>(false);
			argmax.storeDegenerates();
			for(Entry<String, Range> r : this.range.entrySet())
				argmax.update(r.getKey(), r.getValue().max());
			Syo.pl(argmax.get() + "\t"+ argmax.value());
		}
		
		public void oneGeneScatter(String gene) throws IOException
		{
			final int gene_no = this.geneNumber(gene);
			
			List<Integer> list = new ArrayList<Integer>();
			
			for(Cell cell : this.cells.values())
			{
				Integer value = cell.expression().get(gene_no);
				if(value != null)
					list.add(value);
			}
			double[][] data = new double[1][list.size()];
			
			double[] row = data[0];
			
			for(int i = 0; i < row.length; i++)
				row[i] = list.get(i);
			
			
			Plot p = new Plot(new Box(20, 20, 80, 80), new Span(-.5, 0.5, -.5, 10));
			
			p.drawYAxis();
			p.plotColumnScatter(data);
			
			SVG.writeToFile(p.getSVG(), new File(dir, this.name + "--scatter(" + gene + ").svg" ));
		}
				
		public Map<String, Map<Integer, DiffExp>> diffexp(File diffexp_data) throws FileNotFoundException, IOException
		{	
			CSVParser parser = new CSVParser(new FileReader(diffexp_data), CSVFormat.DEFAULT);
			
			Iterator<CSVRecord> row_iterator = parser.iterator();
			
			if(row_iterator.hasNext())
				row_iterator.next();
			
			Map<String, Map<Integer, DiffExp>> map = new TreeMap<String, Map<Integer, DiffExp>>();
			
			while(row_iterator.hasNext())
			{
				Map<Integer, DiffExp> gene = new TreeMap<Integer, DiffExp>();
				CSVRecord record = row_iterator.next();
								
				Iterator<String> column_iterator = record.iterator();
				
				if(column_iterator.hasNext())
					column_iterator.next();
				
				if(column_iterator.hasNext())
					map.put(column_iterator.next(), gene);;
				
				int i = 0;
				double mean_value = Double.NaN;
				double fold_value = Double.NaN;
				while(column_iterator.hasNext())
				{
					final double value = Double.parseDouble(column_iterator.next()); 
					if(i % 3 == 0)
						mean_value = value;
					else if(i % 3 == 1)
						fold_value = value;
					else if(i % 3 == 2)
					{
						final int cluster = i/3;
						gene.put(cluster, new DiffExp(this, cluster, mean_value, fold_value, value));
					}
					i++;
				}
			}
			
			parser.close();
			
			return map;
		}
		
		public Set<String> geneNames()
		{
			return Collections.unmodifiableSet(this.gene_numbers.keySet());
		}
		
		public Map<Integer, Cell> sampleCells()
		{
			return Collections.unmodifiableMap(this.cells);
		}
	}
	
	public void printRanges()
	{
		for(Sample sample : this.samples.values())
			sample.printRanges();
	}
	
	public void oneGeneScatter(String gene) throws IOException
	{
		for(Sample sample : this.samples.values())
			sample.oneGeneScatter(gene);
	}
	
	public void examine() throws FileNotFoundException, IOException
	{
		for(Sample mouse : this.samples.values())
			mouse.examine();
	}
	
	public void summary(String[] target) throws FileNotFoundException, IOException
	{
		Syo.pl("target length = " + target.length);
		Syo.pl(target);
		for(Sample sample : this.samples.values())
			sample.summary(target);
		Syo.pl();
	}
	
	public void lowDimensionalRGB(boolean global_norm, String[][] target) throws FileNotFoundException, IOException
	{
		Syo.pl(target);
		
		Range[] range = null;
		
		if(global_norm)
		{
			range = new Range[3];
			for(int i = 0; i < 3; i++)
				range[i] = new Range();
			
			for(Sample sample : this.samples.values())
				sample.updateRange(target, range);
		}
		
		for(Sample sample : this.samples.values())
			sample.lowDimensionalRGB(target, range);
	}
	
	public void reComputeTsne(double perplexity, int iterations, String[] names_of_discarded) throws FileNotFoundException, IOException
	{
		for(Sample sample : this.samples.values())
			sample.reComputeTsne(perplexity, iterations, names_of_discarded);
	}
	
	public ScRnaSeq(File src, Device dev, String... sampleNameFolderPairs) throws IOException
	{
		this.src = src;
		this.device = dev;
		this.samples = new TreeMap<Integer, Sample>();
		this.gene_lists = new File(this.src, "gene_lists");
		this.violin_plots = new File(this.src, "violin_plots");
		for(int i = 0; i < sampleNameFolderPairs.length; i += 2)
			this.samples.put(i/2+1, new Sample(this, sampleNameFolderPairs[i], new File(src, sampleNameFolderPairs[i+1])));
		this.gene_ranges = new TreeMap<String, Range>();
	}
	
	public static Map<Integer, Point2D.Double> cellid_coordinates(File tsne_data) throws FileNotFoundException, IOException
	{
		Map<Integer, List<String>> tnse = CSV.columns(tsne_data, new int[]{0, 1, 2}, new TreeMap<Integer, List<String>>());
		
		List<String> id = tnse.get(0);
		List<String> xs = tnse.get(1);
		List<String> ys = tnse.get(2);
		
		final int n = id.size();
		if(xs.size() != n || ys.size() != n)
			throw new IllegalArgumentException("Columns not equal size.");
		
		Map<Integer, Point2D.Double> coordinates = new TreeMap<Integer, Point2D.Double>();
		for(int i = 1; i < n; i++)
			coordinates.put(
					Integer.parseInt(id.get(i)), 
					new Point2D.Double(
							Double.parseDouble(xs.get(i)), 
							Double.parseDouble(ys.get(i))
						)
				);
		return coordinates;
	}

	public static Map<String, String> max(Map<Integer, String> gene_names, Map<Integer, Sample.Cell> express, Set<String> exclude)
	{
		Map<String, String> max = new TreeMap<String, String>();
		for(Map.Entry<Integer, Sample.Cell> cell : express.entrySet())
		{
			String barcode = express.get(cell.getKey()).barcode();
			int max_expression = 0;
			int max_index = 0;
			for(Map.Entry<Integer, Integer> profile : cell.getValue().expression().entrySet())
			{
				int key = profile.getKey();
				int expression = profile.getValue(); 
				if(max_expression < expression && 
						!exclude.contains(gene_names.get(key))
					)
				{
					max_expression = expression;
					max_index = key;
				}
			}
			if(max_index > 0)
			{
				String gene = gene_names.get(max_index);
				max.put(barcode, gene);
			}
			else
				Syo.pl(barcode);
		}
		return max;
	}
	
	public static Map<String, Integer> clusters(File kmeans_data) throws FileNotFoundException, IOException
	{
		Map<Integer, List<String>> kmeans = CSV.columns(kmeans_data, new int[]{0, 1}, new TreeMap<Integer, List<String>>());
		List<String> id = kmeans.get(0);
		List<String> ks = kmeans.get(1);
		
		final int n = id.size();
		if(ks.size() != n)
			throw new IllegalArgumentException("Columns not equal size.");
		
		Map<String, Integer> clusters = new TreeMap<String, Integer>();
		for(int i = 1; i < n; i++)
			clusters.put(id.get(i), Integer.parseInt(ks.get(i)));
		return clusters;
	}

	public static Map<String, double[]> projections(File pca_data) throws FileNotFoundException, IOException
	{
		Map<Integer, List<String>> pca = CSV.columns(pca_data, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, new TreeMap<Integer, List<String>>());
		List<String> id = pca.get(0);
		
		final int n = id.size();
		
		Map<String, double[]> projections = new TreeMap<String, double[]>();
		for(int i = 1; i < n; i++)
		{
			final double[] values = new double[10];
			projections.put(id.get(i), values);
			for(int j = 1; j <= 10; j++)
				values[j-1] = Double.parseDouble(pca.get(j).get(i));
		}
		return projections;
	}

	public static Map<Integer, String> cell_barcodes(File file) throws IOException
	{
		List<String> data = Parser.toList(file);
		Map<Integer, String> barcodes = new TreeMap<Integer, String>();
		int i = 1;
		for(String code : data)
			barcodes.put(i++, code);
		return barcodes;
	}


	public static Map<Integer, Map<Integer, Integer>> expression(File file) throws IOException
	{
		List<String> data = Parser.toList(file);
		final int n = data.size();
		Map<Integer, Map<Integer, Integer>> cells = new TreeMap<Integer, Map<Integer, Integer>>();
		for(int i = 3; i < n; i++)
		{
			String[] entry = data.get(i).split(" ");
			Integer cell_index = Integer.parseInt(entry[1]);
			Map<Integer, Integer> cell = cells.get(cell_index);
			if(cell == null)
			{
				cell = new TreeMap<Integer, Integer>();
				cells.put(cell_index, cell);
			}
			int gene = Integer.parseInt(entry[0]);
			int value = Integer.parseInt(entry[2]);
			cell.put(gene, value);
		}
		return cells;
	}

	public static String[] cell_gene_order(Map<Integer, Integer> cell_expression, Map<Integer, String> gene_names)
	{
		final int n = cell_expression.size();
		
		final List<Label<Double>> genes = new ArrayList<Label<Double>>(n);
		for(Map.Entry<Integer, Integer> e : cell_expression.entrySet())
			genes.add(new Label<Double>((double)e.getValue(), gene_names.get(e.getKey()).toUpperCase()));
		
		Collections.sort(genes);
		
		String[] just_names = new String[n];
		for(int i = 0; i < n; i++)
			just_names[i] = genes.get(i).label;
		
		return Arrayz.flip(just_names);
	}

	public static double compareOrder(String[] measured_cell, String[] reference_cell)
	{
		double similarity_score = 0.0;
		for(int i = 0; i < measured_cell.length; i++)
		{
			int j = 0;
			boolean found = false;
			while(j < reference_cell.length && !found)
			{
				if(measured_cell[i].equals(reference_cell[j]))
				{
					found = true;
					similarity_score += 1.0/(Math.abs(i-j)+1.0);
				}
				j++;
			}
		}
		return similarity_score;
	}

	public static int[] originalIndex(int size, Set<Integer> toRemove)
	{
		boolean[] discard = new boolean[size];
		for(int index : toRemove)
			discard[index] = true;
		return ScRnaSeq.originalIndex(discard);
	}

	public static int[] originalIndex(int size, int... toRemove)
	{
		boolean[] discard = new boolean[size];
		for(int index : toRemove)
			discard[index] = true;
		return ScRnaSeq.originalIndex(discard);
	}

	public static int[] originalIndex(boolean[] discard)
	{
		int no_removed = 0;
		for(boolean remove : discard)
			no_removed += remove ? 1 : 0;
		
		final int[] map = new int[discard.length-no_removed];
		
		int offset = 0;
		int index = 0;
		while(index < map.length)
		{
			int original = index+offset;
			if(discard[original])
				offset++;
			else
				map[index++] = original;	
		}
		
		return map;
	}

	public static double[][] toMatrix(int no_cells, int no_genes, Map<Integer, Sample.Cell> expression_mapping)
	{
		double[][] expression_matrix = 
				new double[no_cells][no_genes];
		
		for(Entry<Integer, Sample.Cell> entry : expression_mapping.entrySet())
		{
			final int cell_id = entry.getKey();
			double[] cell_row = expression_matrix[cell_id-1]; 
			final Map<Integer, Integer> cell_expressions = entry.getValue().expression();
			for(Entry<Integer, Integer> expression : cell_expressions.entrySet())
				cell_row[expression.getKey()-1] = expression.getValue();
		}
		
		return expression_matrix;
	}
	
	public void reGraphTsne(double perplexity, int iterations) throws FileNotFoundException, IOException
	{
		for(Sample sample : this.samples.values())
			sample.reGraphTsne(perplexity, iterations);
	}
	
	public static Plot plotFormat(boolean tsne)
	{
		return new Plot(
				new Box(10, 5, 80, 80), 
				tsne ? 
						new Span(-60, 60, -50-10, 50+10) : // -10, +10 was for Neno's data
						new Span(-20, 20, -15, 15) 
			);
	}
			
	public static double[][] flatten(Map<Integer, Map<Integer, Point2D.Double>> data)
	{
		int total = 0;
		for(Map<?, ?> group : data.values())
			total += group.size();
		
		final double[][] flat = new double[total][];
		int i = 0;
		for(Map<?, Point2D.Double> group : data.values())
			for(Point2D.Double mouse : group.values())
				flat[i++] = new double[]{mouse.x, mouse.y};
	
		return flat;
	}
	
	public void reductToSignature(Map<String, IntPredicate> signature)
	{
		for(Sample sample : this.samples.values())
			sample.reductToSignature(signature);
	}
	
	public void enrichment() throws IOException
	{
		Syo.pl("start");
		
		DoubleUnaryOperator f = (double x) -> Math.log10(1.0 + x);
		
		for(Entry<Integer, Sample> sample : this.samples.entrySet())
		{
			for(Entry<Integer, Sample> reference : this.samples.entrySet())
			{
				int sample_id = sample.getKey();
				int reference_id = reference.getKey();
				
				if(sample_id >= reference_id)
				{
					Map<String, VarData> other = reference.getValue().sampleExpression(() -> new VarData(), f);
					Map<String, VarData> focus = sample.getValue().sampleExpression(() -> new VarData(), f);
					Map<String, Double> diff = 
							Maps.putApply(
									focus, 
									new VarData(0), 
									other, 
									new VarData(0), 
									new TreeMap<String, Double>(),
									(VarData a, VarData b) -> a.zTest(b, 0.5)
								);
					
					Piper.out(diff, new File(this.src, "expression" + sample_id + "-"+ reference_id +".tab"));
				}
			}
		}		
	}
	
	public void enrichment(int sample_id) throws IOException
	{
		Supplier<VarData> fact = () -> new VarData();
		DoubleUnaryOperator func = (double x) -> Math.log(1.0 + x);	
		Sample sample = this.samples.get(sample_id);
		Map<String, VarData> focus = sample.sampleExpression(fact, func);
		Map<String, VarData> other = new TreeMap<String, VarData>();
		for(Entry<Integer, Sample> reference : this.samples.entrySet())
		{
			if(sample_id != reference.getKey())
				Addable.add(other, reference.getValue().sampleExpression(fact, func));
		}
			Map<String, Double> diff = 
				Maps.putApply(
						focus, 
						new VarData(0), 
						other, 
						new VarData(0), 
						new TreeMap<String, Double>(), 
						(VarData a, VarData b) -> a.zTest(b)
					);
		
		Piper.out("gene", "p-value", diff, new File(this.src, "expression-" + sample_id + "-v-all.tab"));	
	}
	
	public void anova() throws IOException
	{
		Supplier<VarData> fact = () -> new VarData();
		DoubleUnaryOperator func = (double x) -> Math.log(1.0 + x);
		
		Set<String> genes = this.samples.get(1).geneNames();
		Map<Integer, Map<String, VarData>> all = new TreeMap<Integer, Map<String, VarData>>();
		for(Integer id : this.samples.keySet())
		{
			Sample current = this.samples.get(id);
			Map<String, VarData> data = current.sampleExpression(fact, func);
			all.put(id, data);
		}
		
		final int n = this.samples.size();
		Map<String, Double> anova = new TreeMap<String, Double>();		
		for(String gene : genes)
		{
			VarData[] array = new VarData[n];
			int i = 0;
			boolean proceed = true;
			for(Integer id : all.keySet())
			{
				Map<String, VarData> data = all.get(id);
				VarData value = data.get(gene);
				if(value == null)
					proceed = false;
				array[i++] = value;
			}
			if(proceed)
				anova.put(gene, VarData.anova(array));
		}
		Piper.out("gene", "p-value", anova, new File(this.src, "anova.tab"));
	}
	
	public void geneCorrelation() throws FileNotFoundException, IOException
	{
		for(Sample sample : this.samples.values())
			sample.geneCorrelation();
	}

	static void testReindexing()
	{
		{
			boolean[] g = new boolean[] {true, false, true, true, false, false};
			int[] toOriginal = originalIndex(g);
			Syo.pl(toOriginal);
			TreeMap<Integer, Integer> toCondensed = condensedIndex(toOriginal);
			Syo.pl(toCondensed);
		}
		
		{
			int[] toOriginal = originalIndex(6, 0, 2, 3);
			Syo.pl(toOriginal);
			TreeMap<Integer, Integer> toCondensed = condensedIndex(toOriginal);
			Syo.pl(toCondensed);
		}
	}

	public static TreeMap<Integer, Integer> condensedIndex(int[] original)
	{
		TreeMap<Integer, Integer> index = new TreeMap<Integer, Integer>();
		for(int i = 0; i < original.length; i++)
			index.put(original[i], i);
		return index;
	}
	
	public void differentialExpression() throws IOException
	{
		for(Sample mouse : this.samples.values())
		{
			Syo.pl(mouse.dir().getName());
			
			Map<Integer, Map<String, Double>> clusterEnriched = new TreeMap<Integer, Map<String, Double>>();
			for(int i = 1; i <= mouse.k; i++)
				clusterEnriched.put(i, new TreeMap<String, Double>());
			for(Map.Entry<String, Map<Integer, Sample.DiffExp>> entry : mouse.diffexp.entrySet())
			{
				String gene = entry.getKey();
				Map<Integer, Sample.DiffExp> between = entry.getValue(); 
				int enriched = Stat.argMin(between, (Sample.DiffExp a) -> a.pAdjusted());
				double probability = between.get(enriched).pAdjusted();
				clusterEnriched.get(enriched).put(gene, probability);
			}
			
			for(Map.Entry<Integer, Map<String, Double>> genes_in_group : clusterEnriched.entrySet())
			{
				int cluster = genes_in_group.getKey(); 
				String gene = Stat.argMin(genes_in_group.getValue());
				Syo.pl(cluster + " " + gene + " " + genes_in_group.getValue().get(gene));
			}
			Syo.pl();
		}		
	}
	
	public void violinPlotsGeneAdjecent(String[] genes, int print_explicit) throws IOException
	{		
		int noSamples = this.samples.size();
		String[] color = SvgColor.getDistinct(noSamples, 0.0f, 0.6f, 0.7f);
		for(Sample mouse : this.samples.values())
			mouse.compute_histogram();
		for(int i = 0; i < genes.length; i++)
		{
			if(print_explicit >= 1)
				Syo.pl(genes[i]);
			Box box = new Box(
					new Rectangle(100, 50, 15*noSamples, 30), 
					new Rectangle(0, 0, 20*noSamples+200, 80+100)
				);		
			Plot pp = new Plot(box, new Span(-0.75, noSamples - 0.25, 0.0, 9.0));
			Text text = new Text(8);
			text = text.changeTransform(new TextOrientation(-90));
			text = text.changeAlignment(TextAlignment.OVER);
			pp.drawText(100-15, box.graph.y + box.graph.height/2, genes[i], text);
			pp.rendPrecision(3);
			pp.rendWidth(1.0);
			pp.drawYAxis();
			pp.setTickSize(3);
			pp.yMarkup(0, 3.0, 0);
			pp.setRound();
			pp.setEndRound();
			pp.rendPtRad(1.0);
			pp.rendWidth(0.5);
			double[][] sample_cell_values = new double[noSamples][];
			VarData[] stats = new VarData[noSamples];
			double[][][] curves = new double[noSamples][][];			
			Range range = new Range();
			int count = 0;
			for(Sample mouse : this.samples.values())
			{
				DoubleHistogram dh = mouse.histogram.get(genes[i]);
				double[] cell_values = mouse.geneValues(genes[i]);
				Ip.apply(cell_values, Meth::log2);
				VarData stat = new VarData();
				stats[count] = stat;
				stat.addAll(cell_values);
				sample_cell_values[count] = cell_values;
				
				if(print_explicit == 2)
					Syo.pl(mouse.name+ "\t" + stat.mean() +"\t"+stat.stdDev());
				
				if(dh!= null)
				{	
					double[][] pdf = new double[2][];
					pdf[0] = dh.binMiddles();
					pdf[1] = dh.counts();
					Ip.div(pdf[1], mouse.cells.size());
					pdf[1] = LowPass.rcZeroPhase(1.0, Meth.rcfc(0.05), pdf[1]);
					range.add(pdf[1]);
					curves[count] = pdf;
				}
				
				count++;
			}			
			double max = range.max();
			count = 0;
			for(Sample mouse : this.samples.values())
			{
				Text label = new Text(8);
				final double[][] pdf = curves[count];
				if(pdf != null)
				{
					pdf[0] = Arrays.copyOfRange(pdf[0], 30, 90-6);
					pdf[1] = Arrays.copyOfRange(pdf[1], 30, 90-6);
					Ip.div(pdf[1], max);
					if(print_explicit == 3)
					{
						Syo.p(mouse.name);
						for(double pi : pdf[0])
							Syo.p("\t" + pi);
						Syo.pl();
					}
					Ip.mult(pdf[1], 0.4);
					pp.rendFill(color[count]);
					double[] p = Op.append(Op.add(pdf[1], 0.0+count), Op.add(Op.neg(Op.flip(pdf[1])), 0.0+count), new double[]{pdf[1][0]+count}); 
					double[] v = Op.append(pdf[0], Op.flip(pdf[0]), new double[]{pdf[0][0]});
					pp.plotCurve(p, v);
					pp.rendFillAlpha(1.0);
					pp.rendColor("black");
					int x = 100+14*count+11;
					int y = box.graph.y + box.graph.height+5;
					pp.drawText(x, y, mouse.name(), label.changeTransform(new TextOrientation(-45)));
				}
				count++;
			}
			pp.rendFillAlpha(1.0);
			pp.rendFill("none");
			pp.rendColor("black");
			pp.plotColumnStdDev(sample_cell_values, 0.4);
			SVG.writeToFile(pp.getSVG(), new File(this.violin_plots, "violin--"+genes[i]+".svg"));
		}
	}
		
	public void load_clusters(int k) throws FileNotFoundException, IOException
	{
		for(Sample sample : this.samples.values())
			sample.load_clusters(k);
	}
	
	public List<String> summary()
	{
		List<String> summary = new ArrayList<String>(this.samples.size());
		for(Entry<Integer, Sample> entry : this.samples.entrySet())
			summary.add(entry.getKey() +"\t"+ entry.getValue().counts());
		return summary;
	}
	
	public void genesWithHighCellCount()
	{
		for(Entry<Integer, Sample> entry : this.samples.entrySet())
		{
			Syo.pl(entry.getValue().name);
			entry.getValue().genesWithHighCellCount();
		}
	}
	
	public void volanos(String[] list, int[] pairs, double fold_flag, double fold_max, double p_flag, double p_max, boolean full) throws IOException 
	{
		if(pairs.length%2 != 0)
			throw new IllegalArgumentException("must provides paired indicies");
		
		for(int i = 0; i < pairs.length; i += 2)
			this.samples.get(pairs[i+1]).volcanoPlot(list, this.samples.get(pairs[i]), fold_flag, fold_max, p_flag, p_max, full);
	}
	
	public void volano(int base, double fold_flag, double fold_max, double p_flag, double p_max, boolean full) throws IOException 
	{
		Sample that = this.samples.get(base);
		
		for(Sample thus: this.samples.values())
		{
			Syo.pl(thus.name);
			thus.volcanoPlot(null, that, fold_flag, fold_max, p_flag, p_max, full);
		}	
	}
	
	public void geneCorrelation(String... genes) throws IOException
	{
		if(genes.length != 2)
			throw new IllegalArgumentException("array must be only pair of genes");
		String gene_x = genes[0];
		String gene_y = genes[1];
		Syo.pl(gene_x +"\t"+ gene_y);
		for(Sample thus: this.samples.values())
			thus.geneCorrelation(gene_x, gene_y);
	}
	
	public void printMostCorrelated(String gene, int size)
	{
		for(Entry<Integer, Sample> entry : this.samples.entrySet())
		{
			Sample sample = entry.getValue();
			List<String> genes = sample.mostCorrelated(gene, size);
			Syo.p(sample.name);
			for(String geneY : genes)
				Syo.p("\t" + geneY);
			Syo.pl();
		}
	}
	
	public void printMostCorrelated(String gene)
	{
		for(Entry<Integer, Sample> entry : this.samples.entrySet())
		{
			Sample sample = entry.getValue();
			String genes = sample.mostCorrelated(gene);
			Syo.pl(sample.name+ "\t" + genes);
			
		}
	}
	
	public void correlations(String gene) throws IOException
	{
		for(Sample sample : this.samples.values())
		{
			Syo.pl(sample.name);
			Piper.ser(sample.correlations(gene), new File(sample.dir(), "correlations--"+gene+".ser"));
		}
	}
	
	public void printCorrelations(String... genes) throws IOException
	{
		for(String gene : genes)
		{
			Syo.pl(gene);
			for(Sample sample : this.samples.values())
			{
				Syo.pl(sample.name);
				sample.printCorrelations(gene);
			}
			Syo.pl();
		}
		
	}
	
	public void circlePlot(int print, String... genes) throws IOException
	{
		final int n = this.samples.size();
		MeanData[][] means = new MeanData[n][];
		int[] sizes = new int[n];
		for(Entry<Integer, Sample> entry : this.samples.entrySet())
		{
			Integer key = entry.getKey()-1; 
			Sample sample = entry.getValue();
			sample.compute_mean();
			Syo.pl(key);
			means[key] = sample.means(genes);
			sizes[key] = sample.cells.size();
		}
		int radii = 14;
		
		Box box = new Box(50, 50, radii*genes.length, radii*5); 
		Plot p = new Plot(box, new Span(-0.5, genes.length - 0.5, -0.5, 4.5));		
		p.rendWidth(0.5);
		p.rendFill("green");
		p.circleGridPlot(means, sizes, print);
		
		Text label = new Text(8);
		for(int i = 0; i < genes.length; i++)
			p.drawText(50+radii*i + 10, radii*5+50+2, genes[i], label.changeTransform(new TextOrientation(-45)));
		
		for(int i = 0; i < n; i++)
			p.drawText(50-4, radii*i+50+6, this.samples.get(i+1).name, label.changeTransform(new TextOrientation(-45)));
		box.graph.y += 20+80;
		SVG.writeToFile(p.getSVG(), new File(this.src, "dot_plot.svg"));
	}
	
	public void updateRanges() throws IOException
	{
		for(Sample sample : this.samples.values())
			sample.updateExperimentRanges();
	}
}
