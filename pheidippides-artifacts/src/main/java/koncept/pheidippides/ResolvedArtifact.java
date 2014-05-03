package koncept.pheidippides;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Resolved artifact - has parsed and processed the details
 * @author koncept
 *
 */
public interface ResolvedArtifact {
	
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
	
	public URI getResolvedLocation();
	
	/**
	 * the resources MAY be keyed by classifier, 
	 * or they MAY be keyed by file name (if applicable)
	 * 
	 * @return a map of artifact resources.
	 */
	public Map<String, ArtifactResource> getResources();
}
