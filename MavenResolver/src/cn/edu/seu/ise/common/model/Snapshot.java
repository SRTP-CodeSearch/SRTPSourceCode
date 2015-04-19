package cn.edu.seu.ise.common.model;

import static cn.edu.seu.ise.common.constants.Extensions.GRADLE;
import static cn.edu.seu.ise.common.constants.Extensions.LEININGEN;
import static cn.edu.seu.ise.common.constants.Extensions.NONE;
import static cn.edu.seu.ise.common.constants.Extensions.SBT;
import static cn.edu.seu.ise.common.constants.Extensions.XML;
import static cn.edu.seu.ise.common.helper.StringHelper.EMPTY;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import cn.edu.seu.ise.common.helper.FileHelper;
import cn.edu.seu.ise.common.helper.MapHelper;

/**
 * The abstract class of the project snapshot. 
 * <p>
 * It represents the source code checked out from a commit from repository. 
 * </p>
 * 
 * @author Dong Qiu
 *
 */
public class Snapshot {
	
	/** The snapshot name*/
	@Getter private String projName;
	
	/** The source directory */
	@Getter protected File sourceDir; 
	
	/** The commit time */
	@Setter @Getter protected String time = EMPTY;
	
	/** The unique abbregated name of the commit */
	@Setter @Getter protected String name = EMPTY;
	
	/** The file distribution map */
	private Map<String, List<File>> fileMap;
	
	/** The build config file map */
	private Map<BuildType, List<File>> buildConfigMap;
	
	/** The size of total files */
	@Getter private int size = 0;
	
	/**
	 * Constructor. 
	 * 
	 * @param sourceDir  the source directory
	 * @param info  the snapshot information
	 */
	public Snapshot(File sourceDir) {
		this.sourceDir = sourceDir;
		this.projName = sourceDir.getName();
		this.fileMap = FileHelper.getFileMap(sourceDir);
		this.buildConfigMap = getBuildFiles();
		// gets the size of total files
		for(Map.Entry<String, List<File>> entry : fileMap.entrySet()) {
			size += entry.getValue().size();
		}
	}
	
	/**
	 * Gets the list of files by the given extension. 
	 * 
	 * @param extension  the file name extension
	 * @return  the list of files
	 */
	public List<File> getFiles(@NonNull String extension) {
		if(!fileMap.containsKey(extension)) {
			return new ArrayList<File>();
		}
		return fileMap.get(extension);
	}
	
	/**
	 * Gets the proportion of the given language. 
	 * 
	 * @param extension  the language extension
	 * @return  the proportion
	 */
	public double langRate(@NonNull String extension) {
		List<File> files = getFiles(extension);
		return files.size() / (double) size;
	}
	
	/**
	 * Checks whether the snapshot is built by given type of build tool.
	 * 
	 * @param type  the type of the build tool
	 * @return  {@code true} if the snapshot is built by the given build tool; 
	 * {@code false} otherwise
	 */
	public boolean buildBy(@NonNull BuildType type) {
		return buildConfigMap.containsKey(type);
	}
	
	/**
	 * Gets the build config files by given build type. 
	 * 
	 * @param type  the build type
	 * @return  the list of build files
	 */
	public List<File> getBuildFiles(@NonNull BuildType type) {
		if(!buildBy(type)) {
			return new ArrayList<>();
		}
		return buildConfigMap.get(type);
	}
	
	/**
	 * Gets the set of the build types. 
	 * 
	 * @return  the set of build types
	 */
	public Set<BuildType> getBuildTypes() {
		Set<BuildType> types = EnumSet.noneOf(BuildType.class);
		for(Map.Entry<BuildType, List<File>> entry : buildConfigMap.entrySet()) {
			if(entry.getValue().size() > 0) {
				types.add(entry.getKey());
			}
 		}
		return types;
	}

	/**
	 * Gets the map of all build files. 
	 * 
	 * @return  the build files map where the key is the build tool type, 
	 * and the value is the corresponding 
	 */
	private Map<BuildType, List<File>> getBuildFiles() {
		Map<BuildType, List<File>> buildConfigFileMap = new HashMap<>();
		// handle the maven and ivy config files
		for(File xmlFile : getFiles(XML)) {
			String fileName = xmlFile.getName();
			if(fileName.equalsIgnoreCase("pom.xml")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.MAVEN, xmlFile);
			} else if(fileName.equalsIgnoreCase("ivy.xml")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.IVY, xmlFile);
			} 
		}
		// handle the gradle config files
		for(File gradleFiles : getFiles(GRADLE)) {
			String fileName = gradleFiles.getName();
			if(fileName.equalsIgnoreCase("build.gradle")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.GRADLE, gradleFiles);
			}
		}
		// handle the SBT config files
		for(File sbtFile : getFiles(SBT)) {
			String fileName = sbtFile.getName();
			if(fileName.equalsIgnoreCase("build.sbt")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.SBT, sbtFile);
			}
		}
		// handle the leiningen config file
		for(File leiningenFile : getFiles(LEININGEN)) { 
			String fileName = leiningenFile.getName();
			if(fileName.equalsIgnoreCase("project.clj")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.LEININGEN, leiningenFile);
			}
		}
		// handle the buildr config file
		for(File buildrFile : getFiles(NONE)) {
			String fileName = buildrFile.getName();
			if(fileName.equalsIgnoreCase("buildfile")) {
				MapHelper.updateListMap(buildConfigFileMap, BuildType.BUILDR, buildrFile);
			}
		}
		return buildConfigFileMap;
	}
}
