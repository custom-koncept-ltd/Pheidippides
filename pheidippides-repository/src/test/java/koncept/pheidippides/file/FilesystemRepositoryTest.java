package koncept.pheidippides.file;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.listing.LocationListing;

import org.junit.Test;

public class FilesystemRepositoryTest {

	
	private FilesystemRepository testResourcesRepository() {
		URL resourcesLocation = getClass().getResource("/pom.xml");
		return new FilesystemRepository(new File(resourcesLocation.getFile()).getParentFile());
	}
	
	
	@Test
	public void singleRootIsEmptyAndSearchable() {
		FilesystemRepository fsr = testResourcesRepository();
		LocationListing rootSearchLocation = fsr.getRootSearchLocation();
		assertNotNull(rootSearchLocation);
		
		List<ArtifactDescriptor> artifacts = rootSearchLocation.getArtifactDescriptors();
		assertNotNull(artifacts);
		
		assertTrue(artifacts.isEmpty()); //no artifacts at root level.
	}
	
	/*  the resolvePath method has been removed
	@Test
	public void resolvePath() {
		FilesystemRepository fsr = testResourcesRepository();
		ArtifactResolutionPoint artifact = fsr.resolvePath("pom.xml");
		assertNotNull(artifact);
		
		ArtifactDescriptor descriptor = artifact.getDescriptor();
		assertThat(descriptor.getGroupId(), is("koncept.pheidippides"));
		assertThat(descriptor.getArtifactId(), is("pheidippides-test-parent"));
		assertThat(descriptor.getVersion(), is("1.0-SNAPSHOT"));
		
		artifact = fsr.resolvePath("test-proj/pom.xml");
		assertNotNull(artifact);
		
		descriptor = artifact.getDescriptor();
		assertThat(descriptor.getGroupId(), is("koncept.pheidippides"));
		assertThat(descriptor.getArtifactId(), is("pheidippides-test-child"));
		assertThat(descriptor.getVersion(), is("1.0-SNAPSHOT"));
	}
	*/
}
