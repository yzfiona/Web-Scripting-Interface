package Application;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.gwt.ace.AceMode;

import scala_scripting.ScalaScriptEngineFactory;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Web_Scripting_Interface extends Application {
	private MenuBar menubar = new MenuBar();
	private Window mainWindow = new Window("Component_list Application");
	private VerticalLayout panelvl, curContainer;
	private ScriptEngineManager manager = new ScriptEngineManager();
	private ScriptEngine engine = manager.getEngineByName("JavaScript");
	private static final String[] languages = new String[] { "JavaScript", "Scala"};
	private enum language {ECMAScript, Scala}
	private String currLanguage = "JavaScript";
	private ThemeResource expandIcon_l1 = new ThemeResource("icon/expand_l1.png");
	private ThemeResource collapseIcon_l1 = new ThemeResource("icon/collapse_l1.png");
	private ThemeResource expandIcon_l2 = new ThemeResource("icon/expand_l2.png");
	private ThemeResource collapseIcon_l2 = new ThemeResource("icon/collapse_l2.png");
	   
	@Override
	public void init() {
	    manager.registerEngineName("Scala", new ScalaScriptEngineFactory(this.getContext().getBaseDirectory().getAbsolutePath()));
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
        
	    Panel panel = new Panel();
	    panel.setScrollable(true);
	    panel.setSizeFull();
        //panel.setHeight("450px");  
	    panelvl = (VerticalLayout) panel.getContent();
	    panelvl.setMargin(true); 
	    panelvl.setSpacing(true); 
	    
	    createLayer();
	    createCodeEditor();
        
		layout.addComponent(panel);
        
		mainWindow.setContent(layout);
		mainWindow.setScrollable(true);
		setMainWindow(mainWindow);
		setTheme("ExpandCollapse");
	}
	
	private void createLayer() {
		VerticalLayout container = new VerticalLayout();
	    container.setSizeFull();
	    panelvl.addComponent(container);
	    
	    final Button expand = new Button();
		expand.setIcon(expandIcon_l2);
		expand.setStyle(Button.STYLE_LINK);
		container.addComponent(expand);		
		container.setComponentAlignment(expand, Alignment.TOP_RIGHT);
		
		final VerticalLayout compContainer = new VerticalLayout();
	    compContainer.setSizeFull();
	    container.addComponent(compContainer);
	    container.setExpandRatio(compContainer, 1);
	    
		expand.addListener(new Button.ClickListener() {
			// @Override 
			public void buttonClick(Button.ClickEvent event) {
				if (compContainer.isVisible()) {
					compContainer.setVisible(false);
					expand.setIcon(collapseIcon_l2);
				}
				else {
					compContainer.setVisible(true);
					expand.setIcon(expandIcon_l2);
										
				}
			}
		});
		curContainer = compContainer;
		
	}
	
	private void createCodeEditor() {
		final GridLayout grid = new GridLayout(2, 2);
		grid.setSizeFull();
		curContainer.addComponent(grid);
		//TODO replace button with pic
		final Button expand = new Button();
		expand.setIcon(expandIcon_l1);
		expand.setStyle(Button.STYLE_LINK);
		grid.addComponent(expand, 1, 0);
		grid.setComponentAlignment(expand, Alignment.TOP_RIGHT);
		
		VerticalLayout embedded = new VerticalLayout();
		//TOFIX functions not yet
		embedded.addStyleName("embedded");
		grid.addComponent(embedded, 1, 1);
		
		
		final VerticalLayout unit = new VerticalLayout();
		unit.setSizeFull();
		grid.addComponent(unit, 0, 0, 0, 1);
		grid.setColumnExpandRatio(0, 1);
		
		expand.addListener(new Button.ClickListener() {
			// @Override 
			public void buttonClick(Button.ClickEvent event) {
				if (unit.isVisible()) {
					unit.setVisible(false);
					expand.setIcon(collapseIcon_l1);
				}
				else {
					unit.setVisible(true);
					expand.setIcon(expandIcon_l1);
										
				}
			}
		});


		HorizontalLayout input = new HorizontalLayout();
		input.setWidth("100%");
		input.setHeight("190px");
		final AceEditor editor = new AceEditor();
        editor.setValue("var jung = new Jung();\n" +
        			    "jung.addNode(\"NodeA\");\n" +
        			    "jung.addNode(\"NodeB\");\n" +
        			    "jung.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" +
        			    "var graph = jung.createGraph();\n" +
        			    "visualization(graph);\n");
        editor.setMode(AceMode.javascript);
        editor.setSizeFull();
        input.addComponent(editor);
        input.setExpandRatio(editor, 1);
              
   /*     HorizontalLayout hl = new HorizontalLayout();
		final TextArea ta = new TextArea(null, "");
		ta.setWidth("100%");
		
		System.setOut(new GUIPrintStream(System.out, ta));
		
		final Refresher refresher = new Refresher();	
		left.addComponent(refresher);*/
		/*Button get = new Button("OK");		
		input.addComponent(get);
		get.addListener(new Button.ClickListener() {
			// @Override 
			public void buttonClick(Button.ClickEvent event) {
				visualization(editor, output, container);  
				  
			}
		});*/
		unit.addComponent(input);
		
		final VerticalLayout output = new VerticalLayout();
		output.setWidth("100%");
		
				
		editor.addShortcutListener(new ShortcutListener("Execution", ShortcutAction.KeyCode.ENTER, new int[]{ShortcutAction.ModifierKey.CTRL}) {
			@Override
			public void handleAction(Object sender, Object target) {
				// TODO Auto-generated method stub
				visualization(editor, output);
				editor.removeShortcutListener(this);
			}
			
		});
		
		editor.addShortcutListener(new ShortcutListener("New Layer", ShortcutAction.KeyCode.ENTER, new int[]{ShortcutAction.ModifierKey.CTRL, ShortcutAction.ModifierKey.SHIFT}) {
			@Override
			public void handleAction(Object sender, Object target) {
				// TODO Auto-generated method stub
				createLayer();
				visualization(editor, output);
				editor.removeShortcutListener(this);
			}
			
		});
		
		
		HorizontalLayout language = new HorizontalLayout();
		Label lable = new Label("Please select a language:");
		language.addComponent(lable);
		
		final NativeSelect list = new NativeSelect();
        for (int i = 0; i < languages.length; i++) {
        	list.addItem(languages[i]);
        }

        list.setNullSelectionAllowed(false);
        list.setValue(currLanguage);
        list.setImmediate(true);
        list.addListener(new Property.ValueChangeListener(){

			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				System.out.println("current language is " + list.getValue());
				currLanguage = list.getValue().toString();
				ScriptContext context = engine.getContext();
				context.removeAttribute("output", ScriptContext.ENGINE_SCOPE);
				//Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);			
				engine = manager.getEngineByName(list.getValue().toString());
				//engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
				engine.setContext(context);
				initializeEngine(output);
			}
        	
        });
        language.addComponent(list);
        language.setExpandRatio(list, 1);
        unit.addComponent(language);

        unit.addComponent(output);
        unit.setExpandRatio(output, 1);
        initializeEngine(output);
		
	}
	
	private void initializeEngine(VerticalLayout output) {
		  String prefix = null;
		  engine.put("output", output);
		  try {	
			switch (language.valueOf(engine.getFactory().getLanguageName())) {
			case ECMAScript: prefix = "importPackage(Packages.components);"+
		    		  				  "importPackage(com.vaadin.ui);"+
		    						  "importPackage(com.pantar.widget.graph.server);" +
		    						  "importPackage(com.hp.hpl.jena.rdf.model);" +
		    						  "importPackage(com.hp.hpl.jena.util);" +
		    						  "importPackage(java.io);" +
		    						  "importPackage(com.hp.hpl.jena.vocabulary);" +
		    						  "function visualization(graph) {" +
		    						  "if (graph instanceof GraphComponent) {" +
		    						  "output.setHeight('200px');" +
		    						  "}" +
		    						  "output.addComponent(graph);" +
		    						  "}";
								break;
			 case Scala: 		prefix = "import components._;"+
	  				  					 "import com.vaadin.ui._;"+
	  				  					 "import com.pantar.widget.graph.server._;" +
	  				  					 "import com.hp.hpl.jena.rdf.model._;" +
	  				  					 "import com.hp.hpl.jena.util._;" +
	  				  					 "import java.io._;" +
	  				  					 "import com.hp.hpl.jena.vocabulary._;\n"+
	  				  					// "println(output)\n"+
			    						  "def visualization(graph: Component):Unit = {" +
			    						  "if (graph.isInstanceOf[GraphComponent]) {" +
			    						  "output.setHeight(\"200px\");" +
			    						  "}\n" +
			    						  "output.addComponent(graph);" +
			    						  "}\n";
								break;
			}
			engine.eval(prefix, engine.getContext());
		  } catch (ScriptException ex) {
		      ex.printStackTrace();
		  } 
	}
	
	private void visualization(AceEditor editor, VerticalLayout output) {
		 try{			  
		    engine.eval(editor.getValue().toString());	
		    editor.setReadOnly(true);
		  } catch (ScriptException ex) {
		      ex.printStackTrace();
		  }   
		  createCodeEditor();
	}
	
    

}
