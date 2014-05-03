package koncept.pheidippides;

import java.util.List;

public interface SearchableRepository extends Repository {

	public List<SearchLocation> getRootSearchLocation();
	
}
