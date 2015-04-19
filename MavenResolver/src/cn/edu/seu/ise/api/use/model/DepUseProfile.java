package cn.edu.seu.ise.api.use.model;


import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.EQUAL;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import cn.edu.seu.ise.common.resolver.aether.ResolveStatus;
import cn.edu.seu.ise.common.resolver.aether.ResolvedArtifact;
import cn.edu.seu.ise.common.resolver.aether.ResolvedDependency;

/**
 * Dependency use profile. 
 * 
 * @author Dong Qiu
 *
 */
public class DepUseProfile implements Serializable {
	/**Serial version uid */
	private static final long serialVersionUID = 1L;
	
	/** The project name that the dependency use profile corresponds to */
	@Getter private String name;
	/** List of resolved dependency */
	private transient List<ResolvedDependency> dependencies = new ArrayList<>();
	/** List of unique resolved dependencies */
	private List<ResolvedDependency> uniqueDependencies = new ArrayList<>();
	/** Flag shows whether the list of unique dependencies is ready */
	private boolean isStable = false;
	
	/**
	 * Constructor. 
	 * 
	 * @param name  the project name
	 */
	public DepUseProfile(String name) {
		this.name = name;
	}
	
	/**
	 * Adds a {@link ResolvedDependency} instance. 
	 * <p>
	 * If new {@link ResolvedDependency} instance has been added, 
	 * the field isStable should be set to false and the unique 
	 * dependencies should be refreshed if required. </p>
	 * 
	 * 
	 * @param dependency
	 */
	public void add(ResolvedDependency dependency) {
		dependencies.add(dependency);
		isStable = false;
	}
	
	/**
	 * Gets the list of unique resolved {@link ResolvedDependency} instance. 
	 * 
	 * @return  the list of unique resolved dependencies
	 */
	public List<ResolvedDependency> resolveUniqueDependencies() {
		if(isStable) {
			return uniqueDependencies;
		}
		uniqueDependencies = new ArrayList<>();
		// Dependency resolution status (for maintaining the uniqueness of the dependencies)
		Map<String, ResolveStatus> depStatus = new HashMap<>();	
		// Dependency wait map (for maintaining the uniqueness of the dependencies)
		Map<String, ResolvedDependency> wait = new HashMap<>();
		for(ResolvedDependency dep : dependencies) {
			String depId = dep.getMain().getCurrent().toString();
			if(depStatus.containsKey(depId)) {
				ResolveStatus curStatus = depStatus.get(depId);
				if(curStatus == EQUAL) {
					continue;
				}
				if(dep.getStatus() == EQUAL) {
					uniqueDependencies.add(dep);
					depStatus.put(depId, EQUAL);
					wait.remove(depId);
				}
				if(dep.getStatus().ordinal() < curStatus.ordinal()) {
					depStatus.put(depId, dep.getStatus());
					wait.put(depId, dep);
				}
			} else {
				if(dep.getStatus() == EQUAL) {
					uniqueDependencies.add(dep);
					depStatus.put(depId, EQUAL);
				} else {
					depStatus.put(depId, dep.getStatus());
					wait.put(depId, dep);
				}
			}
		}
		uniqueDependencies.addAll(wait.values());
		isStable = true;
		return uniqueDependencies;
	}
	
	/**
	 * Gets the unqiue dependency files. 
	 * 
	 * @return  the list of unique dependency files
	 */
	public List<File> getFiles() {
		List<File> depFiles = new ArrayList<>();
		Set<String> filePaths = new HashSet<>();
		for(ResolvedDependency resolvedDep : resolveUniqueDependencies()) {
			if(!resolvedDep.isResolved()) {
				continue;
			}
			for(ResolvedArtifact artifact : resolvedDep.getArtifacts()) {
				if(!artifact.isResolved()) {
					continue;
				}
				File depFile = artifact.getFile();
				if(filePaths.contains(depFile.getAbsolutePath())) {
					continue;
				}
				depFiles.add(depFile);
				filePaths.add(depFile.getAbsolutePath());
 			}
		}
		return depFiles;
	}
} 
