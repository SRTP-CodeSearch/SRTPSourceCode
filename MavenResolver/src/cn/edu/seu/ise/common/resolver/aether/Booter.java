package cn.edu.seu.ise.common.resolver.aether;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

/**
 * Aether repository system booter. 
 * 
 * @author Dong Qiu
 *
 */
public class Booter {
	
	/**
	 * Generate a new {@link RepositorySystem} instance. 
	 * 
	 * @return  the {@link RepositorySystem} instance
	 */
	public static RepositorySystem newRepoSystem() {
		return RepoSystemFactory.newRepositorySystem();
	}

	/**
	 * Creates a new repository system session. 
	 * 
	 * @param system  the repository system
	 * @param localRepoPath  the path of the local repository
	 * @return
	 */
	public static DefaultRepositorySystemSession newRepoSystemSession(RepositorySystem system, 
			File localRepoFile) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		LocalRepository localRepo = new LocalRepository(localRepoFile);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
		//session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepoListener());
		return session;
	}
	
	/**
	 * Creates a remote repository
	 * 
	 * @param id  the id of the remote repository
	 * @param type  the type of the remote repository
	 * @param url  the url of the remote repository
	 * @return  the corresponding remote repository
	 */
	public static RemoteRepository newRemoteRepo(String id, String type, String url) {
		return new RemoteRepository.Builder(id, type, url).build();
	}
	
	/**
	 * Creates a remote repository
	 * 
	 * @param id  the id of the remote repository
	 * @param type  the type of the remote repository
	 * @param url  the url of the remote repository
	 * @param releasePolicy  the release policy
	 * @param snapshotPolicy  the snapshot policy
	 * 
	 * @return  the corresponding remote repository
	 */
	public static RemoteRepository newRemoteRepo(String id, String type, String url, 
			RepositoryPolicy releasePolicy, RepositoryPolicy snapshotPolicy) {
		return new RemoteRepository.Builder(id, type, url).setReleasePolicy(releasePolicy)
				.setSnapshotPolicy(snapshotPolicy).build();
	}
	
	/**
	 * Gets the default central repository. 
	 * 
	 * @return  the central repository
	 */
	public static RemoteRepository newCentralRepo() {
		return newRemoteRepo("central", "default", "http://repo1.maven.apache.org/maven2");
	}
	
	/**
	 * Gets the list of {@link RemoteRepository} instances.
	 * 
	 * @return  the list of {@link RemoteRepository} instances
	 */
	public static List<RemoteRepository> newDefaultRepos() {
		List<RemoteRepository> remoteRepos = new ArrayList<>();
		remoteRepos.add(Booter.newCentralRepo());
		remoteRepos.add(Booter.newRemoteRepo("spring", "default", 
				"http://repo.spring.io/repo"));
		return remoteRepos;
	}
}
