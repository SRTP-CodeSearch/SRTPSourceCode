package cn.edu.seu.ise.common.resolver.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.seu.ise.common.helper.FileHelper;

/**
 * Maven loader class that loads POM files and retrives the dependencies
 * defined in the pom files. 
 * 
 * @author Dong Qiu
 *
 */
public class MavenLoader {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(MavenLoader.class.getName());
	
	/** The build file name of maven-based project */
	public static final String MAVEN_CONFIG = "pom.xml";
	
	
	/** Shared model map for search parent models */
	private Map<String, POMModel> sharedModels = new HashMap<>();	
	
	/** MultiPOM dependencies */
	private MultiPOMDependencies multiPOMDeps;
	
	/** The list of the POM models */
	private List<POMModel> models = new ArrayList<>();
	
	/** List of POM files */
	private List<File> pomFiles;
	
	/** List of the inner dependency names */
	private Set<String> innerDeps = new HashSet<>();
	
	/**
	 * Constructor. 
	 * 
	 * @param pomFiles  the list of the POM files
	 */
	public MavenLoader(List<File> pomFiles) {
		this.pomFiles = pomFiles;
		loadPOMs();
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param sourceDir  the source directory
	 */
	public MavenLoader(File sourceDir) {
		// searches pom files under project directory
		this(FileHelper.listSubFiles(sourceDir, MAVEN_CONFIG, true));
	}
	
	/**
	 * Gets the {@link MultiPOMDependencies} instance. 
	 * 
	 * @return  the {@link MultiPOMDependencies} instance
 	 */
	public MultiPOMDependencies getMultiPOMDependencies() {
		if(multiPOMDeps != null) {
			return multiPOMDeps;
		}
		multiPOMDeps = new MultiPOMDependencies();
		for(POMModel model : models) {
			if(!model.isExternal()) {
				multiPOMDeps.add(model.getPOMDependencies().filterBy(innerDeps));
			}
		}
		return multiPOMDeps;
	}
	
	/**
	 * Loads POM files. 
	 */
	private void loadPOMs() {
		for(File pom : pomFiles) {
			try {
				POMModel model = POMReader.getPOMModel(pom);
				models.add(model);
				sharedModels.put(model.getName(), model);
				innerDeps.add(model.getGroupArtifactId());
			} catch (IOException e) {
				logger.warn("Fail to read POM {}, \n{}", pom.getAbsolutePath(), e);
			}
		}
		List<POMModel> parents = new ArrayList<>();
		// sets the parent models
		for(POMModel model : models) {
			if(!model.hasParent()) {
				continue;
			}
			if(sharedModels.containsKey(model.getParentName())) {
				model.setParent(sharedModels.get(model.getParentName()));
			} else {
				List<POMModel> parentModels = model.getParentModels();
				for(POMModel parentModel : parentModels) {
					parentModel.setExternal(true);
					if(!sharedModels.containsKey(parentModel.getName())) {
						sharedModels.put(parentModel.getName(), parentModel);
						parents.add(parentModel);
					}
				}
			}
		}
		models.addAll(parents);
		// resolves all the POM models
		for(POMModel model : models) {
			model.resolve();
		}
	}
}
