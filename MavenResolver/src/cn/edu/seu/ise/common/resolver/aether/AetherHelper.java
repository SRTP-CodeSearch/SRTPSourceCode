package cn.edu.seu.ise.common.resolver.aether;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;


/**
 * A helper class that improve the use of Aether-related classes. 
 * 
 * @author Dong Qiu
 *
 */
public class AetherHelper {
	
	/**
	 * Clones the artifact by copying group id, artifact id, 
	 * extension and version. The classifier is ignored
	 * 
	 * @param artifact  the {@link Artifact} instance 
	 * @return  the cloned {@link Artifact} instance 
	 */
	public static Artifact deepCopy(Artifact artifact) {
		return new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), 
				artifact.getExtension(), artifact.getVersion());
	}
	
	/**
	 * Clones the base of the artifact. 
	 * <p>
	 * It only copies the group id, artifact id and extension of the artifact. 
	 * The version is set as [0,) to search all available version. </p>
	 * 
	 * @param artifact  the {@link Artifact} instance 
	 * @return  the cloned {@link Artifact} instance 
	 */
	public static Artifact baseCopy(Artifact artifact) {
		return new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), 
				"jar", "[0,)");
	}
	
	/**
	 * Gets the relative local path of the artifact. 
	 * 
	 * @param artifact  the {@link Artifact} instance
	 * @return  the relative path of the artifact
	 */
	public static String getPath(Artifact artifact) {
		String[] groupPaths = artifact.getGroupId().split("\\.");
		String artifactId = artifact.getArtifactId();
		String version = artifact.getVersion();
		StringBuilder builder = new StringBuilder();
		for(String groupPath : groupPaths) {
			builder.append(groupPath).append("/");
		}
		builder.append(artifactId).append("/").append(version).append("/");
		builder.append(artifactId).append("-").append(version).append(".")
			.append(artifact.getExtension());
		return builder.toString();
	}
}
