package NodeHandle;



import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import NodeHandle.MyNode.RelTypes;






public class ClassNode extends MyNode {
	public ClassNode()
	{
		super();
	
		
	}

	public Node createNode(String className,String packageName,String type ) {
	
	
		try ( Transaction tx = graphDb.beginTx() )
		{
		Label label = DynamicLabel.label( "class" );
		// Create some nodes
		thisNode = graphDb.createNode( label );
		thisNode.setProperty( "className", className );
		thisNode.setProperty( "packageName", packageName );
		thisNode.setProperty( "classType", type );
		thisNode.setProperty("parseState",false);
		thisNode.setProperty( "type", "class" );
		
		System.out.println( "classNode created" );
		tx.success();
		}
		return thisNode;
		
	}
	//��ʹ���ཨ��ͬʹ���ߵĹ�ϵ
	public void setUseRelToClass(ClassNode user ){
		boolean flag = false;
		try ( Transaction tx = graphDb.beginTx() )
		{
			for(Relationship iter : this.thisNode.getRelationships(RelTypes.USE, Direction.INCOMING)){
				if(iter.getStartNode().equals(user.thisNode))
					flag = true;
			}
			if(flag==false){
				 user.thisNode.createRelationshipTo( this.thisNode, RelTypes.USE );
			}
		tx.success();
		}
		
		
	}
	//�ӿڽ���ͬʵ���ߵĹ�ϵ
	public void setImplementRelToClass(ClassNode implementer){
		boolean flag = false;
		try ( Transaction tx = graphDb.beginTx() )
		{
			for(Relationship iter : this.thisNode.getRelationships(RelTypes.IMPLEMENT, Direction.INCOMING)){
				if(iter.getStartNode().equals(implementer.thisNode))
					flag = true;
			}
			if(flag==false){
				 implementer.thisNode.createRelationshipTo( this.thisNode, RelTypes.IMPLEMENT );
			}
		tx.success();
		}
		
	}
	//�����ཨ��ͬ�����ߵ���ϵ
	public void setExtendRelToClass(ClassNode extender){
		Relationship  relationship;
		try ( Transaction tx = graphDb.beginTx() )
		{
			if((extender.thisNode.getSingleRelationship( RelTypes.EXTEND,Direction.OUTGOING))==null){
				relationship = extender.thisNode.createRelationshipTo( this.thisNode, RelTypes.EXTEND );
			}
		tx.success();
		}
		
	}
	//����ͬ���̽ڵ����ϵ
	public void setContainRelToClass(ProjectNode container){
		try ( Transaction tx = graphDb.beginTx() )
		{
			if(this.thisNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING)==null){
				container.thisNode.createRelationshipTo( this.thisNode, RelTypes.CONTAIN );
			}
		tx.success();
		
		}
		
	}
	//waiting to be modified
	public boolean checkNodeExist(String className,String packageName,String type){
		Label label = DynamicLabel.label( "class" );
		try ( Transaction tx = graphDb.beginTx() )
		{
			for ( Node node : graphDb.findNodesByLabelAndProperty( label, "className", className ) )
			{
				if(node!=null&&node.getProperty("packageName").equals(packageName)&&node.getProperty("type").equals(type)){
					this.thisNode=node;
					return true;
				}
			}
			tx.success();		
		}
		return false;
	}
	public boolean checkSameNode(ClassNode classNode){
		if(thisNode.getProperty("className").equals(classNode.thisNode.getProperty("className"))){
			if(thisNode.getProperty("packageName").equals(classNode.thisNode.getProperty("packageName"))){
				if(thisNode.getProperty("type").equals(classNode.thisNode.getProperty("type"))){
					return true;
				}
			}
		}
		return false;
	}
	public void  setParseState(){
		try (Transaction tx = graphDb.beginTx()){
			thisNode.setProperty("parseState", true);
			tx.success();
		}
	}
	
	public boolean getParseState(){
		try ( Transaction tx = graphDb.beginTx() )
		{
//			if(this.thisNode.getSingleRelationship( RelTypes.CONTAIN, Direction.INCOMING).getEndNode() != null){
//				tx.success();
//				return true;
//			}
//			return false;
//			
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
