package cn.edu.seu.ise.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import cn.edu.seu.ise.api.use.model.DepUseProfile;
import cn.edu.seu.ise.common.config.Log4jConfig;
import cn.edu.seu.ise.common.model.Snapshot;
import cn.edu.seu.ise.common.resolver.Resolver;
import cn.edu.seu.ise.common.resolver.aether.AetherResolver;

public class ResolverTest {
	
	@Test
	public void testResoler() {
		Log4jConfig.initLog4j();
		// set the path of the local repository
		Resolver resolver = new AetherResolver("F:/Repo");
		File sourceCodeDir=new File("E:/SourceCode/Crawler/tempRep");
		File[] files=sourceCodeDir.listFiles();
		for(File itr:files){
			// set the project directory
			System.out.println(itr.getAbsolutePath());
			File lib=new File(itr.getAbsolutePath()+"/lib.txt");
			try {
				BufferedWriter libWriter=new BufferedWriter(new FileWriter(lib));
				DepUseProfile profile = resolver.resolve(new Snapshot(itr));
				List<File> depFiles = profile.getFiles();
					for(File depFile : depFiles) {
						String fileName=depFile.getAbsolutePath();
						System.out.println(depFile.getAbsolutePath());
						libWriter.write(fileName+"\r\n");
					}
				libWriter.close();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// set the project directory
	}
}
