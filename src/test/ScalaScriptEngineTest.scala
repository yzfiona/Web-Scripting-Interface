package test

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.junit._
import Assert._
import scala_scripting.ScalaScriptEngineFactory
import scala.tools.nsc._
import javax.script.ScriptEngineFactory

class ScalaScriptEngineTest {
  
	private var manager = new ScriptEngineManager()
  
    def loadEngine() : ScriptEngine = {
    	var scriptEngine : ScriptEngine = manager.getEngineByName("Scala");
		assertNotNull("Could not load script engine", scriptEngine);

		return scriptEngine;
    }	


    @Before def initialize() {
     manager.registerEngineName("Scala", new ScalaScriptEngineFactory(""))
    }

    @Test def testLoadEngine() {
    	loadEngine();
    }
    
    @Test def testSimpleCode() {
      var scriptEngine : ScriptEngine = loadEngine();
      	val code = "println(\"hello\")";
        val ir = scriptEngine.eval(code);
        assertEquals(InterpreterResults.Success, ir)
   }
    
    
   @Test def testCompleteJungCode() {
      var scriptEngine : ScriptEngine = loadEngine();
      val code = "import com.vaadin.ui.components._\n" +
    		     "var jung = new Jung();\n" +
        		 "jung.addNode(\"NodeA\");\n" +
        		 "jung.addNode(\"NodeB\");\n" +
        		 "jung.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" +
        		 "var graph = jung.createGraph();\n"
        val ir = scriptEngine.eval(code);
        assertEquals(InterpreterResults.Success, ir)
   }
   
    @Test def testCompleteNGCode() {
      var scriptEngine : ScriptEngine = loadEngine();
      val code = "import com.vaadin.ui.components._\n" +
    		     "var graph = new NodeGraphics();\n" +
        		 "graph.addNode(\"NodeA\");\n" +
        		 "graph.addNode(\"NodeB\");\n" +
        		 "graph.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" +
        		 "println(\"\"+graph.getNodeNumber());\n"
        val ir = scriptEngine.eval(code);
        assertEquals(InterpreterResults.Success, ir)
   }

}