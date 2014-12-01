package TestPageRank;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SearchInDb.ValueInMap;

public class testPageRank {
	public static Map graphMap;
	public static final int nodeNumber=4;
	public static double d=0.85;
	public static double boundary=0.00000003;
	public static void main(String argv[]){
		double p1[]=new double[nodeNumber+1];
		double p2[]=new double[nodeNumber+1];
		for(int i=1;i<=nodeNumber;i++){
			p2[i]=(double)1/nodeNumber;
		}
		for(int i=1;i<=nodeNumber;i++){
			p1[i]=0;
		}
		graphMap=new HashMap<Integer,List<ValueInMap>>();
		
		ValueInMap valueInMap=new ValueInMap(2,(double)1/2);
		List<ValueInMap> valueList=new ArrayList<ValueInMap>();
		valueList.add(valueInMap);
		graphMap.put(1, valueList);
		
		valueList=new ArrayList<ValueInMap>();
		valueInMap=new ValueInMap(1,(double)1/3);
		valueList.add(valueInMap);
		valueInMap=new ValueInMap(4,(double)1/2);
		valueList.add(valueInMap);
		graphMap.put(2, valueList);
		
		valueList=new ArrayList<ValueInMap>();
		valueInMap=new ValueInMap(1,(double)1/3);
		valueList.add(valueInMap);
		valueInMap=new ValueInMap(3,(double)1);
		valueList.add(valueInMap);
		valueInMap=new ValueInMap(4,(double)1/2);
		valueList.add(valueInMap);
		graphMap.put(3, valueList);

		valueList=new ArrayList<ValueInMap>();
		valueInMap=new ValueInMap(1,(double)1/3);
		valueList.add(valueInMap);
		valueInMap=new ValueInMap(2,(double)1/2);
		valueList.add(valueInMap);
		graphMap.put(4, valueList);
//		graphMap.get(2)
		while(check(p1,p2)){
			for(int i=1;i<=nodeNumber;i++){
				p1[i]=p2[i];
			}
			for(int i=1;i<=nodeNumber;i++){
				p2[i]=0;
				if(graphMap.get(i)!=null){
					List getList=(List)graphMap.get(i);
					for(int j=0;j<getList.size();j++){
						valueInMap=new ValueInMap(0,0);
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
		
	}
	public  static boolean check(double[] p1,double []p2){
		boolean flag=false;
		for(int i=1;i<=nodeNumber;i++){
			if(((p1[i]-p2[i])>boundary)||((p1[i]-p2[i])<(0-boundary))){
				flag=true;
			}
		}
		return flag;
		
	}

}

