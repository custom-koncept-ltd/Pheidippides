package koncept.pheidippides.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.LocationListing;
import koncept.pheidippides.artifact.MavenPomDescriptor;
import koncept.pheidippides.artifact.MavenVersionMetadata;

public class FileSearchLocation implements LocationListing {

	private final FileSearchLocation parent;
	private final File dir;
	
	public FileSearchLocation(FileSearchLocation parent, File dir) {
		this.parent = parent;
		this.dir = dir;
	}
	
	public List<LocationListing> search() {
		List<LocationListing> found = new ArrayList<LocationListing>();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().startsWith(".");
			}
		});
		for(File file: files)
			found.add(new FileSearchLocation(this, file));
		return found;
	}

	public MavenVersionMetadata getMavenLocalMetadata() throws FileNotFoundException, XMLStreamException {
		File localMetadata = new File(dir, "XXXmaven-metadata-local.xml"); //uh: the -local is the repo name
		if (localMetadata.exists())
			return new MavenVersionMetadata(localMetadata.toURI(), new FileInputStream(localMetadata));
		return null;
	}
	
	public URI getListingLocation() {
		return dir.toURI();
	}
	
	public String getRepositoryPath() {
		if (parent == null) return "/"; //will be the special case parent
		return parent.getRepositoryPath() + dir.getName() + "/";
	}
	
	public List<ArtifactDescriptor> getArtifactDescriptors() {
		List<ArtifactDescriptor> artifacts = new ArrayList<ArtifactDescriptor>();
		try {
			
			//FIRST, use any inder properties that there might be
			
			//resolver-status.properties
			File resolverProperties = getFileIfExists("resolver-status.properties");
			if (resolverProperties != null) {
				
				//todo: Map<Long, MavenVersionMetaData> = new TreeMap();
				List<MavenVersionMetadata> metas = new ArrayList<MavenVersionMetadata>();
				
				Properties p = new Properties();
				p.load(new FileInputStream(resolverProperties));
				for(Object keyObj: p.keySet()) {
					String key = (String)keyObj;
					if (key.endsWith(".lastUpdated")) {
//						String lastUpdated = p.getProperty(key);
						String filename = key.substring(0, key.length() - 12); //12 = length of ".lastupdated"
						//split into filename and a last updated ts
						File metadataFile = getFileIfExists(filename);
						if (metadataFile != null) {
							MavenVersionMetadata versionMetadata = new MavenVersionMetadata(metadataFile.toURI(), new FileInputStream(metadataFile));
							for(int i = 0; i < metas.size() && versionMetadata != null; i++) {
								MavenVersionMetadata existing = metas.get(i);
								if (versionMetadata.getArtifactDescriptor().equals(existing.getArtifactDescriptor())) {
									if (versionMetadata.getLastTimestamp() > existing.getLastTimestamp()) {
										metas.remove(i);
										i--;
									} else {
										versionMetadata = null;
									}
								}
							}
							if (versionMetadata != null) {
								metas.add(versionMetadata);
							}
						}
					}
				}
				for(MavenVersionMetadata meta: metas) {
					ArtifactDescriptor descriptor = meta.getArtifactDescriptor();
					if (
							descriptor != null && 
							descriptor.toPath().equals(getRepositoryPath()) &&
//							!artifacts.contains(descriptor)
							true
							) {
						artifacts.add(descriptor);
					}
				}
				if (!artifacts.isEmpty()) return artifacts;
			}
			
			
			//finally - fall back to a big scan for pom files
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".pom");
				}
			});
			
			for(File file: files) {
				MavenPomDescriptor descriptor = new MavenPomDescriptor(file.toURI(), new FileInputStream(file));
				artifacts.add(descriptor.getDescriptor());
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
//				throw new RuntimeException(e); //swallow for now
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return artifacts;
	}
	
	private File getFileIfExists(String... fileNames) {
		for(String fileName: fileNames) {
			File file = new File(dir, fileName);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}
	

}
