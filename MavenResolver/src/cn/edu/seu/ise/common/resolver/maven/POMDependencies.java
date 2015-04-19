package cn.edu.seu.ise.common.resolver.maven;

import static cn.edu.seu.ise.common.resolver.maven.MavenHelper.getGroupArtifactId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

import cn.edu.seu.ise.common.resolver.aether.AetherDependencies;

/**
 * POM dependencies associated with corresponding repositories. 
 * 
 * @author Dong Qiu
 *
 */
public class POMDependencies {
	
	/** List of the dependencies */
	@Getter private List<Dependency> dependencies = new ArrayList<>();
	
	/** Corresponding remote repositories to resolve the dependencies */
	@Getter private List<Repository> repositories = new ArrayList<>();
	
	/** Set of unique repository urls */
	private Set<String> uniqueUrls = new HashSet<>();
	
	/**
	 * Adds a {@link Dependency} instance. 
	 * 
	 * @param dependency  the {@link Dependency} instance to be added
	 */
	public void addDependency(@NonNull Dependency dependency) {
		dependencies.add(dependency);
	}
	
	/**
	 * Adds a list of {@link Dependency} instances. 
	 * 
	 * @param dependency  the list of {@link Dependency} instances to be added
	 */
	public void addDependencies(@NonNull List<Dependency> deps) {
		dependencies.addAll(deps);
	}
	
	/**
	 * Adds a {@link Repository} instance. 
	 * 
	 * @param repository  the {@link Repository} instance to be added
	 */
	public void addRepository(@NonNull Repository repository) {
		String url = repository.getUrl();
		if(uniqueUrls.contains(url)) {
			return;
		}
		repositories.add(repository);
		uniqueUrls.add(url);
	}
	
	/**
	 * Adds a list of {@link Repository} instances. 
	 * 
	 * @param repos  the list of {@link Repository} instances to be added
	 */
	public void addRepositories(@NonNull List<Repository> repos) {
		repositories.addAll(repos);
	}
	
	/**
	 * Filters the {@link Dependency} instances by given set of dependency names. 
	 * 
	 * @param exclusions  the list of dependnecy names that should be excluded
	 * @return  the updated {@link POMDependencies}
	 */
	public POMDependencies filterBy(Set<String> exclusions) {
		List<Dependency> filtered = new ArrayList<>();
		for(Dependency dep : dependencies) {
			if(!exclusions.contains(getGroupArtifactId(dep))) {
				filtered.add(dep);
			}
		}
		dependencies = filtered;
		return this;
	}
	
	/**
	 * Converts the format of {@link POMDependencies} to {@link AetherDependencies}. 
	 * 
	 * @return  the {@link AetherDependencies} instance
	 */
	public AetherDependencies convert() {
		AetherDependencies bundle = new AetherDependencies();
		for(Dependency dependency : dependencies) {
			bundle.addDependency(MavenToAether.convert(dependency));
		}
		for(Repository repository : repositories) {
			bundle.addRemoteRepo(MavenToAether.convert(repository));
		}
		return bundle;
	}
}
