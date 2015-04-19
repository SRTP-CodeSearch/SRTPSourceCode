package cn.edu.seu.ise.common.resolver.maven;

import static cn.edu.seu.ise.common.helper.StringHelper.isBlank;
import static cn.edu.seu.ise.common.helper.StringHelper.isNotBlank;
import static cn.edu.seu.ise.common.resolver.aether.Booter.newDefaultRepos;
import static cn.edu.seu.ise.common.resolver.maven.MavenHelper.equalByGroupArtifactId;
import static cn.edu.seu.ise.common.resolver.maven.MavenHelper.newCentralRepo;
import static cn.edu.seu.ise.common.resolver.maven.MavenHelper.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.seu.ise.common.resolver.aether.AetherResolver;
import cn.edu.seu.ise.common.resolver.aether.ResolvedArtifact;

/**
 * The enhanced {@link Model}. 
 * 
 * @author Dong Qiu
 *
 */
public class POMModel {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(POMModel.class.getName());
	
	/** POM files */
	private File pomFile;
	
	/** The current model */
	private Model model;
	
	/** The parent model */
	@Setter @Getter private POMModel parent;
	
	/** Flag shows whether the POM model is resolved */
	@Getter @Setter private boolean isResolved = false;
	
	/** The Properties defined in the model (contains inherited properties) */
	@Getter Properties properties = new Properties();
	
	/** The dependencies defined in the model */
	@Getter private List<Dependency> dependencies = new ArrayList<>(); 
	
	/** The repositories that can be used to resolve the dependencies */
	@Getter private List<Repository> repositories = new ArrayList<>();
	
	/** Flag shows whether the pom file is not from project */
	@Getter @Setter private boolean isExternal = false;
	
	/**
	 * Constructor. 
	 * 
	 * @param model  the {@link Model} instance 
	 * @throws IOException 
	 */
	public POMModel(@NonNull File pomFile) throws IOException {
		this.pomFile = pomFile;
		model = POMReader.read(pomFile);
	}
	
	/**
	 * Resolves the {@link POMModel} instance. 
	 */
	public void resolve() {
		if(isResolved) {
			return;
		}
		// if the POM has no parent
		if(parent == null) {
			// adds system properties
			properties.putAll(System.getProperties());
			addSelfProperties();
			dependencies.addAll(getSelfDependencies());
			// adds the default repository
			repositories.add(newCentralRepo());
			// adds the self defined repository
			repositories.addAll(getSelfRepositories());
			isResolved = true;
			return;
		}
		if(!parent.isResolved()) {
			parent.resolve();
		}
		properties.putAll(parent.getProperties());
		addSelfProperties();
		dependencies.addAll(getSelfDependencies());
		repositories.addAll(getSelfRepositories());
		repositories.addAll(parent.getRepositories());
		isResolved = true;
	}
	
	/**
	 * Gets the list of parent {@link POMModel} instances if exist. 
	 * 
	 * @return  the list of parent {@link POMModel} instances
	 */
	public List<POMModel> getParentModels() {
		List<POMModel> parents = new ArrayList<>();
		POMModel parent = getParentModel();
		while(parent != null) {
			parents.add(parent);
			if(!parent.hasParent()) {
				break;
			}
			parent = parent.getParentModel();
		}
		return parents;
	}

	/**
	 * Resolves the parent {@link POMModel}. 
	 * 
	 * @return  the parent {@link POMModel}; may be null if the resolution fails
	 */
	public POMModel getParentModel() {
		AetherResolver resolver = AetherResolver.getResolver();
		// prepare the remote repository
		List<RemoteRepository> remoteRepos = new ArrayList<>();
		for(Repository repo : model.getRepositories()) {
			remoteRepos.add(MavenToAether.convert(repo));
		}
		remoteRepos.addAll(newDefaultRepos());
		ResolvedArtifact resolvedPOM = resolver.resolveSmartly(
				new DefaultArtifact(getParentName()), remoteRepos);
		POMModel parentModel = null;
		if(resolvedPOM.isResolved()) {
			File pomFile = resolvedPOM.getCurrent().getFile();
			try {
				parentModel = POMReader.getPOMModel(pomFile);
				setParent(parentModel);
			} catch (IOException e) {
				logger.warn("Fail to resolve the {}, parent of {}, \n{}", 
						getName(), getParentName(), e);
			}
		}
		return parentModel;
	}
	
	/**
	 * Gets the {@link POMDependencies} instance. 
	 * 
	 * @return  the {@link POMDependencies} instance
	 */
	public POMDependencies getPOMDependencies() {
		POMDependencies pomDeps = new POMDependencies();
		pomDeps.addRepositories(getRepositories());
		for(Dependency dependency : getDependencies()) {
			if(isBlank(dependency.getVersion())) {
				updateVersion(dependency);
			} 
			pomDeps.addDependencies(getDependencies());
		}
		return pomDeps;
	}

	/**
	 * Gets the unique name of {@link POMModel} instance. 
	 * 
	 * @return  the unique name
	 */
	public String getName() {
		StringBuilder builder = new StringBuilder();
		Parent parent = model.getParent();
		if(model.getGroupId() == null) {
			if(parent == null || parent.getGroupId() == null) {
				builder.append("[unresolved]");
			} else {
				builder.append(parent.getGroupId());
			}
		} else {
			builder.append(model.getGroupId());
		}
		builder.append(":").append(model.getArtifactId())
			.append(":").append(model.getPackaging()).append(":");
		if(model.getVersion() == null) {
			if(parent == null || parent.getVersion() == null) {
				builder.append("[unresolved]");
			} else {
				builder.append(parent.getVersion());
			}
		} else {
			builder.append(model.getVersion());
		}
		return builder.toString();
	}
	
	/**
	 * Gets the simplified name of {@link POMModel} instance. 
	 * 
	 * @return  the simplified name
	 */
	public String getGroupArtifactId() {
		StringBuilder builder = new StringBuilder();
		Parent parent = model.getParent();
		if(model.getGroupId() == null) {
			if(parent == null || parent.getGroupId() == null) {
				builder.append("[unresolved]");
			} else {
				builder.append(parent.getGroupId());
			}
		} else {
			builder.append(model.getGroupId());
		}
		builder.append(":").append(model.getArtifactId());
		return builder.toString();
	}
	
	/**
	 * Gets the unique name of parent {@link POMModel} instance. 
	 * 
	 * @return  the unique name 
	 */
	public String getParentName() {
		if(hasParent()) {
			return model.getParent().getId();
		}
		return null;
	}
	
	/**
	 * Checks whether it has a parent model. 
	 * 
	 * @return  {@code true} if it has a parent model;
	 * {@code false} otherwise
	 */
	public boolean hasParent() {
		return model.getParent() != null;
	}
	
	/**
	 * Gets the dependencies defined in the self POM file. 
	 * 
	 * @return  the {@link Dependency} instances
	 */
	public List<Dependency> getSelfDependencies() {
		List<Dependency> deps = new ArrayList<>();
		// adds the dependencies
		DependencyManagement mgmt = model.getDependencyManagement();
		if(mgmt != null) {
			for(Dependency dep : mgmt.getDependencies()) {
				deps.add(update(dep, properties));
			}
		}
		if(model.getDependencies() != null) {
			for(Dependency dep : model.getDependencies()) {
				deps.add(update(dep, properties));
			}
		}
		// adds the depdendencies in the profile if exists
		if(model.getProfiles().isEmpty()) {
			return deps;
		}
		for(Profile profile : model.getProfiles()) {
			if(profile.getDependencies().isEmpty()) {
				continue;
			}
			Properties profileProp = new Properties();
			profileProp.putAll(properties);
			profileProp.putAll(profile.getProperties());
			for(Dependency dep : profile.getDependencies()) {
				deps.add(update(dep, profileProp));
			}
		}
		return deps;
	}
	
	/**
	 * Gets the remote repositories defined in the self POM file. 
	 * 
	 * @return  the {@link Repository} instances
	 */
	public List<Repository> getSelfRepositories() {
		List<Repository> repos = new ArrayList<>();
		// adds the remote repositories
		if(model.getRepositories() != null) {
			for(Repository repo : model.getRepositories()) {
				repos.add(update(repo, properties));
			}
		}
		if(model.getProfiles().isEmpty()) {
			return repos;
		}
		// adds the remote repositories defined in the profiles if exists
		for(Profile profile : model.getProfiles()) {
			if(profile.getRepositories().isEmpty()) {
				continue;
			}
			Properties profileProp = new Properties();
			profileProp.putAll(properties);
			profileProp.putAll(profile.getProperties());
			for(Repository repo : profile.getRepositories()) {
				repos.add(update(repo, profileProp));
			}
		}
		return repos;
	}
	
	/**
	 * Adds the properties defined in the self POM file. 
	 * 
	 * @return  the {@link Properties} instance
	 */
	public void addSelfProperties() {
		// adds the properties defined in the model
		properties.putAll(model.getProperties());
		// adds the properties defined in the profiles
		if(model.getProfiles() != null) {
			List<Profile> profiles = model.getProfiles();
			for(Profile profile : profiles) {
				properties.putAll(profile.getProperties());
			}
		}
		// add the inherent properties 
		properties.putAll(getInherentProperties());
		update(properties);
	}
	
	/**
	 * Gets custom properties in the {@code Model}.
	 * 
	 * @param model  the model of the pom file
	 * @return  the map of the custom properties
	 */
	private Map<String, String> getInherentProperties() {
		Map<String, String> properties = new HashMap<>();
		// add the base dir
		properties.put("project.basedir", pomFile.getParentFile().getAbsolutePath());
		properties.put("basedir", pomFile.getParentFile().getAbsolutePath());
		// adds the group id, artifact id and version
		if(isNotBlank(model.getGroupId())) {
			properties.put("groupId", model.getGroupId());
			properties.put("project.groupId", model.getGroupId());
			// for deprecated 
			properties.put("pom.groupId", model.getGroupId());
		}
		if(isNotBlank(model.getArtifactId())) {
			properties.put("artifactId", model.getArtifactId());
			properties.put("project.artifactId", model.getArtifactId());
			// for deprecated 
			properties.put("pom.artifactId", model.getArtifactId());
		}
		if(isNotBlank(model.getVersion())) {
			properties.put("version", model.getVersion());
			properties.put("project.version", model.getVersion());
			// for deprecated 
			properties.put("pom.version", model.getVersion());
		}
		if(isNotBlank(model.getName())) {
			properties.put("project.name", model.getName());
			// for deprecated 
			properties.put("pom.name", model.getName());
		}
		// add the project base url
		if(model.getProjectDirectory() != null) {
			properties.put("project.baseUri", model.getProjectDirectory().getAbsolutePath());
		}
		if(hasParent()) {
			// adds the parent version
			Parent parent = model.getParent();
			if(isNotBlank(parent.getVersion())) {
				properties.put("project.parent.version", parent.getVersion());
				properties.put("parent.version", parent.getVersion());
			}
		}
		// adds the properties defined in build 
		Build build = model.getBuild();
		if(build != null) {
			if(isNotBlank(build.getDirectory())) {
				properties.put("project.build.directory", build.getDirectory());
			}
			if(isNotBlank(build.getSourceDirectory())) {
				properties.put("project.build.sourceDirectory", 
						build.getSourceDirectory());
			}
			if(isNotBlank(build.getScriptSourceDirectory())) {
				properties.put("project.build.scriptSourceDirectory", 
						build.getScriptSourceDirectory());
			}
			if(isNotBlank(build.getTestSourceDirectory())) {
				properties.put("project.build.testSourceDirectory", 
						build.getTestSourceDirectory());
			}
			if(isNotBlank(build.getOutputDirectory())) {
				properties.put("project.build.outputDirectory", 
						build.getOutputDirectory());
			}
			if(isNotBlank(build.getTestOutputDirectory())) {
				properties.put("project.build.testOutputDirectory", 
						build.getTestOutputDirectory());
			}
		}
		return properties;
	}
	
	
	/**
	 * Updates the dependency version based on the parent dependency. 
	 * 
	 * @param dependency  the {@link Dependency} instance to be updated
	 */
	private void updateVersion(Dependency dependency) {
		POMModel parentModel = getParent();
		while(parentModel != null) {
			for(Dependency parentDep : parentModel.getDependencies()) {
				if(equalByGroupArtifactId(dependency, parentDep)) {
					if(!isBlank(parentDep.getVersion())) {
						dependency.setVersion(parentDep.getVersion());
						return;
					}
				}
			}
			parentModel = parentModel.getParent();
		}
		dependency.setVersion("LATEST");
	}
}
