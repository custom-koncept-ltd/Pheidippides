package koncept.pheidippides.artifact;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;

import org.junit.Test;

public class MavenPomDescriptorTest {

	@Test
	public void descriptorTest() throws Exception {
		URL resource = getClass().getResource("/pom.xml");
		MavenPomDescriptor pd = new MavenPomDescriptor(resource.toURI(), resource.openStream());
		ArtifactDescriptor descriptor = pd.getDescriptor();
		assertNotNull(descriptor);
		assertThat(descriptor.getGroupId(), is("koncept.pheidippides"));
		assertThat(descriptor.getArtifactId(), is("pheidippides-test-parent"));
		assertThat(descriptor.getVersion(), is("1.0-SNAPSHOT"));
	}
	
	@Test
	public void childDescriptorTest() throws Exception {
		URL resource = getClass().getResource("/pom.xml");
		MavenPomDescriptor pd = new MavenPomDescriptor(resource.toURI(), resource.openStream());
		List<String> children = pd.getChildModules();
		assertNotNull(children);
		assertThat(children.size(), is(1));
		assertThat(children.get(0), is("test-proj"));
	}
	
	@Test
	public void noParentTest() throws Exception {
		URL resource = getClass().getResource("/pom.xml");
		MavenPomDescriptor pd = new MavenPomDescriptor(resource.toURI(), resource.openStream());
		ArtifactDescriptor descriptor = pd.getParent();
		assertNull(descriptor);
	}
	
}
