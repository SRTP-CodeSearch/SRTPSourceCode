import java.io.*;
import java.util.*;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;



import org.neo4j.unsafe.batchinsert.BatchInserter;

//import NodeHandle.MyNode;
import Node.*;
import Node.RelMap.PairsOfNode;


public class ParseMain extends MyNode  {
	// All classPathEntries
	public static ArrayList<String> classPathEntries;
//	// All classPath
//	public static String classPathEntriesLocation = "C:/SRTP/AllLibs";
	// Every project sourcePath
	public static ArrayList<String> sourcePath;
	// ProjectNode Set
	public static ProjectNodeMap projectNodeMap;
	// ClassNode Set
	public static ClassNodeMap classNodeMap;
	// MethodNode Set
	public static MethodNodeMap methodNodeMap;
	// containMethod Set
	public static RelMap containMethodMap;
	// containClass relations Set
	public static RelMap containClassMap;
	// invokeMethod relations Set
	public static RelMap invokeMethodMap;
	// useClass relations Set
	public static RelMap useClassMap;
	// impleInterface relations Set
	public static RelMap impleInterfaceMap;
	// extendClass relations Set
	public static RelMap extendClassMap;

	public ParseMain() {
		classPathEntries = new ArrayList<String>();
		projectNodeMap = new ProjectNodeMap();
		classNodeMap = new ClassNodeMap();
		methodNodeMap = new MethodNodeMap();
		containMethodMap = new RelMap();
		containClassMap = new RelMap();
		invokeMethodMap = new RelMap();
		useClassMap = new RelMap();
		impleInterfaceMap = new RelMap();
		extendClassMap = new RelMap();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// reset db
		MyNode.deleteFileOrDirectory(new File(MyNode.DB_PATH));
		MyNode.startDb();
		MyNode.prepare();

		// String AllProjectsFileLocation = "C:/SRTP/SimpleProject";

		String AllProjectsFileLocation = "E:/SourceCode/Crawler/asf";
		ParseMain parsemain = new ParseMain();
		parsemain.parseProjects(AllProjectsFileLocation);
		System.out.println(Requestor.enumBindingException);
		System.out.println(Requestor.enumBindingSuccess);
		System.out.println(Requestor.methodBindingException);
		System.out.println(Requestor.methodBindingSuccess);
		System.out.println(Requestor.methodInvokeBindingException);
		System.out.println(Requestor.methodInvokeBindingSuccess);
		System.out.println(Requestor.staticBindingException);
		System.out.println(Requestor.staticBindingSuccess);
		System.out.println(Requestor.superBindingException);
		System.out.println(Requestor.superBindingSuccess);
		System.out.println(Requestor.TypeBindingException);
		System.out.println(Requestor.TypeBindingSuccess);

		//
		// test

		// //所有节点
		// methodNodeMap.printAllNodes();
		// classNodeMap.printAllNodes();
		// projectNodeMap.printAllNodes();
		//
		// //project和class之间的contain关系
		// projectNodeMap.printAllNodesIntIndex();
		// containClassMap.printAllRel();
		// classNodeMap.printAllNodesIntIndex();
		//
		// //class和method之间的contain关系
		// classNodeMap.printAllNodesIntIndex();
		// containMethodMap.printAllRel();
		// methodNodeMap.printAllNodesIntIndex();
		//
		// //class和class之间的use关系
		// classNodeMap.printAllNodesIntIndex();
		// useClassMap.printAllRel();
		//
		// //method和method之间的invoke关系
		// methodNodeMap.printAllNodesIntIndex();
		// invokeMethodMap.printAllRel();
		//
		// //class和interface之间的implement关系
		// classNodeMap.printAllNodesIntIndex();
		// impleInterfaceMap.printAllRel();
		// parsemain.printAllNodesString(impleInterfaceMap.getAll());
		//
		// //class和class、interface和interface之间的extend关系
		// classNodeMap.printAllNodesIndex();
		// extendClassMap.printAllRel();

	}

	public void parseProjects(String projectPathEntries) throws IOException {
		File[] projectFiles = new File(projectPathEntries).listFiles();
		for (File f : projectFiles) {
			if (f.isDirectory()) {
				String[] sourcePathEntries = new String[1];
				sourcePathEntries[0] = f.getCanonicalPath();
				getClassPath(new File(f.getCanonicalPath()+"/lib.txt"));
				getClassPathFromLocal(f);
				sourcePath = new ArrayList<String>();
				sourcePath.clear();
				ParseFilesInDir(new File(f.getCanonicalPath()));
				ProjectParser parserPJ = new ProjectParser(f.getName(),
						f.getCanonicalPath(), sourcePathEntries,
						classPathEntries, sourcePath);
				parserPJ.parse();
				for (MethodNode iter : methodNodeMap.getAllNodes()) {
					System.out.println(iter.MethodName + "----"
							+ iter.hashCode());
				}
				for (PairsOfNode iter : invokeMethodMap.getAll()) {
					System.out.println(iter.getNode1Index() + "---"
							+ iter.getNode2Index());
				}
				ParseMain.createProjectNodeInDb();
				ParseMain.createClassNodeInDb();
				ParseMain.createMethodNodeInDb();
				ParseMain.createContainClassRel();
				ParseMain.createContainMethodRel();
				ParseMain.createInvokeMethodRel();
				ParseMain.createUseClassRel();
				ParseMain.createExtendClassRel();
				ParseMain.createImpleInterfaceRel();
				//每次解析一个新的工程重新指定对象，交给垃圾回收机制处理
//				projectNodeMap = new ProjectNodeMap();
//				classNodeMap = new ClassNodeMap();
//				methodNodeMap = new MethodNodeMap();
//				containMethodMap = new RelMap();
//				containClassMap = new RelMap();
//				invokeMethodMap = new RelMap();
//				useClassMap = new RelMap();
//				impleInterfaceMap = new RelMap();
//				extendClassMap = new RelMap();
				projectNodeMap.projectNodeMap.clear();
				classNodeMap.classNodeMap.clear();
				methodNodeMap.methodNodeMap.clear();
				containMethodMap.containRelMap.clear();
				containClassMap.containRelMap.clear();
				invokeMethodMap.containRelMap.clear();
				useClassMap.containRelMap.clear();
				impleInterfaceMap.containRelMap.clear();
				extendClassMap.containRelMap.clear();
				// 工程节点同class的contain关系当 class重复的时候 依然建立关系
				
				System.gc();

			}
		}
	}

	public void printAllNodesString(Collection<PairsOfNode> rels) {
		for (PairsOfNode iter : rels) {
			System.out.println(classNodeMap.getNode(iter.getNode1Index())
					+ "----" + classNodeMap.getNode(iter.getNode2Index()));
		}
	}

	public static void createProjectNodeInDb() {
		try (Transaction tx = graphDb.beginTx()) {
			Label labelName = DynamicLabel.label("project");
			Label labelIndex = DynamicLabel.label("index");
			Label prIndex = DynamicLabel.label("prIndex");
			org.neo4j.graphdb.Node newNode = null;
			// Create some nodes
			for (ProjectNode iter : projectNodeMap.getAllNodes()) {
				newNode = null;
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(labelIndex, "index",
								iter.hashCode())) {
					newNode = node;
					if(iter.parseState){
						newNode.setProperty("parseState", iter.parseState);
						newNode.setProperty("fileLocation", iter.fileLocation);
					}
					
				}
				if (newNode == null) {
					newNode = graphDb.createNode(labelName);
					newNode.addLabel(labelIndex);
					newNode.addLabel(prIndex);
					newNode.setProperty("projectName", iter.projectName);
					newNode.setProperty("fileLocation", iter.fileLocation);
					newNode.setProperty("parseState", iter.parseState);
					newNode.setProperty("type", "project");
					newNode.setProperty("index", iter.hashCode());
				}

			}

			tx.success();

		}
	}

	public static void createClassNodeInDb() {
		try (Transaction tx = graphDb.beginTx()) {
			Label labelName = DynamicLabel.label("class");
			Label labelIndex = DynamicLabel.label("index");
			Label prIndex = DynamicLabel.label("prIndex");
			// Create some nodes
			org.neo4j.graphdb.Node newNode = null;
			for (ClassNode iter : classNodeMap.getAllNodes()) {
				newNode = null;
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(labelIndex, "index",
								iter.hashCode())) {
					//更新parseState
					newNode = node;
					if(iter.parseState){
						newNode.setProperty("parseState", iter.parseState);
						newNode.setProperty("fileLocation", iter.fileLocation);
					}
				}
				if (newNode == null) {
					newNode = graphDb.createNode(labelName);
					newNode.addLabel(labelIndex);
					newNode.addLabel(prIndex);
					newNode.setProperty("className", iter.className);
					newNode.setProperty("fileLocation", iter.fileLocation);
					newNode.setProperty("classType", iter.classType);
					newNode.setProperty("packageName", iter.packageName);
					newNode.setProperty("type", "class");
					newNode.setProperty("parseState", iter.parseState);
					newNode.setProperty("index", iter.hashCode());
				}
			}

			tx.success();

		}

	}

	public static void createMethodNodeInDb() {
		try (Transaction tx = graphDb.beginTx()) {
			Label labelName = DynamicLabel.label("method");
			Label labelIndex = DynamicLabel.label("index");
			Label prIndex = DynamicLabel.label("prIndex");
			org.neo4j.graphdb.Node newNode = null;

			// Create some nodes
			for (MethodNode iter : methodNodeMap.getAllNodes()) {
				newNode = null;
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(labelIndex, "index",
								iter.hashCode())) {
					newNode = node;
					if(iter.parseState){
						newNode.setProperty("parseState", iter.parseState);
					}
				}
				if (newNode == null) {
					newNode = graphDb.createNode(labelName);
					newNode.addLabel(labelIndex);
					newNode.addLabel(prIndex);
					newNode.setProperty("methodName", iter.MethodName);
					newNode.setProperty("param", iter.param);
					newNode.setProperty("type", "method");
					newNode.setProperty("parseState", iter.parseState);
					newNode.setProperty("index", iter.hashCode());
				}
			}

			tx.success();

		}

	}

	public static void createContainClassRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempProNode = null;
			org.neo4j.graphdb.Node tempClassNode = null;
			for (PairsOfNode iter : containClassMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempProNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempClassNode = node;
				}
				if(tempClassNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING)==null){
					tempProNode.createRelationshipTo(tempClassNode,
						RelTypes.CONTAIN);
				}
				

			}
			tx.success();
		}

	}

	public static void createContainMethodRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempClassNode = null;
			org.neo4j.graphdb.Node tempMethodNode = null;
			for (PairsOfNode iter : containMethodMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempClassNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempMethodNode = node;
				}
				if(tempMethodNode.getSingleRelationship(RelTypes.CONTAIN, Direction.INCOMING)==null){
					tempClassNode.createRelationshipTo(tempMethodNode,
						RelTypes.CONTAIN);
				}
			}
			tx.success();
		}

	}

	public static void createInvokeMethodRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempInvokerNode = null;
			org.neo4j.graphdb.Node tempMethodNode = null;
			for (PairsOfNode iter : invokeMethodMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempInvokerNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempMethodNode = node;
				}
				boolean flag = false;
				for(Relationship iterInvoke : tempMethodNode.getRelationships(RelTypes.INVOKE, Direction.INCOMING)){
					if(iterInvoke.getStartNode().equals(tempInvokerNode))
						flag = true;
				}
				if(flag == false){
				tempInvokerNode.createRelationshipTo(tempMethodNode,
						RelTypes.INVOKE);
				}

			}
			tx.success();
		}

	}

	public static void createUseClassRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempUserNode = null;
			org.neo4j.graphdb.Node tempClassNode = null;
			for (PairsOfNode iter : useClassMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempUserNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempClassNode = node;
				}
				boolean flag = false;
				for(Relationship iterUse : tempClassNode.getRelationships(RelTypes.USE, Direction.INCOMING)){
					if(iterUse.getStartNode().equals(tempUserNode))
						flag = true;
				}
				if(flag == false){
					tempUserNode.createRelationshipTo(tempClassNode, RelTypes.USE);
				}
			}
			tx.success();
		}

	}

	public static void createImpleInterfaceRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempImplementerNode = null;
			org.neo4j.graphdb.Node tempClassNode = null;
			for (PairsOfNode iter : impleInterfaceMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempImplementerNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempClassNode = node;
				}
				boolean flag = false;
				for(Relationship iterImplement : tempClassNode.getRelationships(RelTypes.IMPLEMENT, Direction.INCOMING)){
					if(iterImplement.getStartNode().equals(tempImplementerNode))
						flag = true;
				}
				if(flag == false){
					tempImplementerNode.createRelationshipTo(tempClassNode,
							RelTypes.IMPLEMENT);
				}
			}
			tx.success();
		}

	}

	public static void createExtendClassRel() {
		Label label = DynamicLabel.label("index");
		try (Transaction tx = graphDb.beginTx()) {
			org.neo4j.graphdb.Node tempExtenderNode = null;
			org.neo4j.graphdb.Node tempClassNode = null;
			for (PairsOfNode iter : extendClassMap.getAll()) {
				// ProjectNode
				// proNode=this.projectNodeMap.getNode(iter.getNode1Index());
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode1Index())) {
					tempExtenderNode = node;
				}
				for (org.neo4j.graphdb.Node node : graphDb
						.findNodesByLabelAndProperty(label, "index",
								iter.getNode2Index())) {
					tempClassNode = node;
				}
				boolean flag = false;
				for(Relationship iterExtend : tempClassNode.getRelationships(RelTypes.EXTEND, Direction.INCOMING)){
					if(iterExtend.getStartNode().equals(tempExtenderNode))
						flag = true;
				}
				if(flag == false){
					tempExtenderNode.createRelationshipTo(tempClassNode,
							RelTypes.EXTEND);
				}
			}
			tx.success();
		}

	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void ParseFilesInDir(File file) throws IOException {
		File[] files = file.listFiles();
		if (files.length == 0) {
			file.deleteOnExit();
		} else {
			String filePath = null;
			for (File f : files) {
				if (f.isDirectory()) {
					ParseFilesInDir(f);
				}
				filePath = f.getAbsolutePath();
				// System.out.println(filePath);
				if (f.isFile()) {
					if (f.getName().toLowerCase().endsWith(".java")) {
						sourcePath.add(filePath);
					}
				}
			}
		}
	}

	public void getClassPathFromLocal(File file) throws IOException {
		File[] files = file.listFiles();
		if (files.length == 0) {
			file.deleteOnExit();
		} else {
			String filePath = null;
			for (File f : files) {
				if (f.isDirectory()) {
					getClassPathFromLocal(f);
				}
				// hehe test github
				// hehe2
				filePath = f.getAbsolutePath();
				// System.out.println(filePath);
				if (f.isFile()) {
					if (f.getName().toLowerCase().endsWith(".jar")) {
						//classPathEntries.clear();
						classPathEntries.add(filePath);
					}
				}
			}
		}
	}

	
	public void getClassPath(File f){
		classPathEntries.clear();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(f));
			String tempString = null;
			while((tempString = reader.readLine())!=null){
				classPathEntries.add(tempString);
				}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
//		for(String classPath:classPathEntries){
//			System.out.println(classPath);
//		}
	}
	@Override
	public void createNode() {
		// TODO Auto-generated method stub

	}
}
