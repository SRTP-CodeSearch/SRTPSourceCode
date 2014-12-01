package DeleteUselessFile;

import java.io.File;
import java.io.IOException;

public class DeleteFile {
	public static void main(String argv[]) throws IOException{
		File f=new File("F:/SRTP/AllLibs");
		File[] files = f.listFiles();
		for(int i=0;i<files.length;i++){
		System.out.println(files[i].getCanonicalPath());
		}
		//parseFilesInDir(f);
	}
	public static void parseFilesInDir(File f){
		File[] files = f.listFiles();
		if(files.length==0){
			f.deleteOnExit();
		}
		else{
			
			String filePath = null; 
			for (File ftemp : files ) {
				if(ftemp.isDirectory()){
					parseFilesInDir(ftemp);
				}
				filePath = ftemp.getAbsolutePath();
				System.out.println(filePath);
				if(ftemp.isFile()){
					if(!ftemp.getName().toLowerCase().endsWith(".jar")){
						ftemp.deleteOnExit();
					}
				 
				}
			}
			}
	}
	

}
