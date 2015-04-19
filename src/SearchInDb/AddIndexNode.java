package SearchInDb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import SearchInDb.SearchInDb.RelTypes;

public class AddIndexNode {
	public static Long addStartNode(){
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Node startNode=SearchInDb.graphDb.createNode();
			startNode.setProperty("start", 0);
			tx.success();
			return startNode.getId();
		}
		
	}
	public static void addAbcNode(Long startNodeId){
		IndexDefinition indexDefinition;
		Label label = DynamicLabel.label("abcMethodIndex");
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Schema schema = SearchInDb.graphDb.schema();
			indexDefinition = schema.indexFor(DynamicLabel.label("abcMethodIndex"))
					.on("methodLetter").create();
			tx.success();
		}
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Schema schema = SearchInDb.graphDb.schema();
			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
		}
		Label labelClass = DynamicLabel.label("abcClassIndex");
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Schema schema = SearchInDb.graphDb.schema();
			indexDefinition = schema.indexFor(DynamicLabel.label("abcClassIndex"))
					.on("classLetter").create();
			tx.success();
		}
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Schema schema = SearchInDb.graphDb.schema();
			schema.awaitIndexOnline(indexDefinition, 10, TimeUnit.SECONDS);
		}
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			for(int i=0;i<26;i++){
			Node newNode=SearchInDb.graphDb.createNode();;
			int asc2=97+i;
			char temp=(char) asc2;
			newNode.addLabel(label);
			newNode.setProperty("methodLetter",temp);
			SearchInDb.graphDb.getNodeById(startNodeId).createRelationshipTo(newNode,RelTypes.POINT);
			}
			tx.success();
		}
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			for(int i=0;i<26;i++){
			Node newNode=SearchInDb.graphDb.createNode();;
			int asc2=97+i;
			char temp=(char) asc2;
			newNode.addLabel(labelClass);
			newNode.setProperty("classLetter",temp);
			SearchInDb.graphDb.getNodeById(startNodeId).createRelationshipTo(newNode,RelTypes.POINT);
			}
			tx.success();
		}
		
	}
	public static void main(String[] argv){
		//SearchInDb.deleteFileOrDirectory(new File(SearchInDb.DB_PATH));
//		long start=addStartNode();
//		addAbcNode(start);
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Label label = DynamicLabel.label("abcMethodIndex");
			for(Node startNode:SearchInDb.graphDb.findNodesByLabelAndProperty(label,"methodLetter",'a')){
				System.out.println(startNode.getProperty("methodLetter").equals('a'));
				System.out.println(startNode.getId());

			}
		}
		HashSet<String> methodSet=new HashSet<String>();
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Label label = DynamicLabel.label("method");
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				String methodName=(String)node.getProperty("methodName");
				methodSet.add(methodName);
			}
			tx.success();
		}
		try (Transaction tx = SearchInDb.graphDb.beginTx()) {
			Label label = DynamicLabel.label("methodBNameIndex");
			Label labelMethod = DynamicLabel.label("abcMethodIndex");
			for(String methodName:methodSet)
			{
				Node newNode=SearchInDb.graphDb.createNode();
				newNode.addLabel(label);
				newNode.setProperty("uniqueName",methodName);
				char sign=methodName.charAt(0);
				ArrayList<Node> startNodes=new ArrayList<Node>();
				for(Node node:SearchInDb.graphDb.findNodesByLabelAndProperty(label,"methodLetter",sign)){
					startNodes.add(node);
				}
				Node startNode=startNodes.get(0);
				startNode.createRelationshipTo(newNode,RelTypes.POINT);
			}			
			tx.success();
		
		}
		
		

	}

}
