package SearchInDb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

public class SearchMethod extends SearchInDb {

	public ArrayList<Node> searchByName(String methodName) {
		Label label = DynamicLabel.label("method");
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : graphDb.findNodesByLabelAndProperty(label,
					"methodName", methodName)) {
				resultNodes.add(node);
				tx.success();
			}

		}
		return resultNodes;

	}

	// 验证函数是否被解析
	public boolean validateParseState(Node node) {
		try (Transaction tx = graphDb.beginTx()) {
			if (node.getProperty("parseState").equals(true)) {
				tx.success();
				return true;
			} else {
				tx.success();
				return false;
			}
		}

	}

	// find the methods which invoke this method
	public ArrayList<Node> searchInvokerMethod(Node methodNode) {
		// wait to be check null or ""

		try {
			ArrayList<Node> resultNodes = new ArrayList<Node>();
			try (Transaction tx = graphDb.beginTx()) {
				// TraversalDescription td=
				// graphDb.traversalDescription().relationships(RelTypes.INVOKE,
				// Direction.INCOMING);
				// for(Path path: td.traverse(methodNode)){
				// resultNodes.add(path.startNode());
				// }
				for (Relationship iter : methodNode.getRelationships(
						RelTypes.INVOKE, Direction.INCOMING)) {
					resultNodes.add(iter.getStartNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	// find the methods which is invoked by this method
	public ArrayList<Node> searchInvokeMethod(Node methodNode) {
		try {
			ArrayList<Node> resultNodes = new ArrayList<Node>();
			try (Transaction tx = graphDb.beginTx()) {
				// TraversalDescription td=
				// graphDb.traversalDescription().breadthFirst()
				// .relationships(RelTypes.INVOKE,
				// Direction.OUTGOING).evaluator(Evaluators.atDepth(1));
				// // TraversalDescription td=
				// graphDb.traversalDescription().relationships(RelTypes.INVOKE,
				// Direction.OUTGOING);
				// for(Path path: td.traverse(methodNode)){
				// resultNodes.add(path.endNode());
				// }
				for (Relationship iter : methodNode.getRelationships(
						RelTypes.INVOKE, Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}
	}

	public Node getContainClass(Node methodNode) {
		try {
			try (Transaction tx = graphDb.beginTx()) {
				Node classNode = methodNode.getSingleRelationship(
						RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
				return classNode;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	public String getFileLocation(Node methodNode) throws IOException {
		try {
			try (Transaction tx = graphDb.beginTx()) {
				Node classNode = methodNode.getSingleRelationship(
						RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
				Node proNode = classNode.getSingleRelationship(
						RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
				String packageName = (String) classNode
						.getProperty("packageName");
				packageName = packageName.replace('.', '\\');
				String fileLocation = (String) proNode
						.getProperty("fileLocation");
				String className = (String) classNode.getProperty("className");
				String classLocation = fileLocation + '\\' + packageName + '\\'
						+ className + ".java";
				try {
					new FileReader(classLocation);
				} catch (FileNotFoundException e) {
					String classLocationForSC = getFileLocationForSC(
							fileLocation, packageName, className, classLocation);
					System.out.println(classLocationForSC);
					return classLocationForSC;
				}
				tx.success();
				return classLocation;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a method");
			return null;
		}

	}

	public String getFileLocationForSC(String fileLocation, String packageName,
			String className, String classLocation) throws IOException {
		File Dir = new File(fileLocation + '\\' + packageName + '\\');
		String content;
		for (File iter : Dir.listFiles()) {
			StringBuilder fileData = new StringBuilder(1000);
			BufferedReader reader = new BufferedReader(new FileReader(
					iter.getCanonicalPath()));
			char[] buf = new char[10];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			content = fileData.toString();
			if (content.contains("class " + className)) {
				return iter.getCanonicalPath();
			}
		}
		return "Class not found";
	}
}
