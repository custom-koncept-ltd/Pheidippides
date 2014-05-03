package koncept.pheidippides.artifact;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URL;

import koncept.pheidippides.ArtifactDescriptor;

import org.junit.Test;

public class MavenVersionMetadataTest {

	@Test
	public void artifactDescriptor() throws Exception {
		URL resource = getClass().getResource("/test-maven-metadata-local-1.xml");
		MavenVersionMetadata meta = new MavenVersionMetadata(resource.toURI(), resource.openStream());
		
		ArtifactDescriptor descriptor = meta.getArtifactDescriptor();
		assertNotNull(descriptor);
		assertThat(descriptor.getGroupId(), is("koncept.pheidippides"));
		assertThat(descriptor.getArtifactId(), is("pheidippides-repository-1"));
		assertThat(descriptor.getVersion(), is("1.0-SNAPSHOT"));
	}
	
	@Test
	public void perExtensionVersionRemapping() throws Exception {
		URL resource = getClass().getResource("/test-maven-metadata-local-1.xml");
		MavenVersionMetadata meta = new MavenVersionMetadata(resource.toURI(), resource.openStream());
		
		assertThat(meta.getSubstituteVersionSuffixToUse("pom"), is("1.0-SNAPSHOT"));
		assertThat(meta.getSubstituteVersionSuffixToUse("jar"), is("1.0-SNAPSHOT[with qualifier]"));
		assertThat(meta.getSubstituteVersionSuffixToUse("unknown"), is("1.0-SNAPSHOT"));
	}
	
	@Test
	public void alternateVersioningInformation() throws Exception {
		URL resource = getClass().getResource("/test-maven-metadata-local-2.xml");
		MavenVersionMetadata meta = new MavenVersionMetadata(resource.toURI(), resource.openStream());
		
		ArtifactDescriptor descriptor = meta.getArtifactDescriptor();
		assertNotNull(descriptor);
		assertThat(descriptor.getGroupId(), is("koncept.pheidippides"));
		assertThat(descriptor.getArtifactId(), is("pheidippides-repository-2"));
		assertThat(descriptor.getVersion(), is("1.0-SNAPSHOT"));
	}
}
