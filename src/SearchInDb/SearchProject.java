package SearchInDb;

import java.util.ArrayList;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class SearchProject extends SearchInDb {
	public ArrayList<Node> searchByName(String projectName) {
		Label label = DynamicLabel.label("project");
		ArrayList<Node> resultNodes = new ArrayList<Node>();
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : graphDb.findNodesByLabelAndProperty(label,
					"projectName", projectName)) {
				resultNodes.add(node);
				tx.success();
			}

		}
		return resultNodes;

	}

}
