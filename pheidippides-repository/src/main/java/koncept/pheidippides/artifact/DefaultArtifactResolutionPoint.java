package koncept.pheidippides.artifact;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactLister;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.ArtifactResource;
import koncept.pheidippides.listing.ListedArtifactResource;
import koncept.pheidippides.listing.LocationListing;

public class DefaultArtifactResolutionPoint implements ArtifactResolutionPoint {

	private final ArtifactLister lister;
	private final URI location;
	private final MavenPomDescriptor pomDescriptor;
	private final MavenVersionMetadata versionMetadata;
	
	public DefaultArtifactResolutionPoint(ArtifactLister lister, URI location, MavenPomDescriptor pomDescriptor){
		this(lister, location, pomDescriptor, null);
		
	}
	
	public DefaultArtifactResolutionPoint(ArtifactLister lister, URI location, MavenPomDescriptor pomDescriptor, MavenVersionMetadata versionMetadata){
		this.lister = lister;
		this.location = location;
		this.pomDescriptor = pomDescriptor;
		this.versionMetadata = versionMetadata;
	}
	
	public ArtifactDescriptor getParent() {
		return pomDescriptor.getParent();
	}

	public ArtifactDescriptor getDescriptor() {
		return pomDescriptor.getDescriptor();
	}

	public List<String> getChildModules() {
		return pomDescriptor.getChildModules();
	}

	public URI getResolvedLocation() {
		return location;
	}

	public Collection<ArtifactResource> getResources() {
		List<ArtifactResource> resources = new ArrayList<ArtifactResource>();
		LocationListing listing = lister.getSearchLocation(getDescriptor().toPath());
		List<String> contents = listing.listContents();
		for(String content: contents) {
			String artifactType = getArtifactType(content);
			if (artifactType == null) continue; //invalid - skip it
			String prefix = generatePrefixForArtifactType(artifactType);
			if (content.startsWith(prefix)) {
				String classifier = content.substring(prefix.length() + 1, content.length() - artifactType.length());
				if (classifier.equals("")) {
					resources.add(new ListedArtifactResource(listing.getContents(content), getDescriptor(), artifactType));
				} else {
					classifier = classifier.substring(0, classifier.length() - 1);
					resources.add(new ListedArtifactResource(listing.getContents(content), getDescriptor(), artifactType, classifier));
				}
			}
		}
		return resources;
	}

	private String generatePrefixForArtifactType(String artifactType) {
		if (versionMetadata != null)
			return getDescriptor().getArtifactId() + "-" + versionMetadata.getSubstituteVersionSuffixToUse(artifactType);
		return getDescriptor().getArtifactId() + "-" + getDescriptor().getVersion();
	}
	
	private String getArtifactType(String filename) {
		int index = filename.lastIndexOf(".");
		if (index == -1 || index == filename.length()) return null;
		return filename.substring(index + 1);
	}
	
}
