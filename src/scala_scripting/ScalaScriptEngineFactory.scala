package scala_scripting


import java.util._
import javax.script._
import javax.script.ScriptEngine._
import scala.tools.nsc.Global


class ScalaScriptEngineFactory(classPath: String) extends ScriptEngineFactory{
  
  def getScriptEngine: ScriptEngine =  
    new ScalaScriptEngine(this, classPath)

  def getEngineName = "Scala Script Engine"
  
  def getEngineVersion = "0.1"
  
  def getExtensions = {
    val extensions = new ArrayList[String]
    extensions.add("scala")
    Collections.unmodifiableList[String](extensions)
  }
  
  def getLanguageName = "Scala"
 
  def getLanguageVersion = {
    val propertiesFname = "/compiler.properties"
    val propertyName = "version.number"
    val undefinedVersion = "undefined"
    
    try {
      classOf[Global].getResourceAsStream(propertiesFname) match {
        case null => undefinedVersion
        case stream => {
	  val props = new Properties()
	  props.load(stream)
	  props.getProperty(propertyName, undefinedVersion)
	}
      }
    }
    catch {
      case _ => undefinedVersion
    }   
  }
	  
  def getMethodCallSyntax(obj: String, m: String, args: String*) = obj + "." + m + "(" + args.mkString(", ") + ")"
  
  def getMimeTypes = {
    val mimeTypes = new ArrayList[String]
    mimeTypes.add("text/plain")
    Collections.unmodifiableList[String](mimeTypes)
  }
  
  def getNames = {
    val names = new ArrayList[String]
    names.add("Scala")
    names.add("scala")
    Collections.unmodifiableList[String](names)
  }
  
  def getOutputStatement(toDisplay: String) = "println(" + toDisplay + ")"
  
  def getParameter(key: String): Object = key match {
    case ENGINE => getEngineName
    case ENGINE_VERSION => getEngineVersion
    case NAME => getNames.get(0)
    case LANGUAGE => getLanguageName
    case LANGUAGE_VERSION => getLanguageVersion
    case _ => null
  }
  
  def getProgram(statements: String*) = "{\n" + statements.mkString("\n") + "}\n"
}