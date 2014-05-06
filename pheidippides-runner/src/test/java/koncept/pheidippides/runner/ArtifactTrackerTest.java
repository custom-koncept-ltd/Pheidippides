package koncept.pheidippides.runner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.file.FilesystemRepository;

import org.junit.Assert;
import org.junit.Test;

public class ArtifactTrackerTest {

	//hmm... this could easily fail if you don't use maven defaults...
	//TODO: FilesystemRepository.getDefaultUserRepository() should pick up on non-default locations
	@Test
	public void validaterLocalRepository() {
		FilesystemRepository localUserRepository = FilesystemRepository.getDefaultUserRepository();
//		System.out.println("location = " + localUserRepository.getRepositoryUri());
		ArtifactTracker tracker = new ArtifactTracker(
				FilesystemRepository.getDefaultUserRepository(),
				new ArrayList<ArtifactTrackerSpec>()
				);
		
		tracker.update();
		
		Assert.assertFalse(tracker.getCurrentArtifacts().isEmpty()); //local repo CAN'T be empty

		Set<ArtifactDescriptor> validatedDescriptors = new HashSet<ArtifactDescriptor>();
		
		//foibles:
//		C:\Users\koncept\.m2\repository\de\fau\cs\osr\ptk\parser-toolkit\1.1.1-SNAPSHOT
//		C:\Users\koncept\.m2\repository\koncept\http\koncept-full\
		
		for(ArtifactDescriptor descriptor: tracker.getCurrentArtifacts()) {
//			System.out.println(descriptor);
			Assert.assertFalse("Descriptor already exists: " + descriptor, validatedDescriptors.contains(descriptor));
			validatedDescriptors.add(descriptor);
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
	@Test
	public void resolvingArtifacts() {
		//resolve a KNOWN artifact
		
		//should be able to get a list of TYPES of artifacts to download
		
//		>> jar
//		>> pom
//		>> ?? how to handle flavours?
		
		//need a <type, classifier> list per artifact descriptor
		
		
		
	}
	
}
