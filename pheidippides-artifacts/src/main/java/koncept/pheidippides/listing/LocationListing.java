package koncept.pheidippides.listing;

import java.net.URI;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;

public interface LocationListing {

	/**
	 * retuns any location listings that are accessable from this location (sub directories)
	 * @return
	 */
	public List<LocationListing> search();
	
	/**
	 * looks for any artifact descriptors at this location
	 * 
	 * @deprecated use a wrapper around a 'content listing'
	 * 
	 * @return
	 */
	@Deprecated()
	public List<ArtifactDescriptor> getArtifactDescriptors(); //actually, this is what we want...
	
	/**
	 * gets the fully qualified URI for the current location listing
	 * @return
	 */
	public URI getLocation();
	
	/**
	 * gets the sub-path WITHIN the repository that this location listing represents
	 * @return
	 */
	public String getListingPath();
	
	/**
	 * wrapped lister for the contents of the location <br/>
	 * This will NOT return directories, ONLY "FILES"
	 * @return
	 */
	public List<String> listContents();
	
	/**
	 * return a wrapper on the underlyer, or null if it doesn't exist
	 * 
	 * @param name
	 * @return
	 */
	public ContentListing getContents(String name);
}
