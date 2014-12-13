package SearchInDb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TestMain {
	public static void main(String [] argv) throws IOException{
		SearchInDb.startDb();
//		SearchInDb.prepare();
		SearchClass sc= new SearchClass();
		SearchMethod sm=new SearchMethod();
		SearchProject sp=new SearchProject();
		ArrayList<Node> test=new ArrayList<Node>();
		test=sc.searchByName("ReadObjectState"); 
		System.out.println(test.size());
		for(int i=0;i<test.size();i++){
			System.out.println(test.get(i).getId());			
			if(sc.validateParseState(test.get(i))){
				String he=sc.getFileLocation(test.get(i));
				System.out.println(he);
				File f= new File(he);
				//System.out.println(readFileToString(sm.getFileLocation(test.get(1))));
			}
			else{
				System.out.println("this method hasn't been parsed");
				String he = null;
				try {
					he = sm.getFileLocation(test.get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(he);
				//File f= new File(he);
				//System.out.println(readFileToString(sm.getFileLocation(test.get(1))));
			}
		}

//		Label label = DynamicLabel.label( "method" );
//		try ( Transaction tx = SearchInDb.graphDb.beginTx())
//		{
//			for ( Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(label, "type","method" ))
//			{
//			System.out.println(node.getId());
//			tx.success();
//			}
//		
//		}

		
	}
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
