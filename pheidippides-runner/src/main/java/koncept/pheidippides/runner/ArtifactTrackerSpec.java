package koncept.pheidippides.runner;

public class ArtifactTrackerSpec {

	private final String groupId;
	private final String artifactId;
	
	public ArtifactTrackerSpec(String groupId) {
		this.groupId = groupId;
		artifactId = null;
	}
	
	public ArtifactTrackerSpec(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	
}
