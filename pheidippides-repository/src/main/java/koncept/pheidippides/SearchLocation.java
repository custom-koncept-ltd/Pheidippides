package koncept.pheidippides;

import java.util.List;

public interface SearchLocation {

	public List<SearchLocation> search();
	
	public List<ResolvedArtifact> getArtifacts();
	
}
