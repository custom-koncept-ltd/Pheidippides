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
	public ArtifactResolutionPoint resolve(ArtifactDescriptor descriptor);
	
	/**
	 * @return true if the repository is listable, false if it is not
	 */
	public boolean isListableRepository();
	
	/**
	 * if the contents of the repository can be listed (browsed), this will
	 * return the listing implementation
	 * @return
	 */
	public ArtifactLister getArtifactLister();
	
	
}
