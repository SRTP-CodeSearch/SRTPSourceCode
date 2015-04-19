package cn.edu.seu.ise.common.resolver;

import java.io.File;
import java.util.List;

import cn.edu.seu.ise.api.use.model.DepUseProfile;
import cn.edu.seu.ise.common.model.Snapshot;

/**
 * The interface of the resolver. 
 * 
 * @author Dong Qiu
 *
 */
public interface Resolver {
	/**
	 * Resolves dependencies from the {@link Snapshot} instance. 
	 * 
	 * @param Snapshot  the {@link Snapshot} instance
	 * @return  the list of dependency files
	 */
	public List<File> getDependencies(Snapshot snapshot);
	
	/**
	 * Gets inner dependencies from the snapshot. 
	 * 
	 * @param snapshot  the snapshot to be resolved
	 * @return  the list of inner dependency files
	 */
	public List<File> getInnerDependencies(Snapshot snapshot);
	
	/**
	 * Gets external dependencies. 
	 */
	public List<File> getExternalDependencies();

	/**
	 * Resolves dependencies from the {@link Snapshot} instance. 
	 * 
	 * @param snapshot  the {@link Snapshot} instance
	 * @return  the {@link DepUseProfile} instance
	 */
	public DepUseProfile resolve(Snapshot snapshot);
}
