package components

trait Graphics {
	def createGraph(): Any
	def addNode(name: String): Unit
	def addEdge(begin: String, end: String, name: String): Unit
}