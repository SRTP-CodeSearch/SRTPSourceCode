package SearchInDb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import SearchInDb.SearchInDb.RelTypes;

public class RelationshipHundle extends SearchInDb {
	public static StringBuilder searchInvokeRel() {
		// File Dir = new File("/var/temp.csv");
		Label label = DynamicLabel.label("method");
		StringBuilder RelationShipData = new StringBuilder(1000);
		StringBuilder pr = new StringBuilder(1000);
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				for (Relationship iter : node.getRelationships(RelTypes.INVOKE,
						Direction.OUTGOING)) {
					String datatemp = "" + node.getId() + "," + iter.getId()
							+ "\r";
					RelationShipData.append(datatemp);
				}
			}
		}
		return RelationShipData;
	}

	public static void setPrIndex() {
		Label label = DynamicLabel.label("class");
		long i = 1;
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "class")) {
				node.setProperty("prIndex", i);
				i++;
				System.out.println(i);
			}
			tx.success();
		}

	}
	public static void setMethodPrIndex() {
		Label label = DynamicLabel.label("method");
		long i = 1;
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				node.setProperty("prIndex", i);
				i++;
				System.out.println(i);
			}
			tx.success();
		}

	}

	public static void main(String argv[]) {
		//计算pagerank值写入文件
		setPrIndex();
		setMethodPrIndex();
		writeRelationInFile();
		writeMethodRelationInFile();
		//findByIndex();
	//自然语言处理的文件
//		writeClassName();
//		writeMethodName();
	}

	public static void findByIndex() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "index", -117417897)) {
				System.out.println(node.getProperty("className"));
			}

		}
	}

	public static void writeRelationInFile() {
		int count=0;
		File superFolder = new File("var");
		File[] dir = superFolder.listFiles();
		for (File temp : dir) {
			if (temp.getName().equals("classGraph.csv")
					|| temp.getName().equals("classPr.csv")) {
				temp.delete();
			}
		}
		File methodRel = new File("var/classGraph.csv");
		File methodPr = new File("var/classPr.csv");
		Label label = DynamicLabel.label("class");
		StringBuilder methodRelData = new StringBuilder(1000);
		StringBuilder methodPrData = new StringBuilder(1000);
		String methodRelTemp;
		String methodPrTemp;
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "class")) {
				methodPrTemp = "" + node.getProperty("prIndex") + "," + 1
						+ "\n";
				methodPrData.append(methodPrTemp.toString());
				System.out.println(node.getId());
				for (Relationship iter : node.getRelationships(RelTypes.USE,
						Direction.OUTGOING)) {
					try {
						methodRelTemp = "" + node.getProperty("prIndex") + ","
								+ iter.getEndNode().getProperty("prIndex") + "\n";
						methodRelData.append(methodRelTemp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						count++;
					}
				}
				if (methodRelData.length() > 1000000) {
					BufferedWriter relWriter;
					BufferedWriter prWriter;
					try {
						relWriter = new BufferedWriter(new FileWriter(
								methodRel.getCanonicalPath(), true));
						prWriter = new BufferedWriter(new FileWriter(
								methodPr.getCanonicalPath(), true));
						relWriter.write(methodRelData.toString());
						prWriter.write(methodPrData.toString());
						relWriter.close();
						prWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					methodRelData = new StringBuilder(1000);
					methodPrData = new StringBuilder(1000);
				}
			}
			tx.success();
		}
		BufferedWriter relWriter;
		BufferedWriter prWriter;
		try {
			relWriter = new BufferedWriter(new FileWriter(
					methodRel.getCanonicalPath(), true));
			prWriter = new BufferedWriter(new FileWriter(
					methodPr.getCanonicalPath(), true));
			relWriter.write(methodRelData.toString());
			prWriter.write(methodPrData.toString());
			relWriter.close();
			prWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(count);
	}
	public static void writeMethodRelationInFile() {
		int count=0;
		File superFolder = new File("var");
		File[] dir = superFolder.listFiles();
		for (File temp : dir) {
			if (temp.getName().equals("methodGraph.csv")
					|| temp.getName().equals("methodPr.csv")) {
				temp.delete();
			}
		}
		File methodRel = new File("var/methodGraph.csv");
		File methodPr = new File("var/methodPr.csv");
		Label label = DynamicLabel.label("method");
		StringBuilder methodRelData = new StringBuilder(1000);
		StringBuilder methodPrData = new StringBuilder(1000);
		String methodRelTemp;
		String methodPrTemp;
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				methodPrTemp = "" + node.getProperty("prIndex") + "," + 1
						+ "\n";
				methodPrData.append(methodPrTemp.toString());
				System.out.println(node.getId());
				for (Relationship iter : node.getRelationships(RelTypes.INVOKE,
						Direction.OUTGOING)) {
					try {
						methodRelTemp = "" + node.getProperty("prIndex") + ","
								+ iter.getEndNode().getProperty("prIndex") + "\n";
						methodRelData.append(methodRelTemp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						count++;
					}
				}
				if (methodRelData.length() > 1000000) {
					BufferedWriter relWriter;
					BufferedWriter prWriter;
					try {
						relWriter = new BufferedWriter(new FileWriter(
								methodRel.getCanonicalPath(), true));
						prWriter = new BufferedWriter(new FileWriter(
								methodPr.getCanonicalPath(), true));
						relWriter.write(methodRelData.toString());
						prWriter.write(methodPrData.toString());
						relWriter.close();
						prWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					methodRelData = new StringBuilder(1000);
					methodPrData = new StringBuilder(1000);
				}
			}
			tx.success();
		}
		BufferedWriter relWriter;
		BufferedWriter prWriter;
		try {
			relWriter = new BufferedWriter(new FileWriter(
					methodRel.getCanonicalPath(), true));
			prWriter = new BufferedWriter(new FileWriter(
					methodPr.getCanonicalPath(), true));
			relWriter.write(methodRelData.toString());
			prWriter.write(methodPrData.toString());
			relWriter.close();
			prWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(count);
	}

	public static void ReadLinuxFile() throws FileNotFoundException,
			IOException {
		File readLinuxFile = new File("var/people.csv");
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(
				readLinuxFile.getCanonicalPath()));
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		System.out.println(fileData.toString());

	}
	public static void writeClassName(){
		int parseCount=0;
		int noparseCount=0;
		File superFolder = new File("var");
		File[] dir = superFolder.listFiles();
		for (File temp : dir) {
			if (temp.getName().equals("class.txt")) {
				temp.delete();
			}
		}
		Label label = DynamicLabel.label("class");
		HashMap<String,Integer> classNodeMap=new HashMap<String,Integer>();
		File classFile=new File("var/class.txt");
		BufferedWriter classWriter;
		try {
			classWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "class")) {
				String classNameTemp=(String)node.getProperty("className");
				System.out.println(classNameTemp);
				System.out.println((boolean)node.getProperty("parseState"));
				if((boolean)node.getProperty("parseState")){
				if(!classNodeMap.containsKey(classNameTemp)){
					classNodeMap.put(classNameTemp,1);
				}else{
					Integer times=(Integer)classNodeMap.get(classNameTemp);
					times++;
					classNodeMap.put(classNameTemp,times);
				}
				parseCount++;
				}else{
					noparseCount++;
				}
			}
			tx.success();
		}
		Set<String> classNameSet=classNodeMap.keySet();
		for(String classNameTemp:classNameSet){
			try {
//				classWriter.write(classNameTemp+"\t");
				Integer times=(Integer)classNodeMap.get(classNameTemp);
				if(classNameTemp.equals("CharacterIterator")){
					System.out.println(times);
				}
//				String temp=classNameTemp+"\t"+times+"\r\n";
				String temp=classNameTemp+"\r\n";
				//System.out.println(classNameTemp+"\t"+times+"\r\n");
				classWriter.write(temp);
				classWriter.flush();
				//classWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(parseCount);
		System.out.println(noparseCount);
		
	}
	public static void writeMethodName(){
		File superFolder = new File("var");
		File[] dir = superFolder.listFiles();
		for (File temp : dir) {
			if (temp.getName().equals("method.txt")) {
				temp.delete();
			}
		}
		Label label = DynamicLabel.label("method");
		HashMap<String,Integer> classNodeMap=new HashMap<String,Integer>();
		File classFile=new File("var/method.txt");
		BufferedWriter classWriter;
		try {
			classWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(classFile)));
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				String classNameTemp=(String)node.getProperty("methodName");
				if((boolean)node.getProperty("parseState")){
				if(!classNodeMap.containsKey(classNameTemp)){
					classNodeMap.put(classNameTemp,1);
				}else{
					Integer times=(Integer)classNodeMap.get(classNameTemp);
					times++;
					classNodeMap.put(classNameTemp,times);
				}
				}
			}
			tx.success();
		}
		Set<String> classNameSet=classNodeMap.keySet();
		for(String classNameTemp:classNameSet){
			try {
//				classWriter.write(classNameTemp+"\t");
				Integer times=(Integer)classNodeMap.get(classNameTemp);
				if(classNameTemp.equals("CharacterIterator")){
					System.out.println(times);
				}
//				String temp=classNameTemp+"\t"+times+"\r\n";
				String temp=classNameTemp+"\r\n";
				//System.out.println(classNameTemp+"\t"+times+"\r\n");
				classWriter.write(temp);
				classWriter.flush();
				//classWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
