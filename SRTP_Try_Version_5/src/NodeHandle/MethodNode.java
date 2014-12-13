package NodeHandle;



import java.util.Arrays;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;



public class MethodNode extends MyNode {
	public MethodNode()
	{
		super();
	}

	public Node createNode(String methodName,String[] param ) {
	
	
		try ( Transaction tx = graphDb.beginTx() )
		{
		Label label = DynamicLabel.label( "method" );
		// Create some nodes
		thisNode = graphDb.createNode( label );
		thisNode.setProperty( "methodName", methodName );
		thisNode.setProperty("parseState",false);
		thisNode.setProperty("param", param);
		thisNode.setProperty("type", "method");
		System.out.println( "MethodNode-name:" + methodName + " created" );
		tx.success();
		
		}
		return thisNode;
	}
	
	//被调用函数建立同调用者的关系
	public void setInvokeRelToMethod(MethodNode invoker ){
		boolean flag = false;
		try ( Transaction tx = graphDb.beginTx() )
		{
			for(Relationship iter : this.thisNode.getRelationships(RelTypes.INVOKE, Direction.INCOMING)){
				if(iter.getStartNode().equals(invoker.thisNode))
					flag = true;
//				System.out.println(invoker.thisNode.getProperty("methodName"));
//				System.out.println(iter.getStartNode().getProperty("methodName"));
			}

			if(flag==false)
				invoker.thisNode.createRelationshipTo( this.thisNode, RelTypes.INVOKE );
			tx.success();
		}
		//return relationship;
	}
	//被包含函数建立同包含类的关系
	public void setContainRelToMethod(ClassNode container){
		
		Relationship relationship ;
		try ( Transaction tx = graphDb.beginTx() )
		{
			if(this.thisNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING)==null){
				relationship = container.thisNode.createRelationshipTo( this.thisNode, RelTypes.CONTAIN );
			}
			tx.success();
		}
		
	}
	//waiting to be modified
	public boolean checkNodeExist(String methodName,String [] param,ClassNode classNode){
		Label label = DynamicLabel.label( "method" );
		ClassNode currentClassNode = new ClassNode();
		try ( Transaction tx = graphDb.beginTx() )
		{
		for ( Node node : graphDb.findNodesByLabelAndProperty( label, "methodName", methodName ) )
		{
			if((node!=null)&&(Arrays.equals((String [])node.getProperty("param"),param))){
				currentClassNode.thisNode=node.getSingleRelationship( RelTypes.CONTAIN, Direction.INCOMING ).getStartNode();
				if(currentClassNode.checkSameNode(classNode)){
					this.thisNode=node;
					return true;
				}

			}
		}
		tx.success();
		}
		return false;
	}
	
	public void  setParseState(){
		try (Transaction tx = graphDb.beginTx()){
			thisNode.setProperty("parseState", true);
			tx.success();
		}
	}
	//how to handle when one method has been programmed in several class
	public  boolean getParseState(){
		try ( Transaction tx = graphDb.beginTx()){
//		{
//			
//		if(this.thisNode.getSingleRelationship( RelTypes.CONTAIN, Direction.INCOMING ) != null){
//			if(this.thisNode.getSingleRelationship( RelTypes.CONTAIN, Direction.INCOMING ).getEndNode().getSingleRelationship( RelTypes.CONTAIN, Direction.INCOMING)!=null)
//			tx.success();
//			return true;
//		}
//		return false;
//		}
				tx.success();
				return (boolean) thisNode.getProperty("parseState");
				
	}
	}
	
	@Override
	public void createNode() {
		// TODO Auto-generated method stub
		
	}

}