package koncept.pheidippides.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactLister;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.Repository;
import koncept.pheidippides.artifact.DefaultArtifactResolutionPoint;
import koncept.pheidippides.artifact.MavenPomDescriptor;
import koncept.pheidippides.listing.LocationListing;

public class FilesystemRepository implements Repository, ArtifactLister {

	private final File root;
	
	public static FilesystemRepository getDefaultUserRepository() {
		File file = new File(getUserDefaultRepositoryLocation());
		return new FilesystemRepository(file);
	}
	
	public static String getUserDefaultRepositoryLocation() {
		return System.getProperty("user.home") + "/.m2/repository";
	}
	
	public FilesystemRepository(File root) {
		this.root = root.getAbsoluteFile();
	}
	
	public URI getRepositoryUri() {
		return root.toURI();
	}
	
	public LocationListing getRootSearchLocation() {
		if (root.exists() && root.isDirectory()) {
			return (LocationListing) new FileSearchLocation(null, root);
		}
		return null;
	}
	
	public LocationListing getSearchLocation(String repositoryPath) {
		if (!repositoryPath.startsWith("/"))
			throw new RuntimeException("repository paths must be absolute");
		String [] parts = repositoryPath.split("/");
		FileSearchLocation location = new FileSearchLocation(null, root);
		for(String part: parts) if (!part.equals("")) {
			location = new FileSearchLocation(location, new File(location.getDir(), part));
		}
		if (!location.getDir().exists())
			return null;
		return (LocationListing)location;
	}
	
	public ArtifactResolutionPoint resolve(ArtifactDescriptor descriptor) {
		try {
			File dir = new File(root, descriptor.toPath());
			File expectedPom = new File(dir, descriptor.getArtifactId() + "-" + descriptor.getVersion() + ".pom");
			if (!expectedPom.exists() || !expectedPom.isFile())
				throw new RuntimeException("Unable to resolve artifact " + descriptor);
			MavenPomDescriptor pomDescriptor = new MavenPomDescriptor(expectedPom.toURI(), new FileInputStream(expectedPom));
			
			if (!descriptor.equals(pomDescriptor.getDescriptor()))
				throw new RuntimeException("unexpected artifact descriptor: got " + pomDescriptor.getDescriptor() + " but expected " + descriptor);
			return new DefaultArtifactResolutionPoint(this, dir.toURI(), pomDescriptor);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isListableRepository() {
		return true;
	}
	
	public ArtifactLister getArtifactLister() {
		return this;
	}
	
	public ArtifactResolutionPoint resolveChildModule(ArtifactDescriptor descriptor, String modulePath) {
		// TODO Auto-generated method stub
		return null;
	}

}
