package shabanzadeh2025.rend;

import java.awt.Point;
import java.util.Map;
import java.util.SortedMap;

import org.jdom2.Document;
import org.jdom2.Element;

import shabanzadeh2025.rend.Stroke.LineCap;
import shabanzadeh2025.rend.Stroke.LineJoin;
import shabanzadeh2025.util.Circle;

/**
 * Generic drawing class for all plot types.
 *  
 * @author Dene Ringuette
 */

public class Draw
{
	protected GraphRender rend;
	
	private int precision;

	protected Box box;

	protected Element svg;

	protected Element defs;

	protected int imageCount = 0;
	
	protected Point perspective;

	public Draw(Box plotBox, int numberOfGraphs)
	{
		this.box = plotBox;
		this.svg = new Element("svg", SVG.namespace);
		String graphWidth = "" + plotBox.totalWidth();
		String graphHeight = "" + (plotBox.totalHeight() * numberOfGraphs);
		this.svg.setAttribute("width", graphWidth);
		this.svg.setAttribute("height", graphHeight);
		this.svg.setAttribute("version", "1.1");
		this.svg.setAttribute("viewBox", "0 0 " + graphWidth + " " + graphHeight);
		this.rendDefault();
		this.rendPrecision(2);
	}
	
	public Box getPlotBox()
	{
		return this.box;
	}
	
	public void setPlotBox(Box box)
	{
		this.box = box;
	}
	
	public Draw(Box plotBox)
	{
		this(plotBox, 1);
	}
	
	public void rendDefault()
	{
		this.rend = new GraphRender();
	}

	public void drawBox()
	{
		this.drawRect(this.box.left(), this.box.top(), this.box.width(), this.box.height());
	}
	
	public void addDefs()
	{
		this.defs = new Element("defs", SVG.namespace);
		this.svg.addContent(this.defs);
	}
	
	public void drawXAxis()
	{
		this.drawLine(this.box.left(), this.box.bottom(), this.box.right(), this.box.bottom());
	}
	
	public void drawYAxis()
	{
		this.drawLine(this.box.left(), this.box.bottom(), this.box.left(), this.box.top());
	}
	
	public void drawAxes()
	{
		this.drawXAxis();
		this.drawYAxis();
	}
	
	public void linkImage(String relativeName)
	{
		this.linkImage(this.box.left(), this.box.top(), this.box.width(), this.box.height(), relativeName);
	}
	
	public void linkImage(double x, double y, double w, double h, String relativeName)
	{
		this.linkImage(str(x), str(y), str(w), str(h), relativeName);
	}
	
	public void linkImage(String x, String y, String w, String h, String relativeName)
	{
		Element image = new Element("image", SVG.namespace);
		image.addNamespaceDeclaration(SVG.xlink);
		image.setAttribute("href", relativeName, SVG.xlink);
		image.setAttribute("x", x);
		image.setAttribute("y", y);
		image.setAttribute("width", w);
		image.setAttribute("height", h);
		image.setAttribute("preserveAspectRatio", "none");
		image.setAttribute("viewBox", x + " " + y + " " + w + " " + h);
		image.setAttribute("id", "image" + this.imageCount++);		
		this.svg.addContent(image);
	}
	
	public void rendPrecision(int arg)
	{
		if(arg < 0)
			throw new IllegalArgumentException("precision must not be negative");
		this.precision = arg;
	}
	
	public String str(double x)
	{
		return Format.decimals(x, this.precision);
	}
	
	public String[] str(double... x)
	{
		String[] data = new String[x.length];
		for(int i = 0; i < x.length; i++)
			data[i] = this.str(x[i]);
		return data;
	}
	
	public void drawCircle(double cx, double cy, double r)
	{
		this.drawCircle(str(cx), str(cy), str(r));
	}
	
	public void drawCircle(Circle c)
	{
		this.drawCircle(c.radius(), c.centerX(), c.centerY());
	}
	
	public void drawCircle(String cx, String cy, String r)
	{
		Element circle = new Element("circle", SVG.namespace);
		circle.setAttribute("cx", cx);
		circle.setAttribute("cy", cy);
		circle.setAttribute("r", r);
		this.setAll(circle);
		this.svg.addContent(circle);
	}
	
	public void drawCurve(double[] x, double[] y)
	{
		int n = x.length;
		if(n != y.length)
			throw new IllegalArgumentException("Unpaired x-y: " + x.length + " != " + y.length);
		if(n < 2)
			throw new IllegalArgumentException("Path too short");
		Element path = new Element("path", SVG.namespace);
		StringBuffer buffer = new StringBuffer();
		buffer.append("M" + str(x[0]) + " " + str(y[0]));
		for(int i = 1; i < n; i++)
			buffer.append("L" + str(x[i]) + " " + str(y[i]));
		path.setAttribute("d", buffer.toString());
		this.setAll(path);
		this.svg.addContent(path);
	}
	
	public void drawDomain(double[] x, double[] y)
	{
		int n = x.length;
		if(n != y.length)
			throw new IllegalArgumentException("Unpaired x-y: " + x.length + " != " + y.length);
		if(n < 2)
			throw new IllegalArgumentException("Path too short");
		Element path = new Element("path", SVG.namespace);
		StringBuffer buffer = new StringBuffer();
		buffer.append("M" + str(x[0]) + " " + str(y[0]));
		for(int i = 1; i < n; i++)
			buffer.append("L" + str(x[i]) + " " + str(y[i]));
		path.setAttribute("d", buffer.toString());
		this.setAll(path);
		this.svg.addContent(path);
	}
	
	public void drawCurve(SortedMap<Double, Double> xtoy)
	{
		if(xtoy.size() < 2)
			throw new IllegalArgumentException("Path too short");
		
		StringBuffer buffer = new StringBuffer();
		for(Map.Entry<Double, Double> e : xtoy.entrySet())
			buffer.append("L" + str(e.getKey()) + " " + str(e.getValue()));
		buffer.setCharAt(0, 'M');
		
		Element path = new Element("path", SVG.namespace);
		path.setAttribute("d", buffer.toString());
		this.setAll(path);
		this.svg.addContent(path);
	}
	
	public void drawCurve(String[] x, String[] y)
	{
		int n = x.length;
		if(n != y.length)
			throw new IllegalArgumentException();

		Element path = new Element("path", SVG.namespace);
		StringBuffer buffer = new StringBuffer();
		buffer.append("M" + x[0] + " " + y[0]);
		for(int i = 1; i < n; i++)
			buffer.append("L" + x[i] + " " + y[i]);
		path.setAttribute("d", buffer.toString());
		this.setAll(path);
		this.svg.addContent(path);
	}
	
	public void drawEllipse(double cx, double cy, double rx, double ry)
	{
		this.drawEllipse(str(cx), str(cy), str(rx), str(ry));
	}
	
	public void drawEllipse(double cx, double cy, double rx, double ry, double theta)
	{
		this.drawEllipse(str(cx), str(cy), str(rx), str(ry), str(theta));
	}
	
	private void setAll(Element element)
	{
		this.setStroke(element);
		this.setDash(element);
		this.setFill(element);	
	}

	private void setStroke(Element element)
	{
		Stroke stroke = this.rend.stroke;
		if(stroke == null)
		{
			element.setAttribute("stroke", "none");
		}
		else
		{
			element.setAttribute("stroke", stroke.color);
			element.setAttribute("stroke-width", "" + stroke.width);
			if(0 <= stroke.alpha && stroke.alpha < 1.0)
				element.setAttribute("stroke-opacity", "" + stroke.alpha);
			if(stroke.join != Stroke.LineJoin.MITER)
				element.setAttribute("stroke-linejoin", stroke.join.value);
			if(stroke.end != Stroke.LineCap.BUTT)
				element.setAttribute("stroke-linecap", stroke.end.value);
		}
	}
	
	private void setDash(Element element)
	{
		Dash dash = this.rend.dash;
		if(dash != null)
		{
			if(dash.array != null)
			{
				element.setAttribute("stroke-dasharray", dash.array);
				
				if(dash.offset != 0)
					element.setAttribute("stroke-dashoffset", "" + dash.offset);
			}
		}
	}
	
	private void setFill(Element element)
	{
		Fill fill = this.rend.fill;
		if(fill.link)
		{
			element.setAttribute("fill", "url(#" + fill.color + ")");
		}
		else
		{
			element.setAttribute("fill", fill.color);
			if(fill.alpha < 1.0)
				element.setAttribute("fill-opacity", "" + fill.alpha);
		}
	}
	
	public void drawEllipse(String cx, String cy, String rx, String ry)
	{
		Element ellipse = new Element("ellipse", SVG.namespace);
		ellipse.setAttribute("cx", cx);
		ellipse.setAttribute("cy", cy);
		ellipse.setAttribute("rx", rx);
		ellipse.setAttribute("ry", ry);
		this.setAll(ellipse);
		this.svg.addContent(ellipse);
	}
	
	public void drawEllipse(String cx, String cy, String rx, String ry, String theta)
	{
		Element ellipse = new Element("ellipse", SVG.namespace);
		ellipse.setAttribute("cx", cx);
		ellipse.setAttribute("cy", cy);
		ellipse.setAttribute("rx", rx);
		ellipse.setAttribute("ry", ry);
		ellipse.setAttribute("transform", "rotate("+ theta + " " + cx + " " + cy + ")");
		this.setAll(ellipse);
		this.svg.addContent(ellipse);
	}
	
	public void drawArc(double ax, double ay, double rx, double ry, double bx, double by, boolean large_arg_flag)
	{
		this.drawArc(str(ax), str(ay), str(rx), str(ry), str(bx), str(by), large_arg_flag ? "1" : "0");
	}
	
	public void drawArc(String ax, String ay, String rx, String ry, String bx, String by, String large_arc_flag)
	{
		Element path = new Element("path", SVG.namespace);
		StringBuffer buffer = new StringBuffer();
		buffer.append("M " + ax + " " + ay + " A "+ rx + " " + ry + " 0 "+large_arc_flag+" 0 " + bx + " " + by);
		path.setAttribute("d", buffer.toString());
		this.setAll(path);
		this.svg.addContent(path);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		this.drawLine(str(x1), str(y1), str(x2), str(y2));
	}
	
	public void drawLine(String x1, String y1, String x2, String y2)
	{
		Element line = new Element("line", SVG.namespace);
		line.setAttribute("x1", x1);
		line.setAttribute("y1", y1);
		line.setAttribute("x2", x2);
		line.setAttribute("y2", y2);
		this.setStroke(line);
		this.setDash(line);
		this.svg.addContent(line);
	}
	
	public void defRadialGradient(String id, String start_color, String start_alpha, String stop_color, String stop_alpha)
	{
		Element gradient = new Element("radialGradient", SVG.namespace);
		gradient.setAttribute("id", id);
		gradient.setAttribute("cx", "50%");
		gradient.setAttribute("cy", "50%");
		gradient.setAttribute("r",  "50%");
		gradient.setAttribute("fx", "50%");
		gradient.setAttribute("fy", "50%");
		
		Element stop0 = new Element("stop", SVG.namespace);
		stop0.setAttribute("offset", "0%");
		stop0.setAttribute("style", "stop-color:" + start_color + ";stop-opacity:" + start_alpha);
		gradient.addContent(stop0);
		
		Element stop1 = new Element("stop", SVG.namespace);
		stop1.setAttribute("offset", "100%");
		stop1.setAttribute("style", "stop-color:" + start_color + ";stop-opacity:" + stop_alpha);
		gradient.addContent(stop1);
		
		this.defs.addContent(gradient);
	}
	
	public void defRadialGaussian(String id, String start_color)
	{
		Element gradient = new Element("radialGradient", SVG.namespace);
		gradient.setAttribute("id", id);
		gradient.setAttribute("cx", "50%");
		gradient.setAttribute("cy", "50%");
		gradient.setAttribute("r",  "50%");
		gradient.setAttribute("fx", "50%");
		gradient.setAttribute("fy", "50%");
		
		for(double z = 0.0; z <= 4; z += 0.25)
		{
			Element stop0 = new Element("stop", SVG.namespace);
			stop0.setAttribute("offset", Format.decimals(25*z, 0) + "%");
			stop0.setAttribute("style", "stop-color:" + start_color + ";stop-opacity:" + Format.decimals(Math.exp(-z*z/2), 3));
			gradient.addContent(stop0);
		}
		
		this.defs.addContent(gradient);
	}
		
	public void drawVect(double x, double y, double u, double v)
	{
		this.drawLine(str(x), str(y), str(x+u), str(y+v));
	}
	
	public void drawRect(double x, double y, double width, double height)
	{
		this.drawRect(str(x), str(y), str(width), str(height));
	}
	
	public void drawRect(String x, String y, String width, String height)
	{
		Element rect = new Element("rect", SVG.namespace);
		rect.setAttribute("x", x);
		rect.setAttribute("y", y);
		rect.setAttribute("width", width);
		rect.setAttribute("height", height);
		this.setAll(rect);
		this.svg.addContent(rect);
	}
	
	public void drawText(double graphX, double graphY, TextAlignment align, String content)
	{
		this.drawText(graphX, graphY, content, this.rend.text.changeAlignment(align));
	}
	
	public void drawStar(double graphX, double graphY, TextAlignment align, String content)
	{
		this.drawText(graphX, graphY, content, this.rend.star.changeAlignment(align));
	}
	
	public void drawText(double graphX, double graphY, String content)
	{
		this.drawText(graphX, graphY, content, this.rend.text);
	}
	
	public void drawText(double graphX, double graphY, String content, Text text)
	{
		Element label = new Element("text", SVG.namespace);
		
		String x = str(graphX);
		String y = str(graphY);
		
		label.setAttribute("x", x);
		label.setAttribute("y", y);
		
		label.setAttribute("font-family", text.style());
		label.setAttribute("font-size", "" + text.size());
		label.setAttribute("fill", text.color());
		label.setAttribute("text-anchor", text.alignment().horizontal());
		label.setAttribute("dominant-baseline", text.alignment().vertical());
		if(text.hasTransform())
			label.setAttribute("transform", text.transform().rotation(x, y));
			
		label.setText(content);
		
		this.svg.addContent(label);
	}
	
	public void drawTitle(int size, String title)
	{
		this.drawText(
				box.middleX(), 
				box.top(), 
				title, 
				new Text(
						TextAlignment.TOP, 
						this.rend.text.style(), 
						size
					)
			);
	}
	
	public void drawXLabel(String xAxisName, String xAxisUnits)
	{
		Element xLabel = new Element("text", SVG.namespace);
		Point bm = box.xLabel();
		xLabel.setAttribute("x", "" + bm.x);
		xLabel.setAttribute("y", "" + bm.y);
		xLabel.setAttribute("font-family", this.rend.text.style());
		xLabel.setAttribute("font-size", "" + this.rend.text.size());
		xLabel.setAttribute("fill", "black");
		xLabel.setAttribute("text-anchor", "middle");
		if(xAxisUnits == null)
			xLabel.setText(xAxisName);
		else
			xLabel.setText(xAxisName + " (" + xAxisUnits + ")");
		this.svg.addContent(xLabel);
	}
	
	public void drawXLabel(String xAxisName, String xAxisUnits, int offset)
	{
		Element xLabel = new Element("text", SVG.namespace);
		xLabel.setAttribute("x", "" + box.middleX());
		xLabel.setAttribute("y", "" + (box.bottom()+offset));
		xLabel.setAttribute("font-family", this.rend.text.style());
		xLabel.setAttribute("font-size", "" + this.rend.text.size());
		xLabel.setAttribute("fill", "black");
		xLabel.setAttribute("text-anchor", "middle");
		if(xAxisUnits == null)
			xLabel.setText(xAxisName);
		else
			xLabel.setText(xAxisName + " (" + xAxisUnits + ")");
		this.svg.addContent(xLabel);
	}
	
	public void drawYLabel(String yAxisName, String yAxisUnits)
	{
		Element yLabel = new Element("text", SVG.namespace);
		Point point = box.yLabel();
		yLabel.setAttribute("x", "" + point.x);
		yLabel.setAttribute("y", "" + point.y);
		yLabel.setAttribute("font-family", this.rend.text.style());
		yLabel.setAttribute("font-size", "" + this.rend.text.size());
		yLabel.setAttribute("fill", "black");
		yLabel.setAttribute("text-anchor", "middle");
		yLabel.setAttribute("dominant-baseline", "central");
		if(yAxisUnits == null)
			yLabel.setText(yAxisName);
		else
			yLabel.setText(yAxisName + " (" + yAxisUnits + ")");
		this.svg.addContent(yLabel);
	}
	
	public void drawYLabel(String yAxisName, String yAxisUnits, int offset)
	{
		Element yLabel = new Element("text", SVG.namespace);
		yLabel.setAttribute("x", "" + (box.left()-offset));
		yLabel.setAttribute("y", "" + box.middleY());
		yLabel.setAttribute("font-family", this.rend.text.style());
		yLabel.setAttribute("font-size", "" + this.rend.text.size());
		yLabel.setAttribute("fill", "black");
		yLabel.setAttribute("text-anchor", "middle");
		yLabel.setAttribute("dominant-baseline", "central");
		if(yAxisUnits == null)
			yLabel.setText(yAxisName);
		else
			yLabel.setText(yAxisName + " (" + yAxisUnits + ")");
		this.svg.addContent(yLabel);
	}
	
	public void drawYLabels(String... yAxisNames)
	{
		int step = (int)Math.round(1.5*this.rend.text.size());
		int offset = - step*(yAxisNames.length/2);
		if(yAxisNames.length%2 == 0)
			offset += step/2;
		for(String yAxisName : yAxisNames)
		{
			Element yLabel = new Element("text", SVG.namespace);
			yLabel.setAttribute("x", "" + (box.left() - 75));
			yLabel.setAttribute("y", "" + (box.middleY() + offset));
			yLabel.setAttribute("font-family", this.rend.text.style());
			yLabel.setAttribute("font-size", "" + this.rend.text.size());
			yLabel.setAttribute("fill", "black");
			yLabel.setAttribute("text-anchor", "middle");
			yLabel.setAttribute("dominant-baseline", "central");
			yLabel.setText(yAxisName);
			this.svg.addContent(yLabel);
			offset += step;
		}
	}
			
	public Document getSVG()
	{
		return new Document(this.svg, SVG.docType());
	}
	
	public void rendColor(int red, int green, int blue)
	{
		this.rendColor(SvgColor.make(red, green, blue));
	}
	
	public void rendColor(String color)
	{
		this.rend.stroke = this.rend.stroke.changeColor(color);
	}
	
	public void rendFill(int red, int green, int blue)
	{
		this.rendFill(SvgColor.make(red, green, blue));
	}
	
	public void rendFill(String fill)
	{
		this.rend.fill = this.rend.fill.changeColor(fill);
	}
	
	public void rendFillLink(String id)
	{
		this.rend.fill = Fill.link(id);
	}
	
	public void rendFillAlpha(double alpha)
	{
		if(alpha < 0.0 || alpha > 1.0)
			throw new IllegalArgumentException("Invalid alpha range.");
		this.rend.fill = this.rend.fill.changeAlpha(alpha);
	}
	
	public void rendAlpha(double alpha)
	{
		if(alpha < 0.0 || alpha > 1.0)
			throw new IllegalArgumentException("Invalid alpha range.");
		this.rend.stroke = this.rend.stroke.changeAlpha(alpha);
	}
	
	public void rendPtRad(double arg)
	{
		this.rend.point = arg;
	}
	
	public void rendText(int pt)
	{
		this.rend.text = this.rend.text.changeSize(pt);
	}
	
	public void rendWidth(double width)
	{
		this.rend.stroke = this.rend.stroke.changeWidth(width);
	}
	
	public void noStroke()
	{
		this.rend.stroke = null;
	}
	
	public void rend(int width, String color)
	{
		this.rendWidth(width);
		this.rendColor(color);
	}
	
	public void clearStyle()
	{
		this.rend.dash = null;
		this.rend.stroke = this.rend.stroke.changeJoin(LineJoin.MITER);
		this.rend.stroke = this.rend.stroke.changeEnd(LineCap.BUTT);
	}
	
	public void setStyle(int segment, int gap)
	{
		this.setStyle(segment + ", " + gap);
	}
	
	public void setStyle(String style)
	{
		if(this.rend.dash == null)
			this.rend.dash = new Dash(style, 0);
		else
			this.rend.dash = this.rend.dash.changeArray(style);
	}
	
	public void setRound()
	{
		this.rend.stroke = this.rend.stroke.changeJoin(LineJoin.ROUND);
	}
	
	public void setEndRound()
	{
		this.rend.stroke = this.rend.stroke.changeEnd(LineCap.ROUND);
	}
	
	public void setTextColor(String color)
	{
		this.rend.text = this.rend.text.changeColor(color);
	}
	
	public void setTextAngle(double a)
	{
		this.rend.text = this.rend.text.changeTransform(new TextOrientation(a));
	}
	
	public GraphRender getGraphRender()
	{
		return this.rend;
	}
	
	public void setGraphRender(GraphRender render)
	{
		this.rend = render;
	}	
}
