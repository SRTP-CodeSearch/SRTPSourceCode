package SearchInDb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
		Label label = DynamicLabel.label("method");
		long i = 1;
		try (Transaction tx = graphDb.beginTx()) {
			for (Node node : SearchInDb.graphDb.findNodesByLabelAndProperty(
					label, "type", "method")) {
				node.setProperty("prIndex", i);
				i++;
			}
			tx.success();
		}

	}

	public static void main(String argv[]) {
		setPrIndex();
		writeRelationInFile();
		// findByIndex();
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
					methodRelTemp = "" + node.getProperty("prIndex") + ","
							+ iter.getEndNode().getProperty("prIndex") + "\n";
					methodRelData.append(methodRelTemp);
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

}
