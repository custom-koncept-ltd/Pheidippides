package koncept.pheidippides.runner;

public class ArtifactTrackerSpec {

	private final String groupId;
	private final String artifactId;
	
	public ArtifactTrackerSpec() {
		groupId = null;
		artifactId = null;
	}
	
	public ArtifactTrackerSpec(String groupId) {
		this.groupId = groupId;
		artifactId = null;
		if (groupId == null) 
			throw new NullPointerException("groupId cannot be constructed as null");
	}
	
	public ArtifactTrackerSpec(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		if (groupId == null) 
			throw new NullPointerException("groupId cannot be constructed as null");
		if (artifactId == null) 
			throw new NullPointerException("artifactId cannot be constructed as null");
	}
	
	public String getGroupId() {
		return groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	
	public String toPartialPath() {
		StringBuilder sb = new StringBuilder("/");
		if (groupId != null) {
			for(String part: groupId.split("\\.")) 
				sb.append(part).append("/");
		}
		if (artifactId != null) {
			sb.append(artifactId).append("/");
		}
		return sb.toString();
	}
	
}
