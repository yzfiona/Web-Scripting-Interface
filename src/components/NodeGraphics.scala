package components

import java.util.HashMap;

import com.pantar.widget.graph.server.DefaultNode;
import com.pantar.widget.graph.server.GraphComponent;
import com.pantar.widget.graph.server.GraphModel;
import com.pantar.widget.graph.server.Node;
import com.pantar.widget.graph.server.factories.GraphModelFactory;
import com.pantar.widget.graph.server.layout.GraphModelRandomLayout;
import com.pantar.widget.graph.server.styles.DefaultRelationStyle;
import com.vaadin.ui.CustomComponent;

class NodeGraphics (model: GraphModel, nodes: HashMap[String, Node])  extends CustomComponent with Graphics{
    //private var model: GraphModel = null;
	//private var nodes: HashMap[String, Node] = new HashMap[String, Node]();
	
	def this() = {
		this (model = GraphModelFactory.getGraphModelInstance(), nodes = new HashMap[String, Node]());
		model.setSingleSelectionSupport(true);
		// TODO add user code here
	}
	
	override def createGraph(): GraphComponent = {
	  model.setSingleSelectionSupport(true);
	  model.layout(new GraphModelRandomLayout());

	  var component: GraphComponent = new GraphComponent(model);
	  component.setHeight("150px");
		
	  return component;
	}
	
	override def addNode(name: String): Unit = {
	   var node: Node = new DefaultNode();
	   node.setLabel(name);
	   nodes.put(name, node);
	}
	
	override def addEdge(begin: String, end: String, name: String): Unit = {
	  model.connect(nodes.get(begin), nodes.get(end), new DefaultRelationStyle());
	}
	
	def  getNodeNumber(): Integer = return nodes.size

}