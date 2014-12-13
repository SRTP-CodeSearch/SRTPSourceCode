package NodeHandle;

import java.io.File;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import NodeHandle.MyNode.RelTypes;
import org.neo4j.*;
public class TestMain {
	public static void main(String argv[]){
		//reset db
		//MyNode.deleteFileOrDirectory( new File( MyNode.DB_PATH ) );
		MyNode.startDb();
		//MyNode.prepare();
		
		MethodNode methNode1 = new MethodNode();	
		MethodNode methNode2 = new MethodNode();	
		ProjectNode proNode = new ProjectNode();
		ClassNode claNode = new ClassNode();
	
		claNode.createNode("Helloworld", "dongdongge", "class");
		proNode.createNode("Helloworld", "D:/helloworld");

		if(claNode.checkNodeExist("Helloworld", "dongdongge", "class")){
			if(proNode.checkNodeExist("Helloworld", "D:/helloworld")){
				claNode.setContainRelToClass(proNode);
			}
		}
		System.out.println(methNode2.getParseState());
		System.out.println(methNode2.thisNode.getId());
	
//		Label label = DynamicLabel.label( "method" );
//		System.out.println(methNode1.getParseState());
//		String nameToFind = "quicksort";
//		try ( Transaction tx = MyNode.graphDb.beginTx() )
//		{
//			try ( ResourceIterator<Node> users =
//					MyNode.graphDb.findNodesByLabelAndProperty( label, "methodName", nameToFind ).iterator() )
//					{
//						ArrayList<Node> userNodes = new ArrayList<>();
////				for ( Node node : MyNode.graphDb.findNodesByLabelAndProperty( label, "methodName", nameToFind ) )
////				{
////				node.delete();
////				}
////				tx.success();
//						while ( users.hasNext() )
//						{
//							userNodes.add( users.next() );
//						}
//						for ( Node node : userNodes )
//						{
//							System.out.println( "The name of  " + " is " + node.getProperty( "methodName" ) );
//						}
//					}
//		}
	}

}
