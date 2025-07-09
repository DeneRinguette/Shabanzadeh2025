package shabanzadeh2025.rend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * SVG file writing.
 * 
 * @author Dene Ringuette
 */

public class SVG
{
	public static DocType docType()
	{
		return new DocType("svg", "-//W3C//DTD SVG 1.1//EN", 
			"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"); 
	}	

	public static final Namespace namespace = 
		Namespace.getNamespace("http://www.w3.org/2000/svg");
	
	public static final Namespace xlink = 
			Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
	
	public static void writeToFile(Document svg, File file) throws IOException
	{
		Format format = Format.getPrettyFormat();
		XMLOutputter xmlOut = new XMLOutputter(format);
		xmlOut.output(svg, new PrintWriter(new FileWriter(file), true));
	}
	
	public static void writeToFile(Document svg, String file) throws IOException
	{
		SVG.writeToFile(svg, new File(file));
	}	
}
