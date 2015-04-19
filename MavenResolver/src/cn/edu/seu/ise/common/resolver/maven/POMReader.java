package cn.edu.seu.ise.common.resolver.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import lombok.NonNull;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * POM reader class that is used to parse the POM file. 
 * 
 * @author Dong Qiu
 *
 */
public class POMReader {
	
	/**
	 * Gets maven model from the pom file.
	 *  
	 * @param pomFile  the pom file 
	 * @return  the maven model
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Model read(@NonNull File pomFile) throws IOException {
		try (FileReader reader = new FileReader(pomFile)){
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			return mavenReader.read(reader);
		} catch (FileNotFoundException e) {
			throw new IOException("Pom file " + pomFile.getAbsolutePath() +  "is not found", e);
		} catch (IOException e) {
			throw new IOException("Fail to read from pom file " + pomFile.getAbsolutePath(), e);
		} catch (XmlPullParserException e) {
			throw new IOException("Fail to read from pom file " + pomFile.getAbsolutePath(), e);
		} 
	}
	
	/**
	 * Gets {@link POMModel} instance from pom file. 
	 * 
	 * @param pomFile  the pom file
	 * @return  the {@link POMModel} instance
	 * 
	 * @throws IOException
	 */
	public static POMModel getPOMModel(@NonNull File pomFile) throws IOException {
		return new POMModel(pomFile);
	}
}
