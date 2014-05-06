package koncept.pheidippides;

import java.net.URI;
import java.util.List;

public interface LocationListing {

	public List<LocationListing> search();
	
//	public List<ResolvedArtifact> getArtifacts();
	
	public List<ArtifactDescriptor> getArtifactDescriptors(); //actually, this is what we want...
	
	public URI getListingLocation();
	
}
