package koncept.pheidippides.artifact;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import koncept.pheidippides.ArtifactDescriptor;

public class MavenPomDescriptor {
	private static XMLInputFactory fac = XMLInputFactory.newInstance();
	
	private final URI location;
	
	private ArtifactDescriptor parent;
	private ArtifactDescriptor descriptor;
	
	private List<String> modules = Collections.emptyList();
	
	public MavenPomDescriptor(URI location, InputStream in) throws XMLStreamException  {
		this.location = location;
		XMLStreamReader reader = fac.createXMLStreamReader(in);
		readXmlDoc(reader);
		
//		List<String> validationErrors = validate();
//		if (!validationErrors.isEmpty()) {
//			throw new RuntimeException("Validation Error(s): " + validationErrors);
//		}
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
				else if (depth == 0 && reader.getLocalName().equals("modules")) 
					modules = readModules(reader);
				else if (depth == 0 && reader.getLocalName().equals("parent")) 
					parent = readParentDescriptor(reader);
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		
		
		if (groupId == null && parent != null) groupId = parent.getGroupId(); //inherit from parent
		if (version == null && parent != null) version = parent.getVersion(); //inherit from parent
		
		//not handling groupId in parent yet...
		descriptor = new ArtifactDescriptor(groupId, artifactId, version);
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
	
	private ArtifactDescriptor readParentDescriptor(XMLStreamReader reader) throws XMLStreamException {
		int depth = 0;
		
		String groupId = null;
		String artifactId = null;
		String version = null;
		
		while (reader.hasNext() && depth >= 0) {
			int eventId = reader.next();
			switch (eventId) {
			case XMLStreamConstants.START_ELEMENT:
				if (depth == 0 && reader.getLocalName().equals("groupId"))
					groupId = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("artifactId"))
					artifactId = readSimpleTagContents(reader);
				else if (depth == 0 && reader.getLocalName().equals("version"))
					version = readSimpleTagContents(reader);
				else {
					depth++;
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth--;
				break;
			}
		}
		
		//not handling groupId in parent yet...
		return new ArtifactDescriptor(groupId, artifactId, version);
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
