package koncept.pheidippides.runner;

import java.util.ArrayList;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.LocationListing;
import koncept.pheidippides.Repository;

public class ArtifactTracker {

	Repository repository;
	List<ArtifactTrackerSpec> trackerSpecs;
	List<ArtifactDescriptor> currentArtifacts;
	
	public ArtifactTracker(Repository repository, List<ArtifactTrackerSpec> trackerSpecs) {
		this.repository = repository;
		this.trackerSpecs = trackerSpecs;
	}
	
	public List<ArtifactTrackerSpec> getTrackerSpecs() {
		return trackerSpecs;
	}
	
	public List<ArtifactDescriptor> getCurrentArtifacts() {
		return currentArtifacts;
	}
	
	public void update() {
			
		if (trackerSpecs.isEmpty() && repository.isListableRepository())
			currentArtifacts = scanListableRepositoryForAll();
		
	}
	
	// currently brute force
	private List<ArtifactDescriptor> scanListableRepositoryForAll() {
		List<ArtifactDescriptor> foundArtifacts = new ArrayList<ArtifactDescriptor>();
		List<LocationListing> rootLocation = repository.getListableRepository().getRootSearchLocation();
		scanListableRepositoryForAll(foundArtifacts, rootLocation);
		return foundArtifacts;
	}
	
	private void scanListableRepositoryForAll(List<ArtifactDescriptor> descriptors, List<LocationListing> locations) {
		for(LocationListing listing: locations) {
			List<ArtifactDescriptor> foundDescriptors = listing.getArtifactDescriptors();
			for(ArtifactDescriptor descriptor: foundDescriptors) {
				descriptors.add(descriptor);
			}
			scanListableRepositoryForAll(descriptors, listing.search());
		}
	}
	
	
}
