package TestPageRank;

public class testAnyway {
	public static final int nodeNumber=4;
	public static void main( String argv[]){
		
		double e[]=new double[nodeNumber];
		double p1[]=new double[nodeNumber];
		double p2[]=new double[nodeNumber];
		for(int i=0;i<nodeNumber;i++){
			p1[i]=(double)1/nodeNumber;
		}
		e=p1;
		p1[1]=(double)1/5;
		System.out.println(e.length);
		System.out.println(e[1]);
	}

}
