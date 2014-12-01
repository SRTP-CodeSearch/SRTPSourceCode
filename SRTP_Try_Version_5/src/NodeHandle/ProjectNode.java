package NodeHandle;



import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;



public class ProjectNode extends MyNode {
	public ProjectNode()
	{
		super();
	
	}

	public 	Node createNode(String projectName,String fileLocation ) {
	
	
		try ( Transaction tx = graphDb.beginTx() )
		{
		Label label = DynamicLabel.label( "project" );
		// Create some nodes
	    thisNode = graphDb.createNode( label );
		thisNode.setProperty( "projectName", projectName );
		thisNode.setProperty( "fileLocation", fileLocation );
		thisNode.setProperty("parseState",false);
		thisNode.setProperty("type", "project");
		System.out.println( "projectNode created" );
		tx.success();
		}
		return thisNode;
		
	}
	public boolean checkNodeExist(String projectName,String fileLocation){
		Label label = DynamicLabel.label( "project" );
		try ( Transaction tx = graphDb.beginTx() )
		{
		for ( Node node : graphDb.findNodesByLabelAndProperty( label, "projectName", projectName ) )
		{
			if(node!=null&&node.getProperty("fileLocation")==fileLocation){
				this.thisNode=node;
				return true;
			}
		}
		tx.success();
		
		}
		return false;
	}
	
	public void  setParseState(){
		thisNode.setProperty("parseState", true);
	}
	
	public boolean getParseState(){
		
		return (boolean) thisNode.getProperty("parseState");
		
	}
	@Override
	public void createNode() {
		// TODO Auto-generated method stub
		
	}

}