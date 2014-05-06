package koncept.pheidippides;

import java.util.List;

public interface ListableRepository extends Repository {

	public List<LocationListing> getRootSearchLocation();
	
}
