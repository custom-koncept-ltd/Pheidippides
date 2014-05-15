package koncept.pheidippides.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.Repository;
import koncept.pheidippides.listing.LocationListing;

public class ArtifactTracker {

	Repository repository;
	List<ArtifactTrackerSpec> trackerSpecs;
	List<ArtifactDescriptor> currentArtifacts;
	
	public ArtifactTracker(Repository repository, List<ArtifactTrackerSpec> trackerSpecs) {
		this.repository = repository;
		this.trackerSpecs = trackerSpecs;
		if (trackerSpecs.isEmpty())
			trackerSpecs = Arrays.asList(new ArtifactTrackerSpec()); //no filter, but empty lists cause issues
	}
	
	public List<ArtifactTrackerSpec> getTrackerSpecs() {
		return trackerSpecs;
	}
	
	public List<ArtifactDescriptor> getCurrentArtifacts() {
		return currentArtifacts;
	}
	
	public void update() {
		if (!repository.isListableRepository())
			throw new RuntimeException("Unable to scan a repository that is not listable");
		currentArtifacts = scanListableRepositoryForAll();

		
	}
	
	private boolean scanListing(LocationListing listing) {
		for(ArtifactTrackerSpec spec: trackerSpecs) {
			String listingPath = listing.getListingPath();
			String specPath = spec.toPartialPath();
			if (listingPath.length() >= specPath.length()) {
				if(listingPath.startsWith(specPath))
					return true;
			} else {
				if(specPath.startsWith(listingPath))
					return true;
			}		
		}
		return false;
	}
	
	private boolean resolveListing(LocationListing listing) {
		for(ArtifactTrackerSpec spec: trackerSpecs) {
			if (listing.getListingPath().startsWith(spec.toPartialPath()))
				return true;
		}
		return false;
	}
	
	// currently brute force
	private List<ArtifactDescriptor> scanListableRepositoryForAll() {
		List<ArtifactDescriptor> foundArtifacts = new ArrayList<ArtifactDescriptor>();
		LocationListing rootLocation = repository.getArtifactLister().getRootSearchLocation();
		scanListableRepositoryForAll(foundArtifacts, rootLocation);
		return foundArtifacts;
	}
	
	private void scanListableRepositoryForAll(List<ArtifactDescriptor> descriptors, LocationListing location) {
		if (scanListing(location)) {
			if (resolveListing(location))
				descriptors.addAll(location.getArtifactDescriptors());
			for(LocationListing child: location.search()) {
				scanListableRepositoryForAll(descriptors, child);
			}
		}
	}
	
	
}
