package cn.edu.seu.ise.common.resolver.aether;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class helps to store the list of dependencies and corresponding 
 * remote repositories to retrieve the dependencies. 
 *  
 * @author Dong Qiu
 *
 */
public class AetherDependencies {
	
	/** Logger */
	static Logger logger = LoggerFactory.getLogger(AetherDependencies.class.getName());
	
	/** List of the dependencies */
	@Getter private List<Dependency> dependencies = new ArrayList<>();
	
	/** Corresponding remote repositories to resolve the dependencies */
	@Getter private List<RemoteRepository> remoteRepos = new ArrayList<>();
	
	/**
	 * Adds a maven dependency and convert to the aether format. 
	 * 
	 * @param dependency  a maven dependency
	 */
	public void addDependency(@NonNull Dependency dependency) {
		dependencies.add(dependency);
	}
	
	/**
	 * Adds a maven remote repository. 
	 * 
	 * @param remoteRepo  the maven remote repository
	 */
	public void addRemoteRepo(RemoteRepository remoteRepo) {
		remoteRepos.add(remoteRepo);
	}
}
