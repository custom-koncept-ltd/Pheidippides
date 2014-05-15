package koncept.pheidippides;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Resolved artifact - has parsed and processed the details
 * @author koncept
 *
 */
public interface ArtifactResolutionPoint {
	
	/**
	 * @return the parent artifact descriptor, or null
	 */
	public ArtifactDescriptor getParent();
	
	/**
	 * @return the version descriptor for this resolved artifact
	 */
	public ArtifactDescriptor getDescriptor();
	
	/**
	 * @return child projects, or an empty list 
	 */
	public List<String> getChildModules();
	
	/**
	 * fully qualified resolution uri
	 * @return
	 */
	public URI getResolvedLocation();
	
	/**
	 * a collection of all of the downloadable artifacts for this artifact descriptor
	 * @return
	 */
	public Collection<ArtifactResource> getResources();
}
