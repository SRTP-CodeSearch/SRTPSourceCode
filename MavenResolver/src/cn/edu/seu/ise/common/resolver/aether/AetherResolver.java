package cn.edu.seu.ise.common.resolver.aether;

import static cn.edu.seu.ise.common.constants.Extensions.JAR;
import static cn.edu.seu.ise.common.helper.FileHelper.getDir;
import static cn.edu.seu.ise.common.resolver.aether.AetherHelper.baseCopy;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.ALTERNATIVE;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.EQUAL;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.PARTIAL;
import static cn.edu.seu.ise.common.resolver.aether.ResolveStatus.UNRESOLVED;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.seu.ise.api.use.model.DepUseProfile;
import cn.edu.seu.ise.common.helper.FileHelper;
import cn.edu.seu.ise.common.helper.StringHelper;
import cn.edu.seu.ise.common.model.Snapshot;
import cn.edu.seu.ise.common.resolver.Resolver;


/**
 * The aether dependency resolver. 
 * 
 * @author Dong Qiu
 *
 */
public class AetherResolver implements Resolver {
	
	/** Logger */
	static Logger logger = LoggerFactory.getLogger(AetherResolver.class.getName());
	
	/** Repository system */
	private RepositorySystem system;
	
	/** Repository system session */
	private RepositorySystemSession session;
	
	/** Dependency collector */
	private DependencyCollector collector;
	
	/** Local repository path */
	private String localRepoPath;
	
	/** Directory of the external dependencies */
	private String externalDepDir;
	
	/** The current resolver */
	private static AetherResolver instance;
	
	/**
	 * Constructor. 
	 * 
	 * @param localRepoPath  the local repository path
	 */
	public AetherResolver(String localRepoPath) {
		this.localRepoPath = localRepoPath;
		system = Booter.newRepoSystem();
	    session = Booter.newRepoSystemSession(system, new File(localRepoPath));
	    collector = new DependencyCollector();
	    instance = this;
	}
	
	/**
	 * Gets the {@link AetherResolver} instance. 
	 * 
	 * @return  the {@link AetherResolver} instance
	 */
	public static AetherResolver getResolver() {
		if(instance == null) {
			File localRepo = getDir(getDir(System.getProperty("user.dir")), "Repos");
			logger.info("Using default local repository: {}", localRepo.getAbsolutePath());
			return new AetherResolver(localRepo.getAbsolutePath());
		}
		return instance;
	}
	
	@Override
	public List<File> getDependencies(Snapshot snapshot) {
		List<File> dependencyFiles = new ArrayList<>();
		DepUseProfile result = resolve(snapshot);
		// adds the resolved dependency files
		dependencyFiles.addAll(result.getFiles());
		// adds the inner dependency files
		dependencyFiles.addAll(getInnerDependencies(snapshot));
		// adds the external dependency files
		dependencyFiles.addAll(getExternalDependencies());
		return dependencyFiles;
	}
	
	@Override
	public List<File> getInnerDependencies(Snapshot snapshot) {
		return FileHelper.listSubFiles(snapshot.getSourceDir(), new String[]{JAR}, true);
	}
	
	@Override
	public List<File> getExternalDependencies() {
		if(StringHelper.isBlank(externalDepDir)) {
			return new ArrayList<>();
		}
		File file = new File(externalDepDir);
		if(!file.exists() || !file.isDirectory()) {
			return new ArrayList<>();
		}
		return FileHelper.listSubFiles(new File(externalDepDir), new String[]{JAR}, true);
	}
	
	/**
	 * Resolves a {@link Snapshot} instance. 
	 * 
	 * @param snapshot  the {@link Snapshot} instance to be resolved
	 * @return  the {@link DepUseProfile} instance
	 */
	@Override
	public DepUseProfile resolve(@NonNull Snapshot snapshot) {
		List<AetherDependencies> aetherDeps = collector.collectBundles(snapshot);
		DepUseProfile profile = new DepUseProfile(snapshot.getProjName());
		for(AetherDependencies aetherDep : aetherDeps) {
			for(Dependency dependency : aetherDep.getDependencies()) {
				profile.add(resolveSmartly(dependency, aetherDep.getRemoteRepos()));
			}
		}
		return profile;
	}
	
	/**
	 * Resolves the dependency smartly.  
	 * <p>
	 * We firstly resolve the dependency directly. If it fail, 
	 * we manually collect all the artifacts that belong to the 
	 * dependency tree and resolve them one by one. </p>
	 * 
	 * @param dependency  the {@link Dependency} instance to be resolved
	 * @param remoteRepos  the list of remote repositories
	 * @return  the {@link ResolvedDependency} instance
	 */
	public ResolvedDependency resolveSmartly(@NonNull Dependency dependency, 
			@NonNull List<RemoteRepository> remoteRepos)  {
		ResolvedDependency resolved = null;
		try {
			resolved = resolveTransitively(dependency, remoteRepos);
			resolved.setStatus(EQUAL);
			logger.info("Resolved dependency {} transitively", dependency);
			return resolved;
		} catch (RepositoryException e) {
			// add the local repo and resolve the alternative dependency 
			resolved = resolveAlternative(dependency, remoteRepos);
			if(resolved != null) {
				resolved.setStatus(ALTERNATIVE);
				resolved.setOriginal(dependency);
				logger.info("Resolved dependency {} (instead of {}) alternatively", 
						resolved.getCurrent(), dependency);
				return resolved;
			}
			// resolve the dependency only (the transitive dependencies are not included)
			resolved = new ResolvedDependency(dependency);
			ResolvedArtifact artifact = resolveSmartly(dependency.getArtifact(), remoteRepos);
			resolved.addArtifact(artifact);
			if(artifact.isResolved()) {
				resolved.setStatus(PARTIAL);
				logger.info("Resolved dependency {} partially", dependency);
			} else {
				resolved.setStatus(UNRESOLVED);
				logger.warn("Fail to resolve dependency {}", dependency);
			}
			return resolved;
		}
	}
	
	/**
	 * Resolves a {@link Dependency} instance transitively. 
	 * 
	 * @param dependency  the {@link Dependency} to be resolved
	 * @param remoteRepos  the list of candidate remote repositories
	 * @return  the {@link ResolvedDependency} instance
	 * @throws RepositoryException 
	 */
	public ResolvedDependency resolveTransitively(@NonNull Dependency dependency, 
			@NonNull List<RemoteRepository> remoteRepos) throws RepositoryException {
		ResolvedDependency resolved = new ResolvedDependency(dependency);
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.setRepositories(remoteRepos);
		DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, null);
		DependencyResult depResult = null;
		try {
			depResult = system.resolveDependencies(session,dependencyRequest);
			for(ArtifactResult result : depResult.getArtifactResults()) {
				resolved.addArtifact(result.getArtifact());
			}
			return resolved;
		} catch(Exception e) {
			throw new RepositoryException("Fail to resolve the dependency " 
					+ dependency + " transitively");
		}
	}
	
	/**
	 * Resolves a {@link Dependency} instance alternatively in given list 
	 * of candidate remote repositories. 
	 * <p>
	 * It searches the newest version of the dependency and resolves it
	 * transitively. </p>
	 * 
	 * @param dependency  the {@link Dependency} to be resolved
	 * @param remoteRepos  the list of candidate remote repositories
	 * @return  the {@link ResolvedDependency} instance
	 * @throws RepositoryException 
	 */
	public ResolvedDependency resolveAlternative(@NonNull Dependency dependency, 
			@NonNull List<RemoteRepository> remoteRepos)  {
		ResolvedDependency resolved = null;
		for(RemoteRepository remoteRepo : remoteRepos) {
			try {
				resolved = resolveAlternative(dependency, remoteRepo);
				return resolved;
			} catch (RepositoryException e) {
				/*logger.warn("Fail to reslove dependency {} from {}, \n{}", 
						dependency, remoteRepo.getUrl(), Throwables.getStackTraceAsString(e));*/
			}
		}
		return resolved;
	}
	
	/**
	 * Resolves a {@link Dependency} instance alternatively in a given candidate
	 * remote repository. 
	 * <p>
	 * It searches the newest version of the dependency and resolves it
	 * transitively. </p>
	 * 
	 * @param dependency  the {@link Dependency} to be resolved
	 * @param remoteRepos  the candidate remote repositories
	 * @return  the {@link ResolvedDependency} instance
	 * @throws RepositoryException 
	 */
	public ResolvedDependency resolveAlternative(@NonNull Dependency dependency, 
			RemoteRepository remoteRepo) throws RepositoryException  {
		Artifact baseArtifact = baseCopy(dependency.getArtifact());
		List<RemoteRepository> remoteRepos = new ArrayList<>();
		remoteRepos.add(remoteRepo);
		VersionRangeRequest rangeRequest = new VersionRangeRequest();
		rangeRequest.setArtifact(baseArtifact);
		rangeRequest.setRepositories(remoteRepos);
		VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
		Version newestVersion = rangeResult.getHighestVersion();
		if(newestVersion == null) {
			throw new VersionRangeResolutionException(rangeResult);
		}
		Dependency updateDependnecy = dependency.setArtifact(baseArtifact.setVersion(newestVersion.toString()));
		return resolveTransitively(updateDependnecy, remoteRepos);
	}
	
	/**
	 * Resolves a {@link Dependency} instance manually. 
	 * 
	 * @param dependency  the {@link Dependency} to be resolved
	 * @param remoteRepos   the list of candidate remote repositories
	 * @return  the {@link ResolvedDependency} instance
	 */
	public ResolvedDependency resolveManually(@NonNull Dependency dependency, 
			@NonNull List<RemoteRepository> remoteRepos) {
		ResolvedDependency resolved = new ResolvedDependency(dependency);
		List<Artifact> transitives = null;
		try {
			transitives = getTree(dependency, remoteRepos);
			for(Artifact transitive : transitives) {
				resolved.addArtifact(resolveSmartly(transitive, remoteRepos));
			}
			return resolved;
		} catch (DependencyCollectionException e) {
			logger.warn("Fail to get dependency tree of {}", dependency);
			ResolvedArtifact defaultArtifact = new ResolvedArtifact(dependency.getArtifact());
			defaultArtifact.setStatus(UNRESOLVED);
			resolved.addArtifact(defaultArtifact);
			resolved.setStatus(UNRESOLVED);
			return resolved;
		}
	}
	
	/**
	 * Gets list of transitive {@link Artifact} instances from {@link Dependency}.
	 * 
	 * @param dependency  the {@link Dependency} instance
	 * @param remoteRepos  list of {@link RemoteRepository} instances
	 * @return  the list of {@link Artifact} instances
	 * @throws DependencyCollectionException
	 */
	public List<Artifact> getTree(@NonNull Dependency dependency, 
			@NonNull List<RemoteRepository> remoteRepos) 
					throws DependencyCollectionException  {
		final List<Artifact> artifacts = new ArrayList<>();
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.setRepositories(remoteRepos);
		CollectResult collectResult = system.collectDependencies(session, collectRequest);
		collectResult.getRoot().accept(new DependencyVisitor() {
			@Override
			public boolean visitLeave(DependencyNode node) {
				// do nothing
				return true;
			}
			@Override
			public boolean visitEnter(DependencyNode node) {
				artifacts.add(node.getArtifact());
				return true;
			}
		});
		return artifacts;
	}
	
	/**
	 * Resolves an artifact smartly. 
	 * <p>
	 * It firstly tries to resolve the artifact directly. If it fails, 
	 * it tries to re-resolve the artifact by removing interferences 
	 * (e.g. classifier). If it also fails, we resolves the alternative
	 * artifacts (with the same group id, artifact id and the newest 
	 * version) finally. 
	 * </p>
	 * 
	 * @param artifact  the artifact to be resolved
	 * @param remoteRepos  the list of remote repositories
	 * @return  the {@link ResolvedArtifact} instance
	 */
	public ResolvedArtifact resolveSmartly(@NonNull Artifact artifact, 
			@NonNull List<RemoteRepository> remoteRepos) {
		ResolvedArtifact resolved = null;
		try {
			resolved = resolve(artifact, remoteRepos);
			logger.info("Resolved artifact {}", artifact.toString());
			return resolved;
		} catch (ArtifactResolutionException e) {
			resolved = resolveAlternative(artifact, remoteRepos);
			if(resolved != null) {
				resolved.setStatus(ALTERNATIVE);
				resolved.setOriginal(artifact);
				logger.info("Resolved artifact {} instead of {}", resolved.getGAVECId(), 
						artifact.toString());
				return resolved;
			} 
			resolved = resolvedManually(artifact);
			return resolved;
		}
	}
	
	/**
	 * Resolves the artifact manually. 
	 * <p>
	 * It automatically constructor the path of the artifact in the local 
	 * repository and find if it has been resolved already. 
	 * </p>
	 * 
	 * @param artifact  the artifact to be resolved
	 * @return  the resolved artifact
	 */
	public ResolvedArtifact resolvedManually(@NonNull Artifact artifact) {
		ResolvedArtifact resolved = null;
		String fileName = FilenameUtils.concat(localRepoPath, AetherHelper.getPath(artifact));
		File file = new File(fileName);
		if(file.exists()) {
			resolved = new ResolvedArtifact(artifact.setFile(file));
		} else {
			resolved = new ResolvedArtifact(artifact);
			resolved.setStatus(UNRESOLVED);
		}
		return resolved;
	}
	
	/**
	 * Resolves an alternative artifact from given list of remote repositories. 
	 * 
	 * @param artifact  the {@link Artifact} instance to be resolved
	 * @param remoteRepos  the list of {@link RemoteRepository} instances
	 * @return  the resolved {@link ResolvedArtifact} instance
	 * 
	 * @throws VersionRangeResolutionException
	 * @throws ArtifactResolutionException
	 */
	public ResolvedArtifact resolveAlternative(@NonNull Artifact artifact, 
			@NonNull List<RemoteRepository> remoteRepos) {
		ResolvedArtifact resolved = null;
		for(RemoteRepository remoteRepo : remoteRepos) {
			try {
				resolved = resolveAlternative(artifact, remoteRepo);
				return resolved;
			} catch (RepositoryException e) {
				/*logger.warn("Fail to reslove artifact {} from {}, \n{}", 
						artifact, remoteRepo.getUrl(), Throwables.getStackTraceAsString(e));*/
			}
		}
		return resolved;
	}
	
	/**
	 * Resolves an alternative artifact from the given remote repository. 
	 * 
	 * @param artifact  the original {@link Artifact} instance
	 * @param remoteRepo  the {@link RemoteRepository} instance
	 * @return  the {@link ResolvedArtifact} instance
	 * 
	 * @throws VersionRangeResolutionException
	 * @throws ArtifactResolutionException
	 */
	public ResolvedArtifact resolveAlternative(@NonNull Artifact artifact, 
			RemoteRepository remoteRepo) throws VersionRangeResolutionException, 
			ArtifactResolutionException {
		Artifact baseArtifact = baseCopy(artifact);
		List<RemoteRepository> remoteRepos = new ArrayList<>();
		remoteRepos.add(remoteRepo);
		VersionRangeRequest rangeRequest = new VersionRangeRequest();
		rangeRequest.setArtifact(baseArtifact);
		rangeRequest.setRepositories(remoteRepos);
		VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
		Version newestVersion = rangeResult.getHighestVersion();
		if(newestVersion == null) {
			throw new VersionRangeResolutionException(rangeResult);
		}
		return resolve(baseArtifact.setVersion(newestVersion.toString()), remoteRepos);
	}
	
	/**
	 * Resolves an artifact normally. 
	 * 
	 * @param artifact  the {@link Artifact} instance to be resolved
	 * @param remoteRepos   the list of {@link RemoteRepository} instances
	 * @return  the resolved artifact
	 * 
	 * @throws ArtifactResolutionException
	 */
	public ResolvedArtifact resolve(@NonNull Artifact artifact, 
			@NonNull List<RemoteRepository> remoteRepos) 
					throws ArtifactResolutionException {
		ArtifactRequest request = new ArtifactRequest();
		request.setArtifact(artifact);
		request.setRepositories(remoteRepos);
		ArtifactResult result = system.resolveArtifact(session, request);
		return new ResolvedArtifact(result.getArtifact());
	}
}
