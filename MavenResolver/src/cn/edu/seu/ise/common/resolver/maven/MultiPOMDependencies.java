package cn.edu.seu.ise.common.resolver.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.Getter;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

import cn.edu.seu.ise.common.helper.StringHelper;
import cn.edu.seu.ise.common.helper.URLHelper;
import cn.edu.seu.ise.common.resolver.aether.AetherDependencies;

/**
 * Collection of POM dependncies. 
 * 
 * @author Dong Qiu
 *
 */
public class MultiPOMDependencies {
	
	/** List of the POM dependencies */
	@Getter private List<POMDependencies> pomDeps = new ArrayList<>();
	
	/** Url reachable status */
	private Map<String, Boolean> urlStatus = new HashMap<>();
	
	/** Set of unique dependency names */
	private Set<String> uniqueDeps = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Adds the {@link POMDependencies} instance. 
	 * 
	 * @param pomDep  the {@link POMDependencies} instance
	 */
	public void add(POMDependencies pomDep) {
		POMDependencies updatedPOMDep = new POMDependencies();
		// checks if the dependency is valid and not duplicated
		for(Dependency dep : pomDep.getDependencies()) {
			if(!MavenHelper.isValid(dep)) {
				continue;
			}
			String depName = MavenHelper.getName(dep);
			if(uniqueDeps.contains(depName)) {
				continue;
			}
			updatedPOMDep.addDependency(dep);
			uniqueDeps.add(depName);
		}
		// checks the repository if it is reachable 
		for(Repository repo : pomDep.getRepositories()) {
			if(StringHelper.isBlank(repo.getUrl())) {
				continue;
			}
			if(!urlStatus.containsKey(repo.getUrl())) {
				urlStatus.put(repo.getUrl(), URLHelper.isReachable(repo.getUrl()));
			} 
			if(urlStatus.get(repo.getUrl()).booleanValue()) {
				updatedPOMDep.addRepository(repo);
			}
		}
		pomDeps.add(updatedPOMDep);
	}
	
	/**
	 * Converts itself to the list of {@link AetherDependencies} format.
	 * 
	 * @return  the list of the {@link AetherDependencies} instances
	 */
	public List<AetherDependencies> convert() {
		List<AetherDependencies> multiple = new ArrayList<>();
		for(POMDependencies pomDep : pomDeps) {
			multiple.add(pomDep.convert());
		}
		return multiple;
	}
}
