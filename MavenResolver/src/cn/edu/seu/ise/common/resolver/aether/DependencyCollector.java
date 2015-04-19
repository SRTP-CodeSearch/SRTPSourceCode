package cn.edu.seu.ise.common.resolver.aether;

import static cn.edu.seu.ise.common.model.BuildType.BUILDR;
import static cn.edu.seu.ise.common.model.BuildType.GRADLE;
import static cn.edu.seu.ise.common.model.BuildType.IVY;
import static cn.edu.seu.ise.common.model.BuildType.LEININGEN;
import static cn.edu.seu.ise.common.model.BuildType.MAVEN;
import static cn.edu.seu.ise.common.model.BuildType.SBT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.seu.ise.common.model.Snapshot;
import cn.edu.seu.ise.common.resolver.maven.MavenLoader;

/**
 * The dependency collector. 
 * 
 * @author Dong Qiu
 *
 */
public class DependencyCollector {
	
	/**
	 * Collects list of the {@link AetherDependencies} instances from the 
	 * {@link Snapshot} instance. 
	 * 
	 * @param snapshot  the {@link Snapshot} instance
	 * @return  the list of {@link AetherDependencies} instances
	 */
	public List<AetherDependencies> collectBundles(Snapshot snapshot) {
		List<AetherDependencies> aetherDeps = new ArrayList<>();
		if(snapshot.buildBy(MAVEN)) {
			aetherDeps.addAll(collectFromMaven(snapshot.getBuildFiles(MAVEN)));
		}
		if(snapshot.buildBy(IVY)) {
			aetherDeps.addAll(collectFromIVY(snapshot.getBuildFiles(IVY)));
		}
		if(snapshot.buildBy(GRADLE)) {
			aetherDeps.addAll(collectFromGradle(snapshot.getBuildFiles(GRADLE)));
		}
		if(snapshot.buildBy(SBT)) {
			aetherDeps.addAll(collectFromSBT(snapshot.getBuildFiles(SBT)));
		}
		if(snapshot.buildBy(LEININGEN)) {
			aetherDeps.addAll(collectFromLeiningen(snapshot.getBuildFiles(LEININGEN)));
		}
		if(snapshot.buildBy(BUILDR)) {
			aetherDeps.addAll(collectFromBuildr(snapshot.getBuildFiles(BUILDR)));
		}
		return aetherDeps;
	}
	
	/**
	 * Collects dependency bundles from maven build files. 
	 * 
	 * @param files  the list of maven build files
	 * @return
	 */
	public List<AetherDependencies> collectFromMaven(List<File> files) {
		MavenLoader loader = new MavenLoader(files);
		return loader.getMultiPOMDependencies().convert();
	}
	
	/**
	 * Collects dependency bundles from ivy build files. 
	 * 
	 * @param files  the list of the ivy build files
	 * @return
	 */
	public List<AetherDependencies> collectFromIVY(List<File> files) {
		// extract dependency from ivy files
		return new ArrayList<>();
	}
	
	/**
	 * Collects dependency bundles from gradle build files. 
	 * 
	 * @param files  the list of the gradle build files
	 * @return
	 */
	public List<AetherDependencies> collectFromGradle(List<File> files) {
		// extract dependency from gradle files
		return new ArrayList<>();
	}
	
	/**
	 * Collects dependency bundles from sbt build files. 
	 * 
	 * @param files  the list of the sbt build files
	 * @return
	 */
	public List<AetherDependencies> collectFromSBT(List<File> files) {
		// extract dependency from sbt files
		return new ArrayList<>();
	}
	
	/**
	 * Collects dependency bundles from leiningen build files. 
	 * 
	 * @param files  the list of the leiningen build files
	 * @return
	 */
	public List<AetherDependencies> collectFromLeiningen(List<File> files) {
		// extract dependency from leiningen files
		return new ArrayList<>();
	}
	
	/**
	 * Collects dependency bundles from buildr build files. 
	 * 
	 * @param files  the list of the buildr build files
	 * @return
	 */
	public List<AetherDependencies> collectFromBuildr(List<File> files) {
		// extract dependency from leiningen files
		return new ArrayList<>();
	}
}
