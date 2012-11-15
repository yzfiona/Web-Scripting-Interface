package components

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.svggen.SVGGraphics2D;
import org.vaadin.svg.SvgComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

class Jung extends Graphics{
    private var model: Graph[String, String] = new DirectedSparseMultigraph[String, String]();
  
	override def createGraph(): SvgComponent = {
	    var svgComponent: SvgComponent = null;

		try {
		    var docBuilderFactory: DocumentBuilderFactory  = DocumentBuilderFactory.newInstance();
		    var docBuilder: DocumentBuilder = docBuilderFactory.newDocumentBuilder();
		    var document: Document = docBuilder.newDocument();
		    var svgelem: Element = document.createElement("svg");
		    document.appendChild(svgelem);

		    var graphic2d: SVGGraphics2D = new SVGGraphics2D(document);

		    var server: VisualizationImageServer[String, String] = createServer(model);

		    server.printAll(graphic2d);

		    var el: Element = graphic2d.getRoot();
		    el.setAttributeNS(null, "viewBox", "0 0 350 350");
		    el.setAttributeNS(null, "style", "width:100%;height:100%;");

		    var bout: ByteArrayOutputStream = new ByteArrayOutputStream();

		    var out: Writer = new OutputStreamWriter(bout, "UTF-8");
		    graphic2d.stream(el, out);
		    
		    svgComponent = new SvgComponent();
			svgComponent.setWidth("100%");
			svgComponent.setHeight(350, 0);
			svgComponent.setSvg(new String(bout.toByteArray()));

		} catch {
		  case e: UnsupportedEncodingException => e.printStackTrace();
		  case e: ParserConfigurationException => e.printStackTrace();
		  case e: IOException => e.printStackTrace();
		} 
		return svgComponent;
	}
	
	private def createServer(aGraph: Graph[String, String]): VisualizationImageServer[String, String] = {
		var layout: Layout[String, String] = new FRLayout[String, String](aGraph);

		layout.setSize(new Dimension(300, 300));
		var vv: VisualizationImageServer[String, String] = new VisualizationImageServer[String, String](layout, new Dimension(350, 350));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller[String]());
		return vv;
		}
	
	override def addNode(name: String): Unit = {
	  model.addVertex(name);
	}
	
	override def addEdge(begin: String, end: String, name: String): Unit = {
	  model.addEdge(name, begin, end, EdgeType.DIRECTED);
	}
}