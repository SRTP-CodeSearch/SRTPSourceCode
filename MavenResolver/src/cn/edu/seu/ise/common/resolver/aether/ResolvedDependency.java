package cn.edu.seu.ise.common.resolver.aether;

import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.ALTERNATIVE;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.UNRESOLVED;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;

/**
 * The class helps to manage the resolved artifact. 
 * 
 * @author Dong Qiu
 *
 */
public class ResolvedDependency {
	
	/** The resolved dependency */
	@Getter private Dependency current;
	
	/** The origin dependency */
	@Getter @Setter private Dependency original;
	
	/** The resolved transitive artifacts (may contain artifacts that are resolved uncorrently)*/
	@Getter private List<ResolvedArtifact> artifacts = new ArrayList<>();
	
	/** The size of resolved artifacts */
	@Getter private int sizeOfResolvedArtifacts = 0;
	
	/** The size of unresolved artifacts */
	@Getter private int sizeOfUnresolvedArtifacts = 0;
	
	/** The status of the dependency resolution */
	@Getter @Setter private ResolveStatus status;
	
	/**
	 * Constructor. 
	 * 
	 * @param dependency  the {@link Dependency} instance to be resolved
	 */
	public ResolvedDependency(Dependency dependency) {
		this.current = dependency;
		this.original = dependency;
	}
	
	/**
	 * Checks whether the dependency is resolved. 
	 * 
	 * @return  {@code true} if the dependency is resolved; 
	 * {@code false} otherwise
	 */
	public boolean isResolved() {
		return status != UNRESOLVED;
	}
	
	/**
	 * Checks whether the dependency is resolved alternatively. 
	 * 
	 * @return  {@code true} if the dependency is resolved alternatively;
	 * {@code false} otherwise
	 */
	public boolean isAlternative() {
		return status == ALTERNATIVE;
	}
	
	/**
	 * Adds a {@link ResolvedArtifact} instance.
	 * 
	 * @param artifact  the {@link ResolvedArtifact} instance
	 */
	public void addArtifact(@NonNull ResolvedArtifact artifact) {
		// sets the scope of the artifact
		artifact.setScope(current.getScope());
		artifacts.add(artifact);
		if(artifact.isResolved()) {
			sizeOfResolvedArtifacts ++;
		} else {
			sizeOfUnresolvedArtifacts ++;
		}
	}
	
	/**
	 * Adds a {@link Artifact} instance (has been resolved). 
	 * 
	 * @param artifact  the {@link Artifact} instance
	 */
	public void addArtifact(@NonNull Artifact artifact) {
		ResolvedArtifact resolved = new ResolvedArtifact(artifact);
		addArtifact(resolved);
	}
	
	/**
	 * Gets the main {@link ResolvedArtifact} instance. 
	 * 
	 * @return  the main artifact that corresponds to the dependency 
	 */
	public ResolvedArtifact getMain() {
		return artifacts.get(0);
	}
	
	/**
	 * Gets the list of resolved dependency files. 
	 *  
	 * @return  the list of resolved dependency files. 
	 */
	public List<File> getResolvedFiles() {
		List<File> files = new ArrayList<>();
		for(ResolvedArtifact artifact : artifacts) {
			if(artifact.isResolved()) {
				files.add(artifact.getFile());
			}
		}
		return files;
	}
	
	/**
	 * Gets the record of the resolved alternative dependency. 
	 * 
	 * @return  the record
	 */
	public List<String> getAlternativeRecord() {
		List<String> record = getMain().getRecord();
		if(original != null) {
			record.add(original.getArtifact().getVersion());
		}
		return record;
	}
}
