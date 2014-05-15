package koncept.pheidippides.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.file.FilesystemRepository;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ArtifactTrackerIT {

	//hmm... this could easily fail if you don't use maven defaults...
	//TODO: FilesystemRepository.getDefaultUserRepository() should pick up on non-default locations
	@Test
	public void validaterLocalRepository() {
//		System.out.println("location = " + FilesystemRepository.getDefaultUserRepository().getRepositoryUri());
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
	

	@Test
	public void artifactResolutionFilter() {
		ArtifactTracker pheidippidesArtifactsTracker = new ArtifactTracker(
				FilesystemRepository.getDefaultUserRepository(),
				Arrays.asList(new ArtifactTrackerSpec("koncept.pheidippides", "pheidippides-artifacts"))
				);
		ArtifactTracker pheidippidesTracker = new ArtifactTracker(
				FilesystemRepository.getDefaultUserRepository(),
				Arrays.asList(new ArtifactTrackerSpec("koncept.pheidippides"))
				);
		
		pheidippidesArtifactsTracker.update();
		pheidippidesTracker.update();
		Assert.assertThat(pheidippidesArtifactsTracker.getCurrentArtifacts().isEmpty(), CoreMatchers.is(false));
		Assert.assertThat(pheidippidesTracker.getCurrentArtifacts().isEmpty(), CoreMatchers.is(false));
		Assert.assertTrue(pheidippidesTracker.getCurrentArtifacts().size() > pheidippidesArtifactsTracker.getCurrentArtifacts().size());
		

		for(ArtifactDescriptor descriptor: pheidippidesArtifactsTracker.getCurrentArtifacts()) {
			System.out.println(descriptor);
		}
		System.out.println("##");
		for(ArtifactDescriptor descriptor: pheidippidesTracker.getCurrentArtifacts()) {
			System.out.println(descriptor);
		}
	}

}
