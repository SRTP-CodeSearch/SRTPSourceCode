package cn.edu.seu.ise.common.resolver.maven;

import static cn.edu.seu.ise.common.helper.StringHelper.isBlank;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.zip.DataFormatException;

import lombok.NonNull;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

/**
 * The maven helper. 
 * 
 * @author Dong Qiu
 *
 */
public class MavenHelper {
	
	/**
	 * Creates a maven {@link Repository} instance. 
	 * 
	 * @param id  the id of the repository
	 * @param layout  the layout of the repository
	 * @param url  the url of the repository
	 * @return  the {@link Repository} instance
	 */
	public static Repository newRepo(String id, String layout, String url) {
		Repository repository = new Repository();
		repository.setId(id);
		repository.setLayout(layout);
		repository.setUrl(url);
		return repository;
	}
	
	/**
	 * Creates a central {@link Repository} instance. 
	 * 
	 * @return  the central {@link Repository} instance
	 */
	public static Repository newCentralRepo() {
		return newRepo("central", "default", "http://repo1.maven.apache.org/maven2");
	}
	
	/**
	 * Updates the properties by the referenced properties. 
	 *  
	 * @param base  the base properties
	 * @param referenced  the referenced properties
	 * @return  the updated base properties
	 */
	public static Properties update(Properties base, Properties referenced) {
		for(String key : base.stringPropertyNames()) {
			if(base.getProperty(key).contains("${")) {
				base.setProperty(key, replaceGradually(base.getProperty(key), referenced));
			}
		}
		return base;
	}
	
	/**
	 * Updates the properties by self. 
	 * 
	 * @param properties  the properties to be updated
	 * @return  the updated properties
	 */
	public static Properties update(Properties properties) {
		for(String key : properties.stringPropertyNames()) {
			if(properties.getProperty(key).contains("${")) {
				properties.setProperty(key, replaceGradually(properties.getProperty(key), properties));
			}
		}
		return properties;
	}
	
	/**
	 * Updates the dependency if the value is defined by variable. 
	 * <p>
	 * The variable ${v} is defined by properties in maven model. 
	 * </p>
	 * 
	 * @param dependency  the {@link Dependency} instance
	 * @param properties  the properties
	 * @return  the updated {@link Dependency} instance
	 */
	public static Dependency update(Dependency dependency, Properties properties) {
		String groupId = dependency.getGroupId();
		if(groupId != null && groupId.contains("${")) {
			dependency.setGroupId(replaceGradually(groupId, properties));
		}
		String artifactId = dependency.getArtifactId();
		if(artifactId != null && artifactId.contains("${")) {
			dependency.setArtifactId(replaceGradually(artifactId, properties));
		}
		String versionId = dependency.getVersion();
		if(versionId != null && versionId.contains("${")) {
			dependency.setVersion(replaceGradually(versionId, properties));
		}
		return dependency;
	}
	
	/**
	 * Updates the dependency if the value is defined by variable. 
	 * <p>
	 * The variable ${v} is defined by properties in maven model. 
	 * </p>
	 * 
	 * @param repository  the {@link Repository} instance
	 * @param properties  the properties
	 * @return  the updated {@link Repository} instance
	 */
	public static Repository update(Repository repository, Properties properties) {
		String url = repository.getUrl();
		if(url != null && url.contains("${")) {
			// make sure the url is unified
			String updatedUrl = replaceGradually(url, properties)
					.replaceAll("\\\\", Matcher.quoteReplacement("/"));
			repository.setUrl(updatedUrl);
		}
		return repository;
	}
	
	/**
	 * Updates the string variable guadually if referenced value is appied. 
	 * 
	 * @param variable  the string to be updated
	 * @param properties  the referenced properties
	 * 
	 * @return  the updated variable
	 */
	public static String replaceGradually(String variable, Properties properties) {
		int loop = 0;
		String backup = null;
		while(variable.contains("${") && loop < 5) {
			backup = variable;
			try {
				variable = replaceOnce(variable, properties);
				// to avoid the unlimited loops
				if(variable.contains(backup)) {
					return backup;
				}
				loop ++;
			} catch (DataFormatException e) {
				//logger.warn(e.getMessage());
				return variable;
			}
		}
		return variable;
	}
	
	/**
	 * Updates the string variable if referenced value is appied. 
	 * <p>The scan of the string variable is performed once. </p>
	 * 
	 * @param variable  the string to be updated
	 * @param properties  the referenced properties
	 * 
	 * @return  the updated string variable
	 * @throws DataFormatException
	 */
	public static String replaceOnce(String variable, Properties properties) 
			throws DataFormatException {
		StringBuilder builder = new StringBuilder();
		int start = variable.indexOf("${");
		int end = 0;
		if(start < 0) {
			return variable;
		}
		if(start > 0) {
			builder.append(variable.substring(0, start));
		}
		while(start >= 0) {
			end = variable.indexOf("}", start);
			if(end < start + 2) {
				throw new DataFormatException("The properties is not well-defined.");
			}
			String prop = variable.substring(start + 2, end);
			if(!properties.containsKey(prop)) {
				throw new DataFormatException("The properties " + prop + " is not defined.");
			}
			builder.append(properties.getProperty(prop));
			start = variable.indexOf("${", end);
			if(start >= 0 && end + 1 <= start) {
				builder.append(variable.substring(end + 1, start));
			}
		}
		if(end < variable.length() - 1) {
			builder.append(variable.substring(end + 1));
		}
		return builder.toString();
	}
	
	/**
	 * Gets the fully qualified name of the {@link Dependency} instance. 
	 * <p>
	 * The format of the basic info is groupId : artifactId : versionId. 
	 * If the value of any component is null, it will replaced by "[null]". 
	 * </p>
	 * 
	 * @param dependency  the maven dependency
	 * @return  the dependency name
	 */
	public static String getName(@NonNull Dependency dependency) {
		StringBuilder builder = new StringBuilder();
		builder.append(dependency.getGroupId() == null ? 
				"[unresolved]" : dependency.getGroupId()).append(":");
		builder.append(dependency.getArtifactId() == null ? 
				"[unresolved]" : dependency.getArtifactId()).append(":");
		builder.append(dependency.getType()).append(":");
		builder.append(dependency.getVersion() == null ? 
				"[unresolved]" : dependency.getVersion()).append(":");
		return builder.toString();
	}
	
	/**
	 * Gets the simplified name of the {@link Dependency} instance. 
	 * <p>
	 * The format of the basic info is groupId : artifactId. 
	 * If the value of any component is null, it will replaced by "[null]". 
	 * </p>
	 * 
	 * @param dependency  the {@link Dependency} instance
	 * @return  the simplified dependency name
	 */
	public static String getGroupArtifactId(@NonNull Dependency dependency) {
		StringBuilder builder = new StringBuilder();
		builder.append(dependency.getGroupId() == null ? 
				"[unresolved]" : dependency.getGroupId()).append(":");
		builder.append(dependency.getArtifactId() == null ? 
				"[unresolved]" : dependency.getArtifactId());
		return builder.toString();
	}
	
	/**
	 * Checks if the two dependencies are equal by their group id and artifact id. 
	 * 
	 * @param one  one {@link Dependency} instance
	 * @param another  another {@link Dependency} instance
	 * @return
	 */
	public static boolean equalByGroupArtifactId(@NonNull Dependency one, 
			@NonNull Dependency another) {
		return getGroupArtifactId(one).equalsIgnoreCase(getGroupArtifactId(another));
	}
	
	/**
	 * Checks whether the maven dependency is valid. 
	 * 
	 * @param dependency  the maven dependency
	 * @return  {@code true} if the dependency is valid; 
	 * {@code false} otherwise
	 */
	public static boolean isValid(@NonNull Dependency dependency) {
		return isValidId(dependency.getGroupId()) && 
				isValidId(dependency.getArtifactId());
	}
	
	/**
	 * Checks whether the id of the dependency element is valid. 
	 * 
	 * @param value  the id value
	 * @return  {@code true} if the id is valid; 
	 * {@code false} otherwise
	 */
	public static boolean isValidId(String value) {
		if(isBlank(value)) {
			return false;
		}
		if(value.contains("$") || value.contains("{") ||
				value.contains("}") || value.contains("\\") ||
				value.contains("@") || value.contains("/")) {
			return false;
		}
		return true;
	}
}
