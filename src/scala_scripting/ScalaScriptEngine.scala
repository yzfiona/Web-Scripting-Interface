package scala_scripting
import java.io.StringWriter
import scala.tools.nsc._
import java.io.PrintWriter
import javax.script._
import java.io.BufferedReader
import java.io.Reader
import scala.collection.JavaConversions._
import components.NodeGraphics
import scala.tools.nsc.util.SourceFile
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.File
import java.io.FilenameFilter

class ScalaScriptEngine extends AbstractScriptEngine{
  
  private  val writer = new StringWriter()
  private  val env = new Settings()
  private  var interpreter = new Interpreter(env, new PrintWriter(writer))
  private  var factory: ScriptEngineFactory = null
  private  var classPath: String = ""
  
  def this(factory: ScriptEngineFactory, classPath: String) = {
    this()
    this.factory = factory
    this.classPath = classPath match {case "" => ""
    								  case _ => classPath + "\\WEB-INF\\classes"}
    val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation.getPath()
    val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation.getPath()
     	
    var list = importLibs(classPath + "\\WEB-INF\\lib"):::List(compilerPath, libPath, this.classPath)

	env.bootclasspath.value = (env.bootclasspath.value::list)   mkString java.io.File.pathSeparator 
	println("settings.bootclasspath.value=" + env.bootclasspath.value);
	
	env.classpath.value = (env.classpath.value::list)   mkString java.io.File.pathSeparator 
	println("env.classpath.value = " + env.classpath.value);
	
    env.usejavacp.value = true
    println("initializing")
    
    
    //env.sourcepath.value = List(env.sourcepath.value, this.classPath) mkString java.io.File.pathSeparator 
    
  //  env.javabootclasspath.value = List(env.javabootclasspath.value, compilerPath, libPath) mkString java.io.File.pathSeparator 

    
  }
  
  override def put(name:String, value: Object): Unit = {
    interpreter.bind(name, value.getClass().getCanonicalName(), value)
  }
  
  
  @throws(classOf[ScriptException])
  override def eval(code: String): Object = {
     println("I'm using scala interpreter.")
     val ir = interpreter.interpret(code)
//var test = this.get("output")
     println(writer)
     
     return ir
  }
  
  def getFactory = factory
  
  def createBindings: Bindings =
    new SimpleBindings

  @throws(classOf[ScriptException])
  def eval(reader: Reader, context: ScriptContext) = {
    val bufferedReader = new BufferedReader(reader)
    val script = new StringBuilder
    var ch: Int = -1
    while ({ch = bufferedReader.read; ch != -1})
      script.append(ch.asInstanceOf[Char])
    eval(script.toString, context)
  }

  @throws(classOf[ScriptException])
  def eval(script: String, context: ScriptContext) = {
    bindContext(context)
  //  val result = Array("".asInstanceOf[Object])
  //  interpreter.bind("$JSR223result", "Array[Any]", result)
   /* val newScript: String = 
    	"var scriptResult: Any = \"\" ; " +
    	script +
    	" ; $JSR223result(0) = scriptResult"*/
    interpreter.interpret(script)
   /* if (interpreter.interpret(script) != InterpreterResults.Success)
      throw new ScriptException("something went wrong")*/
   // result(0).asInstanceOf[Object]                 
  }
  
  private def bindContext(context: ScriptContext) {
    bindScope(ScriptContext.GLOBAL_SCOPE, context)
    bindScope(ScriptContext.ENGINE_SCOPE, context)
  }
  
    private def bindScope(scope: Int, context: ScriptContext) {
    val bindings = context.getBindings(scope)
  

    if (!(bindings eq null)) {
      asMap(bindings) foreach { e =>
        val parts = e._1.split(":")
        parts.length match {
          case 1 => {
            val valValue = e._2
            val valName = parts(0).trim
            val valType = valValue.getClass.getName
	    val newType = 
	      basicTypesMapping.get(valType).getOrElse(valType)

            interpreter.bind(valName, newType, valValue)
          }
          case 2 => {
            val valValue = e._2
            val valName = parts(0).trim
            val valType = parts(1).trim
	    val realType = valValue.getClass.getName
	    val newType =
	      basicTypesMapping.get(realType).getOrElse(valType)

            interpreter.bind(valName, newType, valValue)
          }
          case _ => error("binding key has to be " +
                  "\"<variable name>:<variable type>\"" +
                  "or \"<variable name>\"")          
        }
      }
    }
  }    
    
  private def asMap[K, E](m: java.util.Map[K, E]) = new MapWrapper[K, E](m) { def underlying = m }

  private val basicTypesMapping = Map(
    "java.lang.Boolean" -> "Boolean",
    "boolean" -> "Boolean",
    "java.lang.Character" -> "Char",
    "char" -> "Char",
    "java.lang.Byte" -> "Byte",
    "byte" -> "Byte",
    "java.lang.Short" -> "Short",
    "short" -> "Short",
    "java.lang.Integer" -> "Int",
    "int" -> "Int",
    "java.lang.Long" -> "Long",
    "long" -> "Long",
    "java.lang.Float" -> "Float",
    "float" -> "Float",
    "java.lang.Double" -> "Double",
    "double" -> "Double"
    )
    
   private def importLibs(folderPath: String): List[String] = {
    var list = List[String]()
    if (folderPath != "") {
      val folder = new File(folderPath)
      val files = folder.listFiles(new FilenameFilter(){  
            def accept(f: File , name: String): Boolean = {  
                  
                return name.endsWith(".jar");
            }  
        });  
      list = (for (file <- files) yield {file.getPath()}).toList
    }
    return list
  }


}