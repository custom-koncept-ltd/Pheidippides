package koncept.pheidippides.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import koncept.pheidippides.ResolvedArtifact;
import koncept.pheidippides.SearchLocation;
import koncept.pheidippides.artifact.MavenVersionMetadata;

public class FileSearchLocation implements SearchLocation {

	private final File dir;
	
	public FileSearchLocation(File dir) {
		this.dir = dir;
	}
	
	public List<SearchLocation> search() {
		List<SearchLocation> found = new ArrayList<SearchLocation>();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().startsWith(".");
			}
		});
		for(File file: files)
			found.add(new FileSearchLocation(file));
		return found;
	}

	public MavenVersionMetadata getMavenLocalMetadata() throws FileNotFoundException, XMLStreamException {
		File localMetadata = new File(dir, "maven-metadata-local.xml");
		if (localMetadata.exists())
			return new MavenVersionMetadata(localMetadata.toURI(), new FileInputStream(localMetadata));
		return null;
	}
	
	public List<ResolvedArtifact> getArtifacts() {
		return Collections.emptyList();
		
//		maven-metadata-local.xml
//		maven-metadata-repository.custom-koncept.co.uk.xml
		
//		File[] files = dir.listFiles(new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				return name.endsWith(".pom");
//			}
//		});
//		if (files.length == 0)
//			return null;
//		if (files.length == 1) try {
//			return new MavenPomDescriptor(files[0].toURI(), new FileInputStream(files[0]));
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		throw new RuntimeException("More than 1 artifact definition found: " + Arrays.asList(files));

	}
	
	public List<ResolvedArtifact> getArtifacts___() {
		//if there is a maven-metadata-local.xml file, parse it
		
		
//		test-maven-metadata-local.xml
		
		return null;
	}
	
	

}
