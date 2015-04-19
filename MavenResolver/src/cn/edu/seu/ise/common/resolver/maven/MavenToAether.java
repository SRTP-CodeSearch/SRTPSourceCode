package cn.edu.seu.ise.common.resolver.maven;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import org.apache.maven.model.Repository;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

import cn.edu.seu.ise.common.constants.Extensions;

/**
 * The converter that transform objects in maven model API to objects in eclipse aether API. 
 * 
 * @author Dong Qiu
 *
 */
public class MavenToAether {
	
	/**
	 * Converts maven dependency to aether dependency. 
	 * 
	 * @param mavenDep  the maven dependency
	 * @return  the aether dependency, can be null if the version of dependency is null
	 */
	public static Dependency convert(@NonNull org.apache.maven.model.Dependency mavenDep) {
		Artifact artifact = new DefaultArtifact(mavenDep.getGroupId(), mavenDep.getArtifactId(), 
				mavenDep.getType(), mavenDep.getVersion());
		if(mavenDep.getExclusions() == null || mavenDep.getExclusions().size() == 0) {
			return new Dependency(artifact, mavenDep.getScope(), mavenDep.isOptional());
		}
		// if the maven dependency has the exclusion
		List<Exclusion> exclusions = new ArrayList<>();
		for(org.apache.maven.model.Exclusion mavenExclusion : mavenDep.getExclusions()) {
			exclusions.add(convert(mavenExclusion));
		}
		return new Dependency(artifact, mavenDep.getScope(), mavenDep.isOptional(), exclusions);
	}
	
	/**
	 * Converts maven exclusion to aether exclusion.
	 * 
	 * @param mavenExclusion  the maven exclusion
	 * @return
	 */
	public static Exclusion convert(@NonNull org.apache.maven.model.Exclusion mavenExclusion) {
		return new Exclusion(mavenExclusion.getGroupId(), mavenExclusion.getArtifactId(), 
				null, Extensions.JAR);
	}
	
	/**
	 * Converts maven repository to aether remote repository. 
	 * 
	 * @param repository  the maven repository
	 * @return  the aether remote repository
	 */
	public static RemoteRepository convert(@NonNull Repository repository) {
		RemoteRepository.Builder builder = new RemoteRepository.Builder(repository.getId(), 
				repository.getLayout(), repository.getUrl());
		if(repository.getReleases() != null) {
			builder.setReleasePolicy(convert(repository.getReleases()));
		}
		if(repository.getSnapshots() != null) {
			builder.setReleasePolicy(convert(repository.getSnapshots()));
		}
		return builder.build();
	}
	
	/**
	 * Convert maven repository policy to aether repository policy.
	 * 
	 * @param policy  the maven repository policy
	 * @return  the aether repository policy
	 */
	public static RepositoryPolicy convert(org.apache.maven.model.RepositoryPolicy policy) {
		String enable = policy.getEnabled();
		boolean isEnable = enable != null ? Boolean.parseBoolean(enable) : true;
		return new RepositoryPolicy(isEnable, policy.getUpdatePolicy(), 
				policy.getChecksumPolicy());
	}
}
