package koncept.pheidippides;

import java.io.IOException;
import java.io.InputStream;

public interface ArtifactResource {

	/**
	 * @return the owning resolved artifacts artifact descriptor
	 */
	public ArtifactDescriptor getDescriptor();
	
	/**
	 * resources often have a classifier<br/>
	 * The ones we will be interested in will.<br/>
	 * <br/>
	 * Classifiers like 'sources', 'javadoc' and 'userdocs' are what we are typically after. 
	 * 
	 * @return the classifier, or null
	 */
	public String getClassifier();
	
	/**
	 * @return inputstream of the underlying resource
	 * @throws IOException
	 */
	public InputStream open() throws IOException;
}
