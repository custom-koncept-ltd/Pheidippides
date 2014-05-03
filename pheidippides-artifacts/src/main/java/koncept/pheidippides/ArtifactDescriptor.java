package koncept.pheidippides;


public class ArtifactDescriptor {
	private final String groupId;
	private final String artifactId;
	private final String version;
	

	public ArtifactDescriptor(
			String groupId,
			String artifactId,
			String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		if (groupId == null) throw new NullPointerException("groupId cannot be null");
		if (artifactId == null) throw new NullPointerException("artifactId cannot be null");
		if (version == null) throw new NullPointerException("version cannot be null");
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getVersion() {
		return version;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != getClass()) return false;
		ArtifactDescriptor that = (ArtifactDescriptor)obj;
		return this.groupId.equals(that.groupId) &&
				this.artifactId.equals(that.artifactId) &&
				this.version.equals(that.version);
	}
	
	@Override
	public int hashCode() {
		//not very efficient
		return this.groupId.hashCode();
	}
	
}
