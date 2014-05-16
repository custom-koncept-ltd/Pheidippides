package koncept.pheidippides.http;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactLister;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.ArtifactResource;
import koncept.pheidippides.listing.LocationListing;

import org.junit.Test;

public class HttpRepositoryIT {

/*
dump /koncept/http/koncept-legacy-mod/7/  http://repository.custom-koncept.co.uk/artifactory/public/koncept/http/koncept-legacy-mod/7/
   - koncept-legacy-mod-7-sources.jar
   - koncept-legacy-mod-7-sources.jar.md5
   - koncept-legacy-mod-7-sources.jar.sha1
   - koncept-legacy-mod-7.jar
   - koncept-legacy-mod-7.jar.md5
   - koncept-legacy-mod-7.jar.sha1
   - koncept-legacy-mod-7.pom
   - koncept-legacy-mod-7.pom.md5
   - koncept-legacy-mod-7.pom.sha1
 */
	@Test
	public void resolveFullyQualifiedArtifact() throws Exception {
		HttpRepository repo = HttpRepository.konceptRepository();
//		ArtifactDescriptor descriptor = new ArtifactDescriptor("koncept.pheidippides", "pheidippides-artifacts", "1.0-SNAPSHOT");
		ArtifactDescriptor descriptor = new ArtifactDescriptor("koncept.http", "koncept-legacy-mod", "7");
		ArtifactResolutionPoint resolved = repo.resolve(descriptor);		
		assertThat(resolved, notNullValue());
		assertThat(resolved.getDescriptor(), is(descriptor));
		
		assertThat(resolved.getParent().toString(), is("koncept-legacy-mod:7"));

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
	
	@Test
	public void browseRepo() throws Exception {
		HttpRepository repo = HttpRepository.konceptRepository();
		assertTrue(repo.isListableRepository());
		ArtifactLister lister = repo.getArtifactLister();
		assertThat(lister, notNullValue());
		LocationListing location = lister.getRootSearchLocation();
		
//		dump(location);
		
		
	}
	
	private void dump(LocationListing listing) {
		System.out.println("dump " + listing.getListingPath() + "  " + listing.getLocation());
		for(String contentName : listing.listContents()) {
			System.out.println("   - " + contentName);
		}
		for(LocationListing child: listing.search()) {
			dump(child);
		}
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
