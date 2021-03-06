package SearchInDb;

import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

import SearchInDb.SearchInDb.RelTypes;

public class SearchClass extends SearchInDb {

	public static ArrayList<Node> searchByName(String className) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		Label label = DynamicLabel.label("class");
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : graphDb.findNodesByLabelAndProperty(label,
					"className", className)) {
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

	// find the classes which use this class
	public ArrayList<Node> searchUseClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		// wait to be check null or ""

		try {
			try (Transaction tx = graphDb.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// graphDb.traversalDescription().relationships(RelTypes.USE,
				// Direction.INCOMING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.startNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						RelTypes.USE, Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}

	// find the classes which is used by this class
	public ArrayList<Node> searchUserClass(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try {
			try (Transaction tx = graphDb.beginTx()) {
				classNode.getProperty("className");
				// TraversalDescription td=
				// graphDb.traversalDescription().relationships(RelTypes.INVOKE,
				// Direction.OUTGOING);
				// for(Path path: td.traverse(classNode)){
				// resultNodes.add(path.endNode());
				// }
				for (Relationship iter : classNode.getRelationships(
						RelTypes.USE, Direction.INCOMING)) {
					resultNodes.add(iter.getStartNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}

	public String getFileLocation(Node classNode) {
		try {
			try (Transaction tx = graphDb.beginTx()) {
				classNode.getProperty("className");
				Node proNode = classNode.getSingleRelationship(
						RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
				String packageName = (String) classNode
						.getProperty("packageName");
				packageName = packageName.replace('.', '\\');
				System.out.println(packageName);
				String fileLocation = (String) proNode
						.getProperty("fileLocation");
				String classLocation = fileLocation + '\\' + "src" + '\\'
						+ packageName + '\\'
						+ (String) classNode.getProperty("className") + ".java";
				return classLocation;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}

	// waiting to be complete to find the sourceCode
	public void getExtendSource(Node classNode) {
		String output = new String();
		try (Transaction tx = graphDb.beginTx()) {
			Traverser extendsTraverser = getExtendSourceTraverser(classNode);
			for (Path friendPath : extendsTraverser) {
				output += "At depth " + friendPath.length() + " => "
						+ friendPath.endNode().getProperty("className") + "\n";
			}
			System.out.println(output);
			tx.success();
		}
	}

	private Traverser getExtendSourceTraverser(final Node classNode) {
		TraversalDescription td = graphDb.traversalDescription().depthFirst()
				.relationships(RelTypes.EXTEND, Direction.OUTGOING);

		return td.traverse(classNode);

	}

	public ArrayList<Node> getImplementSourceTraverser(Node classNode) {
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try {
			try (Transaction tx = graphDb.beginTx()) {
				classNode.getProperty("className");
				TraversalDescription td = graphDb.traversalDescription()
						.relationships(RelTypes.IMPLEMENT, Direction.OUTGOING);
				for (Path path : td.traverse(classNode)) {
					resultNodes.add(path.endNode());
				}
				tx.success();
				return resultNodes;
			}
		} catch (NotFoundException e) {
			System.out.println("this is not a class");
			return null;
		}

	}
}
