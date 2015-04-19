package cn.edu.seu.ise.common.resolver.aether;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository listener that logs events to the console. 
 * 
 * @author Dong Qiu
 *
 */
public class ConsoleRepoListener extends AbstractRepositoryListener {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(ConsoleRepoListener.class.getName());
	
	@Override
	public void artifactResolved(RepositoryEvent event) {
		logger.info("Resolved artifact {}", event.getArtifact());
	}
}
