package koncept.pheidippides.runner;

import java.util.ArrayList;
import java.util.List;

import koncept.classloader.KonceptClasspathEcho;
import koncept.pheidippides.ArtifactDescriptor;

import org.junit.Test;

public class AppRunnerTest {

	
	@Test(expected=ClassNotFoundException.class)
	public void noDependenciesDefined() throws Exception {
		List<ArtifactDescriptor> dependencies = new ArrayList<ArtifactDescriptor>();
		AppRunner.resolveAndRun(dependencies, KonceptClasspathEcho.class.getName(), null);
		
	}
	
	@Test
	public void konceptClasspathEchoTest() throws Exception {
		List<ArtifactDescriptor> dependencies = new ArrayList<ArtifactDescriptor>();
		dependencies.add(new ArtifactDescriptor("koncept.classloader", "classloader", "1.0-SNAPSHOT"));
		AppRunner.resolveAndRun(dependencies, KonceptClasspathEcho.class.getName(), null);
		
	}
}
