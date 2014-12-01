package SearchInDb;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

public abstract class SearchInDb {

	public static String DB_PATH="var/graphDb";
	public static GraphDatabaseService graphDb;
	protected static IndexDefinition indexDefinition;
	public Node thisNode;

	
	
	public SearchInDb(){
		
		}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	// Registers a shutdown hook for the Neo4j instance so that it
	// shuts down nicely when the VM exits (even if you "Ctrl-C" the
	// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}
	public static enum RelTypes implements RelationshipType
	{
		 USE,
		 INVOKE,
		 IMPLEMENT,
		 EXTEND,
		 CONTAIN
		
	}
	public static void startDb(){
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
	}
	public static void prepare(){

		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		indexDefinition = schema.indexFor( DynamicLabel.label( "class" ) )
		.on( "className" )
		.create();
		tx.success();
		}
		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
		}
		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		indexDefinition = schema.indexFor( DynamicLabel.label( "method" ) )
		.on( "methodName" )
		.create();
		tx.success();
		}
		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
		}
		
		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		indexDefinition = schema.indexFor( DynamicLabel.label( "project" ) )
		.on( "projectName" )
		.create();
		tx.success();
		}
		try ( Transaction tx = graphDb.beginTx() )
		{
		Schema schema = graphDb.schema();
		schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
		}
		
	}
	
    public static void deleteFileOrDirectory( File file ) 
     { 
         if ( file.exists() ) 
         { 
             if ( file.isDirectory() ) 
             { 
                 for ( File child : file.listFiles() ) 
                { 
                     deleteFileOrDirectory( child ); 
                 } 
            } 
               file.delete(); 
         } 
     } 
}
