package koncept.pheidippides.file;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ListableRepository;
import koncept.pheidippides.LocationListing;
import koncept.pheidippides.ResolvedArtifact;
import koncept.pheidippides.artifact.MavenPomDescriptor;

public class FilesystemRepository implements ListableRepository {

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
	
	public List<LocationListing> getRootSearchLocation() {
		if (root.exists() && root.isDirectory()) {
			return Arrays.asList((LocationListing)new FileSearchLocation(null, root));
		}
		return Collections.emptyList();
	}
	
	public ResolvedArtifact resolve(ArtifactDescriptor descriptor) {
		String groupId = descriptor.getGroupId().replace('.', '/');
		
		File artifact = new File(root,
				groupId + "/"
						+ descriptor.getArtifactId() + "/"
						+ descriptor.getVersion());
		
		if (artifact.exists())
			return null;
//			return new FileSearchLocation(artifact).getArtifact();
		return null;
	}
	
	public boolean isListableRepository() {
		return true;
	}
	
	public ListableRepository getListableRepository() {
		return this;
	}
	
	public ResolvedArtifact resolveChildModule(ArtifactDescriptor descriptor, String modulePath) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ResolvedArtifact resolvePath(String relativePath) {
		File file = new File(root, relativePath);
		if (file.exists() && file.isFile()) try {
			return new MavenPomDescriptor(file.toURI(), new FileInputStream(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
