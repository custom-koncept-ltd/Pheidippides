package koncept.pheidippides.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MavenPomDescriptor {
	
	private final URI location;
	
	private ArtifactDescriptor parent;
	private ArtifactDescriptor descriptor;
	
	private List<String> modules = Collections.emptyList();
	
	public MavenPomDescriptor(URI location, InputStream in) throws IOException {
		this.location = location;
		Document doc = Jsoup.parse(in, "UTF-8", location.toString());
		
		String artifactId = elementText(doc, "project > artifactId");
		String groupId = elementText(doc, "project > groupId");
		String version = elementText(doc, "project > version");
		
		Element parentElement = element(doc, "project > parent");
		if (parentElement != null) {
			String parentArtifactId = elementText(parentElement, "> artifactId");
			String parentGroupId = elementText(parentElement, "> groupId");
			String parentVersion = elementText(parentElement, "> version");
			
			parent = new ArtifactDescriptor(parentGroupId, parentArtifactId, parentVersion);
			System.out.println(parent);
		}
		
		if (groupId == null && parent != null) groupId = parent.getGroupId(); //inherit from parent
		if (version == null && parent != null) version = parent.getVersion(); //inherit from parent
		descriptor = new ArtifactDescriptor(groupId, artifactId, version);
		
		
		Element modulesElement = element(doc, "project > modules");
		if (modulesElement != null) {
			modules = new ArrayList<String>();
			for(Element moduleElement: modulesElement.select("> module")) {
				modules.add(moduleElement.text());
			}
		}
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
	
	public ArtifactDescriptor getDescriptor() {
		return descriptor;
	}
	
	public ArtifactDescriptor getParent() {
		return parent;
	}
	
	public List<String> getChildModules() {
		return modules;
	}
	
	public URI getResolvedLocation() {
		return location;
	}

}
