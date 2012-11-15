package test

import org.junit._
import Assert._
import java.io.StringWriter
import java.io.PrintWriter
import scala.tools.nsc._
import components._

class ScalaInterpreterTest {
  private  val writer = new StringWriter()
  private  val env = new Settings()
  private  val interpreter = new Interpreter(env, new PrintWriter(writer))
  
  @Before def initialize() {
     env.usejavacp.value = true
  }

  @Test def testPrimaryType() {
     val ir = interpreter.bind("say", "String", "hello")
     assertEquals(InterpreterResults.Success, ir)
  }
  
   @Test def testPrint() {
     val ir = interpreter.interpret("println(\"hello\")")
     assertEquals(InterpreterResults.Success, ir)
  }
  
  @Test def testClassDefinition() {
     var classDef = new StringBuilder();
     classDef.append("class TestInject(sayWhat: String) {");
     classDef.append("\n");
     classDef.append("def saySomething() = sayWhat;");
     classDef.append("}\n");
     interpreter.interpret(classDef.toString())
     val ir = interpreter.interpret("val obj = new TestInject(\"hello\")");
     assertEquals(InterpreterResults.Success, ir)
  }
  
     
   @Test def testImport() {
     val ir = interpreter.interpret("import components._")
     assertEquals(InterpreterResults.Success, ir)
  }
   
   @Test def testComplexType() {
     val graph = new Jung()
     interpreter.interpret("importPackage(Packages.components)");
     val ir = interpreter.bind("graph", graph.getClass().getCanonicalName(),graph);
     assertEquals(InterpreterResults.Success, ir)
   }
   
   @Test def testNewInstance() {
     interpreter.interpret("import components._");
     val ir = interpreter.interpret("val graph = new Jung()");
     interpreter.setContextClassLoader();
     assertEquals(InterpreterResults.Success, ir)
   }
   
    @Test def testCompleteJungCode() {
      val code = "var jung = new Jung();\n" +
        		 "jung.addNode(\"NodeA\");\n" +
        		 "jung.addNode(\"NodeB\");\n" +
        		 "jung.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" +
        		 "var graph = jung.createGraph();\n"
     interpreter.interpret("import components._");
     val ir = interpreter.interpret(code);
     interpreter.setContextClassLoader();
     assertEquals(InterpreterResults.Success, ir)
   }
    
    @Test def testCompleteNGCode() {
      val code = "var graph = new NodeGraphics();\n" +
        		 "graph.addNode(\"NodeA\");\n" +
        		 "graph.addNode(\"NodeB\");\n" +
        		 "graph.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" //+
        		 "println(graph.getNodeNumber())\n"
     interpreter.interpret("import components._");
     val ir = interpreter.interpret(code);
     interpreter.setContextClassLoader();
     assertEquals(InterpreterResults.Success, ir)
   }
    
    @Test def testConstructor() {
      val clazz = interpreter.classLoader.loadClass("components.NodeGraphics")
      val graph = clazz.getConstructor().newInstance().asInstanceOf[NodeGraphics]
      interpreter.bind("graph", graph.getClass().getCanonicalName(),graph)
      val code = "graph.addNode(\"NodeA\");\n" +
        		 "graph.addNode(\"NodeB\");\n" +
        		 "graph.addEdge(\"NodeA\", \"NodeB\", \"edge1\");\n" +
        		 "println(graph.getNodeNumber())\n"
      val ir = interpreter.interpret(code);
      assertEquals(InterpreterResults.Success, ir)
    }
  
   
}