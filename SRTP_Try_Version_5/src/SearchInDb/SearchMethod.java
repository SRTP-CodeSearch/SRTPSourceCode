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
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

import NodeHandle.MyNode.RelTypes;


public class SearchMethod extends SearchInDb {

	public  ArrayList<Node>  searchByName(String methodName){
		Label label = DynamicLabel.label( "method" );
		ArrayList<Node> resultNodes=new ArrayList<Node>();
		try ( Transaction tx = graphDb.beginTx() )
		{
			for ( Node node : graphDb.findNodesByLabelAndProperty( label, "methodName", methodName ) )
			{
			resultNodes.add(node);
			tx.success();
			}
		
		}
		return resultNodes;
		
		}
	//find the methods which invoke this method
	public ArrayList<Node> searchInvokerMethod(Node methodNode){
		//wait to be check null or ""
	
		try{
			ArrayList<Node> resultNodes=new ArrayList<Node>();
			try ( Transaction tx = graphDb.beginTx() )
			{
//			TraversalDescription td= graphDb.traversalDescription().relationships(RelTypes.INVOKE, Direction.INCOMING);
//			for(Path path: td.traverse(methodNode)){
//				resultNodes.add(path.startNode());
//				}
				for(Relationship iter : methodNode.getRelationships(RelTypes.INVOKE, Direction.INCOMING)){
					resultNodes.add(iter.getStartNode());
				}
			tx.success();
			return resultNodes;
			}
		}
		catch(NotFoundException e){
			System.out.println("this is not a method");
			return null;
			}
		
	}
	//find the methods which is invoke by this method
	public ArrayList<Node> searchInvokeMethod(Node methodNode){
		try{
			ArrayList<Node> resultNodes=new ArrayList<Node>();
			try ( Transaction tx = graphDb.beginTx() )
			{
//			TraversalDescription td= graphDb.traversalDescription().breadthFirst()
//					.relationships(RelTypes.INVOKE, Direction.OUTGOING).evaluator(Evaluators.atDepth(1));
////			TraversalDescription td= graphDb.traversalDescription().relationships(RelTypes.INVOKE, Direction.OUTGOING);	
//			for(Path path: td.traverse(methodNode)){
//				resultNodes.add(path.endNode());
//				}
				for(Relationship iter : methodNode.getRelationships(RelTypes.INVOKE, Direction.OUTGOING)){
					resultNodes.add(iter.getEndNode());
				}
			tx.success();
			return resultNodes;
			}
		}
		catch(NotFoundException e){
			System.out.println("this is not a method");
			return null;
			}
	}
	public Node getContainClass(Node methodNode){
		try{
			try ( Transaction tx = graphDb.beginTx() )
			{
			Node classNode=methodNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
			return classNode;
			}
		}
		catch(NotFoundException e){
			System.out.println("this is not a method");
			return null;
			}
		
	}
	public String getFileLocation(Node methodNode){
		try{
			try ( Transaction tx = graphDb.beginTx() )
			{
			Node classNode=methodNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
			Node proNode = classNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING).getStartNode();
			String packageName=(String)classNode.getProperty("packageName");
			packageName=packageName.replace('.', '\\');
			String fileLocation=(String) proNode.getProperty("fileLocation");
			String classLocation=fileLocation+'\\'+"src"+'\\'+packageName+'\\'+(String)classNode.getProperty("className")+".java";	
			return classLocation;
			}
		}
		catch(NotFoundException e){
			System.out.println("this is not a method");
			return null;
			}
		
	}
}



