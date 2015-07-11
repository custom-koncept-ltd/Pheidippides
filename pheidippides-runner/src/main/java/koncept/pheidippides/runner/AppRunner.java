package koncept.pheidippides.runner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import koncept.classloader.KonceptClassLoader;
import koncept.classloader.resource.ClasspathLocation;
import koncept.classloader.resource.ClasspathResource;
import koncept.classloader.resource.loader.location.JarLocation;
import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.ArtifactResource;
import koncept.pheidippides.Repository;
import koncept.pheidippides.file.FilesystemRepository;
import koncept.pheidippides.http.HttpRepository;

public class AppRunner {

	public static void main(String[] args) throws Exception {
		//mmm
		//I'm going with -D properties to define the process
		String groupId = System.getProperty("koncept.pheidippides.runner.groupId");
		String artifactId = System.getProperty("koncept.pheidippides.runner.artifactId");
		
		//TODO... work out how to not define this and just get 'LATEST'
		String version = System.getProperty("koncept.pheidippides.runner.version");
		
		String mainClass = System.getProperty("koncept.pheidippides.runner.class");
		
		resolveAndRun(new ArtifactDescriptor(groupId, artifactId, version), mainClass, args);
	}
	
	public static void resolveAndRun(ArtifactDescriptor target, String mainClass, String[] args) throws Exception {
		List<Repository> repositories = new ArrayList<Repository>();
		repositories.add(FilesystemRepository.getDefaultUserRepository());
		repositories.add(HttpRepository.mavenCentralRepository());
		
		//get the POM
		//suck its dependencies down
		//run it
		
		throw new UnsupportedOperationException();
	}
	
	public static void resolveAndRun(List<ArtifactDescriptor> dependencies, String mainClass, String[] args) throws Exception {
		List<Repository> repositories = new ArrayList<Repository>();
		repositories.add(FilesystemRepository.getDefaultUserRepository());
		repositories.add(HttpRepository.mavenCentralRepository());
		resolveAndRun(repositories, dependencies, mainClass, args);	
	}
	
	public static void resolveAndRun(List<Repository> repositories, List<ArtifactDescriptor> dependencies, String mainClass, String[] args) throws Exception {
		List<ArtifactResource> resources = new ArrayList<ArtifactResource>();
		for(ArtifactDescriptor dependency: dependencies) {
			resources.addAll(resolve(repositories, dependency));
		}
		run(resources, mainClass, args);
	}

	private static Collection<ArtifactResource> resolve(List<Repository> repositories, ArtifactDescriptor dependency) {
		for(Repository repository: repositories) try {
			ArtifactResolutionPoint resolvedArtifact = repository.resolve(dependency);
			if (!resolvedArtifact.getResources().isEmpty())
				return resolvedArtifact.getResources();
		} catch (Exception e) {
			//TODO: shouldn't throw exceptions for 'artifact does not exist'
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to resolve " + dependency);
	}
	
	public static void run(List<ArtifactResource> resources, String mainClass, String[] args) throws Exception {
		List<ClasspathLocation> classpath = new ArrayList<ClasspathLocation>();
		for(final ArtifactResource resource: resources) {
			
			boolean include = false;

			include |= resource.getType().equals("jar");
			
			if (include) {
				JarLocation jarLocation = new JarLocation(null, new ClasspathResource() {
					
					public String asURLString() {
						String urlString = "pheidippides://" +  resource.getDescriptor().toString();
						if (resource.getClassifier() != null)
							urlString = urlString + ":" + resource.getClassifier();
						urlString = urlString + "?type=" + resource.getType();
						return urlString;
					}
					
					public ClasspathLocation getClasspathLocation() {
						return null;
					}
					
					public InputStream getStream() throws IOException {
						return resource.open();
					}
					
				});
				classpath.add(jarLocation);
			}
			
		}
		KonceptClassLoader kcl = KonceptClassLoader.forLocations(classpath);
		
		Class c = kcl.loadClass(mainClass);
		Method main = c.getMethod("main", String[].class);
		if (args == null) args = new String[]{};
		main.invoke(null, new Object[]{args});
	}
	
}
