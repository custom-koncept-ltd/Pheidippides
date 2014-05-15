package koncept.pheidippides.file;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.ArtifactResource;

import org.junit.Test;

public class FilesystemRepositoryIT {

	@Test
	public void resolveFullyQualifiedArtifact() {
		FilesystemRepository fsr = FilesystemRepository.getDefaultUserRepository();
		ArtifactDescriptor descriptor = new ArtifactDescriptor("koncept.pheidippides", "pheidippides-artifacts", "1.0-SNAPSHOT");
		ArtifactResolutionPoint resolved = fsr.resolve(descriptor);		
		assertThat(resolved, notNullValue());
		assertThat(resolved.getDescriptor(), is(descriptor));
		
		assertThat(resolved.getParent().toString(), is("koncept.pheidippides:pheidippides-parent:1.0-SNAPSHOT"));

		//main, sources and javadoc should be available
		Collection<ArtifactResource> resources = resolved.getResources();
		assertFalse(resources.isEmpty());
		
//		for(ArtifactResource resource: resources) {
//			System.out.println(resource.getDescriptor() + " - " + resource.getClassifier() + " " + resource.getType());
//		}
		
		
		//should be able to get a list of TYPES of artifacts to download
		
//		>> jar
//		>> pom
//		>> ?? how to handle flavours?
		
		//need a <type, classifier> list per artifact descriptor
		
		
//		fsr.resolve(descriptor)
	}
	
/*
koncept.http:koncept-full:1.0-SNAPSHOT
koncept.http:koncept-legacy-mod:1.0-SNAPSHOT
koncept.http:koncept-legacy-mod:7
koncept.http:koncept-router:1.0-SNAPSHOT
koncept.http:koncept.http-parent:1.0-SNAPSHOT
koncept.http:sun-legacy:7
koncept.kwiki:kwiki-core:1.0-SNAPSHOT
koncept.kwiki:kwiki-http-server:1.0-SNAPSHOT
koncept.kwiki:kwiki-maven-plugin:1.0-SNAPSHOT
koncept.kwiki:kwiki-parent:1.0-SNAPSHOT
koncept.pheidippides:pheidippides-artifacts:1.0-SNAPSHOT
koncept.pheidippides:pheidippides-parent:1.0-SNAPSHOT
koncept.pheidippides:pheidippides-repository:1.0-SNAPSHOT
koncept.sp:koncept-sp-parent:1.0-SNAPSHOT
 */
	
}
