package koncept.pheidippides;

import java.net.URI;

public interface Repository {

	public URI getRepositoryUri();
	
	/**
	 * Attempts to resolve the given artifact in this repository 
	 * 
	 * @param descriptor
	 * @return the resolved artifact, or null
	 */
	public ResolvedArtifact resolve(ArtifactDescriptor descriptor);
	
	/**
	 * Attempts to resolve the given child artifact in this repository
	 * @param descriptor
	 * @param modulePath
	 * @return the resolved artifact, or null
	 */
	public ResolvedArtifact resolveChildModule(ArtifactDescriptor descriptor, String modulePath);
	
	/**
	 * Attempts to resolve the given artifact in this repository
	 * @param relativePath
	 * @return the resolved artifact, or null
	 */
	public ResolvedArtifact resolvePath(String relativePath);
	
}
