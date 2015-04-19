package cn.edu.seu.ise.common.resolver.aether;

import static cn.edu.seu.ise.common.constants.Separator.COLON;
import static cn.edu.seu.ise.common.helper.StringHelper.EMPTY;
import static cn.edu.seu.ise.common.helper.StringHelper.concat;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.EQUAL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;

/**
 * The class helps to manage the resolved artifact. 
 * 
 * @author Dong Qiu
 *
 */
public class ResolvedArtifact {
	
	/** The resolved artifact */
	@Setter @Getter private Artifact current;
	
	/** Origin artifact is the alternative one is resolved */
	@Setter @Getter private Artifact original;
	
	/** The scope of the artifacts */
	@Setter @Getter private String scope = EMPTY;
	
	/** The status of the artifact resolution */
	@Setter @Getter private ResolveStatus status = EQUAL;
	
	/**
	 * Constructor. 
	 * 
	 * @param dependency  the {@link Dependency} instance to be resolved
	 */
	public ResolvedArtifact(Artifact artifact) {
		this.current = artifact;
		this.original = artifact;
	}
	
	/**
	 * Checks whether the artifact is resolved. 
	 * 
	 * @return  {@code true} if the artifact is resolved; 
	 * {@code false} otherwise
	 */
	public boolean isResolved() {
		return status != ResolveStatus.UNRESOLVED;
	}
	
	/**
	 * Gets the artifact file. 
	 * 
	 * @return  the artifact file
	 */
	public File getFile() {
		return current.getFile();
	}
	
	/**
	 * Gets the group id of the artifact. 
	 * 
	 * @return  the group id of the artifact
	 */
	public String getGId() {
		return current.getGroupId();
	}
	
	/**
	 * Gets the combined id (group and artifact id) of the artifact. 
	 * 
	 * @return  the combined id of the artifact
	 */
	public String getGAId() {
		return concat(COLON, current.getGroupId(), current.getArtifactId());
	}
	
	/**
	 * Gets the combined id (group, rtifact id and version) of the artifact. 
	 * 
	 * @return  the combined id of the artifact
	 */
	public String getGAVId() {
		return concat(COLON, current.getGroupId(), 
				current.getArtifactId(), current.getVersion());
	}
	
	/**
	 * Gets the combined id (group, rtifact id and version and extension) of the artifact. 
	 * 
	 * @return  the combined id of the artifact
	 */
	public String getGAVEId() {
		return concat(COLON, current.getGroupId(), 
				current.getArtifactId(), current.getVersion(), current.getExtension());
	}
	
	/**
	 * Gets the combined id (group, rtifact id and version, extension, classifier) of the artifact. 
	 * 
	 * @return  the combined id of the artifact
	 */
	public String getGAVECId() {
		return EMPTY.equals(current.getClassifier()) ? getGAVEId() : 
			concat(COLON, getGAVEId(), current.getClassifier());
	}
	
	/**
	 * Gets the record of the resolved artifact. 
	 * 
	 * @return  the record of the artifact
	 */
	public List<String> getRecord() {
		List<String> record = new ArrayList<>();
		record.add(current.getGroupId());
		record.add(current.getArtifactId());
		record.add(current.getExtension());
		record.add(current.getClassifier());
		record.add(current.getVersion());
		return record;
	}
}
