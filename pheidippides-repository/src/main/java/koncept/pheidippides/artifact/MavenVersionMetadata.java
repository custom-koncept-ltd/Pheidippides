package koncept.pheidippides.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactResource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MavenVersionMetadata {
	
	private final URI location;
	
	private ArtifactDescriptor descriptor;
	private Map<String, String> substituteVersionSuffixes = new HashMap<String, String>();
	private long lastUpdated = 0L;
	
	public MavenVersionMetadata(URI location, InputStream in) throws IOException  {
		this.location = location;
		Document doc = Jsoup.parse(in, "UTF-8", location.toString());
		
		String artifactId = elementText(doc, "metadata > artifactId");
		String groupId = elementText(doc, "metadata > groupId");
		String version = elementText(doc, "metadata > version"); //if null may be updated below
		
		//have a dodgy 'version' hack
		Element versioningElement = element(doc, "metadata > versioning");
		if (versioningElement != null) {
			String lastUpdated = elementText(doc, "lastUpdated");
			Element versionsElement = element(versioningElement, "> versions");
			if (version == null && versionsElement != null) {
				version = elementText(versionsElement, "> version");
			}
			Element snapshotVersions = element(versioningElement, ">snapshotVersions");
			if (snapshotVersions != null && lastUpdated != null) {
				lastUpdated = lastUpdated.replaceAll("\\.", ""); //strip out any dots - they are optional, and can get in the way
				for(Element snapshotVersion: snapshotVersions.select("> snapshotVersion")) {
					String updated = elementText(snapshotVersion, "> updated");
					if (updated != null) updated = updated.replaceAll("\\.", "");
					if (updated != null && updated.equals(lastUpdated)) {
						String extension = elementText(snapshotVersion, "> extension");
						String suffix = elementText(snapshotVersion, "> value");
						substituteVersionSuffixes.put(extension, suffix);
					}
				}
			}
		}
		
		if (groupId != null && artifactId != null && version != null)
			descriptor = new ArtifactDescriptor(groupId, artifactId, version);

	}
	
	private String elementText(Element root, String cssQuery) {
		Elements elements = root.select(cssQuery);
		if (elements.size() == 0)
			return null;
		if (elements.size() > 1)
			throw new RuntimeException("Found multiple matches for " + cssQuery);
		return elements.get(0).text();
	}
	
	private Element element(Element root, String cssQuery) {
		Elements elements = root.select(cssQuery);
		if (elements.size() == 0)
			return null;
		if (elements.size() > 1)
			throw new RuntimeException("Found multiple matches for " + cssQuery);
		return elements.get(0);
	}
	
	public ArtifactDescriptor getArtifactDescriptor() {
		return descriptor;
	}
	
	public String getSubstituteVersionSuffixToUse(String artifactType) {
		String suffix = substituteVersionSuffixes.get(artifactType);
		return suffix == null ? descriptor.getVersion() : suffix;
	}
	
	public Set<String> getKnownArtifactTypes() {
		return substituteVersionSuffixes.keySet();
	}
	
	public long getLastTimestamp() {
		return lastUpdated;
	}
	
	public ArtifactDescriptor getDescriptor() {
		return descriptor;
	}
	
	public URI getResolvedLocation() {
		return location;
	}
	
	public Map<String, ArtifactResource> getResources() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
