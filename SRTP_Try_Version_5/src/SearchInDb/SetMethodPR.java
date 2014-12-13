package SearchInDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;



public class SetMethodPR {
	public static int nodeNumber;
	public static double d=0.85;
	public static double boundary=0.00000003;
	public static List<Long> nodeIdList=new ArrayList<Long>();
	public void setMethodNodeList(){
			SearchInDb.startDb();
			
			Label label = DynamicLabel.label( "method" );
			try ( Transaction tx = SearchInDb.graphDb.beginTx())
			{
				for ( Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(label, "type","method" ))
				{
				nodeIdList.add(node.getId());
				tx.success();
				}
			
			}
	}
	public Map getGraphMap(List nodeIdList){
		Map graphMap=new HashMap<Long,List<ValueInMap>>();
		SearchMethod searchMethod=new SearchMethod();
		ArrayList<Node> resultNodes;
		try ( Transaction tx = SearchInDb.graphDb.beginTx())
		{
			for(int i=0;i<nodeIdList.size();i++){
				Node node=SearchInDb.graphDb.getNodeById((long) nodeIdList.get(i));
				resultNodes=new ArrayList<Node>();
				resultNodes=searchMethod.searchInvokeMethod(node);
				for(int j=0;j<resultNodes.size();j++){
					int index=nodeIdList.indexOf(resultNodes.get(j).getId());
					List<ValueInMap> getList=new ArrayList<ValueInMap>();
					ValueInMap valueInMap = new ValueInMap(i+1,(double)1/resultNodes.size());
					if(graphMap.get(index+1)!=null){
						getList=(List)graphMap.get(index+1);
						getList.add(valueInMap);
						}
					else{
						getList.add(valueInMap);
					}
					graphMap.put(index+1, getList);
					
				}
			}
			tx.success();
		}
		return graphMap;
		
	}
	public void setPK(){
		ValueInMap valueInMap;
		setMethodNodeList();
		Map<Long,List<ValueInMap>> graphMap=getGraphMap(nodeIdList);
		final int nodeNumber=nodeIdList.size();
		double p1[]=new double[nodeNumber+1];
		double p2[]=new double[nodeNumber+1];
		for(int i=1;i<=nodeNumber;i++){
			p2[i]=(double)1/nodeNumber;
		}
		for(int i=1;i<=nodeNumber;i++){
			p1[i]=0;
		}
		while(check(p1,p2)){
			for(int i=1;i<=nodeNumber;i++){
				p1[i]=p2[i];
			}
			for(int i=1;i<=nodeNumber;i++){
				p2[i]=0;
				if(graphMap.get(i)!=null){
					List getList=(List)graphMap.get(i);
					for(int j=0;j<getList.size();j++){
						valueInMap=(ValueInMap)getList.get(j);
						p2[i]+=p1[valueInMap.column]*(valueInMap.value);					
					}
				}
				
				p2[i]=d*p2[i]+(1-d)/nodeNumber;
			}
		}
		for(int i=1 ;i<=nodeNumber;i++){
			System.out.println(p2[i]);
		}
		setPRInDb(p2, nodeIdList);
		
	}
	public static boolean check(double[] p1,double []p2){
		boolean flag=false;
		final int nodeNumber=nodeIdList.size();
		for(int i=1;i<=nodeNumber;i++){
			if(((p1[i]-p2[i])>boundary)||((p1[i]-p2[i])<(0-boundary))){
				flag=true;
			}
		}
		return flag;
		
	}
	public void setPRInDb(double[] p2,List<Long> nodeIdList){
		try ( Transaction tx = SearchInDb.graphDb.beginTx())
		{
			for(int i=0;i<nodeIdList.size();i++){
				Node node=SearchInDb.graphDb.getNodeById((long) nodeIdList.get(i));
				node.setProperty("pageRank", p2[i+1]);
			}
			tx.success();
		}
	}
	public static void main(String argv[]){
		SetMethodPR setMethodPR=new SetMethodPR();
		setMethodPR.setPK();
	}



}
