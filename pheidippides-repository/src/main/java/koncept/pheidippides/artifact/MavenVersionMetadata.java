package koncept.pheidippides.artifact;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactResource;

public class MavenVersionMetadata {
	private static XMLInputFactory fac = XMLInputFactory.newInstance();
	
	private final URI location;
	
	private ArtifactDescriptor descriptor;
	private Map<String, String> substituteVersionSuffixes = new HashMap<String, String>();
	private long lastUpdated = 0L;
	
	public MavenVersionMetadata(URI location, InputStream in) throws XMLStreamException  {
		this.location = location;
		XMLStreamReader reader = fac.createXMLStreamReader(in);
		readXmlDoc(reader);
		
		List<String> validationErrors = validate();
		if (!validationErrors.isEmpty()) {
			throw new RuntimeException("Validation Error(s): " + validationErrors);
		}
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
	
	
	private void readXmlDoc(XMLStreamReader reader) throws XMLStreamException {
		int depth = -1; //starts with 'project' as the first element
		
		String groupId = null;
		String artifactId = null;
		String version = null;
		
		while (reader.hasNext()) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("groupId"))
					groupId = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("artifactId"))
					artifactId = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("version"))
					version = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("versioning"))
					readVersioning(reader);
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		
		if (descriptor != null && version == null) 
			version = descriptor.getVersion();
		if (groupId != null && artifactId != null && version != null)
			descriptor = new ArtifactDescriptor(groupId, artifactId, version);
	}
	
	private void readVersioning(XMLStreamReader reader) throws XMLStreamException {
		int depth = 0;
		String lastUpdated = null;
		while (reader.hasNext() && depth >= 0) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("lastUpdated"))
					lastUpdated = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("versions"))
					readVersion(reader);
				else if (depth == 0 && reader.getLocalName().equals("snapshotVersions"))
					readSnapshotVersions(lastUpdated, reader);
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		if (lastUpdated != null) {
			this.lastUpdated = new Long(lastUpdated.replaceAll("\\.", ""));
		}
	}
	
	private void readVersion(XMLStreamReader reader) throws XMLStreamException {
		int depth = 0;
		List<String> versions = new ArrayList<String>();
		while (reader.hasNext() && depth >= 0) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("version"))
					versions.add(readSimpleTagContents(reader));
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		if (!versions.isEmpty()) {
			String version = versions.get(versions.size() - 1);
			descriptor = new ArtifactDescriptor("", "", version); //TODO: Find a better way of dealing with this poke through
		}
	}
	
	private void readSnapshotVersions(String lastUpdated, XMLStreamReader reader) throws XMLStreamException {
		int depth = 0;
		boolean inSnapshotVersion = false;
		String extension = "";
		String suffix = "";
		String updated = "";
		
		while (reader.hasNext() && depth >= 0) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("snapshotVersion")) {
					depth++;
					inSnapshotVersion = true;
				} else if (depth == 1 && inSnapshotVersion && reader.getLocalName().equals("extension"))
					extension = readSimpleTagContents(reader);
				else if (depth == 1 && inSnapshotVersion && reader.getLocalName().equals("value"))
					suffix = readSimpleTagContents(reader);
				else if (depth == 1 && inSnapshotVersion && reader.getLocalName().equals("updated"))
					updated = readSimpleTagContents(reader);
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				if (depth == 0 && inSnapshotVersion && updated.equals(lastUpdated)) {
					substituteVersionSuffixes.put(extension, suffix);
					//not supposed to be required - but ensure there is no bleed-through with badly formatted xml
					extension = "";
					suffix = "";
					updated = "";
				}
				inSnapshotVersion = false;
				break;
			}
		}
		
		//snapshotVersion
		
//		 <extension>jar</extension>
//	        <value>1.0-SNAPSHOT</value>
//	        <updated>20140424170111</updated>
		
		
//		<snapshotVersion>
//        <extension>jar</extension>
//        <value>1.0-SNAPSHOT</value>
//        <updated>20140424170111</updated>
//      </snapshotVersion>
	}
	
	private String readSimpleTagContents(XMLStreamReader reader) throws XMLStreamException {
		int depth = 1; //already opened the tag
		String content = "";
		while (reader.hasNext() && depth != 0) {
			switch(reader.next()) {
			case XMLStreamConstants.START_ELEMENT:
				depth++; //not expected
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			case XMLStreamConstants.CHARACTERS:
				content = content + reader.getText().trim();
				break;
			}
		}
		return content;
	}
	
	private List<String> readModules(XMLStreamReader reader) throws XMLStreamException {
		List<String> modules = new ArrayList<String>();
		int depth = 0;
		while (reader.hasNext() && depth >= 0) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("module")) {
					modules.add(readSimpleTagContents(reader));
				}
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		return modules;
	}
	
	public List<String> validate() {
		return Collections.emptyList();
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
