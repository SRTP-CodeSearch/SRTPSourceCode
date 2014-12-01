import java.io.*;
import java.util.*;


import NodeHandle.MyNode;


public class ParseMain {
	//All classPathEntries
	public	static ArrayList<String> classPathEntries ;
	//All classPath 
	public static String classPathEntriesLocation = "C:/SRTP/AllLibs" ;
	//Every project sourcePath
	public static ArrayList<String> sourcePath;
	
	public ParseMain(){
		classPathEntries = new ArrayList<String>();
	}
	public  static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//reset db
		MyNode.deleteFileOrDirectory(new File(MyNode.DB_PATH));
		MyNode.startDb();
		MyNode.prepare();
		
		//String AllProjectsFileLocation = "F:/SRTP/AllProjects";		
		String AllProjectsFileLocation = "C:/SRTP/AllProjects/SimpleProject/";		
		ParseMain parsemain = new ParseMain();
		parsemain.parseProjects(AllProjectsFileLocation);

	}
	public void parseProjects(String projectPathEntries) throws IOException{
		File [] projectFiles = new File(projectPathEntries).listFiles();
		for(File f: projectFiles){
			if(f.isDirectory()){
				String [] sourcePathEntries = new String[1];
				sourcePathEntries[0]=f.getCanonicalPath();
				getClassPath(new File(classPathEntriesLocation));
				sourcePath = new ArrayList<String>();
				sourcePath.clear();
				ParseFilesInDir(new File(f.getCanonicalPath()));
				ProjectParser parserPJ = new ProjectParser(f.getName(),f.getCanonicalPath(),sourcePathEntries,classPathEntries,sourcePath);
				parserPJ.parse();
			}
		}
	}
	/**
	 * @param file
	 * @throws IOException
	 */
	public  void ParseFilesInDir(File file) throws IOException{	
		File[] files = file.listFiles ( );
		if(files.length==0){
			file.deleteOnExit();
		}
		else{
		String filePath = null; 
		 for (File f : files ) {
			 if(f.isDirectory()){
				 ParseFilesInDir(f);
			 }
			 filePath = f.getAbsolutePath();
			 System.out.println(filePath);
			 if(f.isFile()){
				 if(f.getName().toLowerCase().endsWith(".java")){
				 sourcePath.add(filePath);
				 }
			 }
		 }
		}
	}

	public  void getClassPath(File file) throws IOException{
	File[] files = file.listFiles ( );
	if(files.length==0){
		file.deleteOnExit();
	}
	else{
	String filePath = null; 
	 for (File f : files ) {
		 if(f.isDirectory()){
			 getClassPath(f);
		 }
		 //hehe test github
		 filePath = f.getAbsolutePath();
		 System.out.println(filePath);
		 if(f.isFile()){
			 if(f.getName().toLowerCase().endsWith(".jar")){
				 classPathEntries.add(filePath);
			 }
		 }
	 }
	}
}
}
