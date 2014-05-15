package koncept.pheidippides.listing;

import java.io.IOException;
import java.io.InputStream;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactResource;

public class ListedArtifactResource implements ArtifactResource {

	private final ContentListing contentListing;
	private final ArtifactDescriptor descriptor;
	private final String type;
	private final String classifier;
	
	public ListedArtifactResource(ContentListing contentListing, ArtifactDescriptor descriptor, String type) {
		this(contentListing, descriptor, type, null);
	}
	
	public ListedArtifactResource(ContentListing contentListing, ArtifactDescriptor descriptor, String type, String classifier) {
		this.contentListing = contentListing;
		this.descriptor = descriptor;
		this.type = type;
		this.classifier = classifier;
	}
	
	public ArtifactDescriptor getDescriptor() {
		return descriptor;
	}

	public String getType() {
		return type;
	}

	public String getClassifier() {
		return classifier;
	}

	public InputStream open() throws IOException {
		return contentListing.openStream();
	}

}
