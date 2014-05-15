package koncept.pheidippides;

import koncept.pheidippides.listing.LocationListing;

public interface ArtifactLister {

	public LocationListing getRootSearchLocation();
	
	public LocationListing getSearchLocation(String repositoryPath);
	
}
