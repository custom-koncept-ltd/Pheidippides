package koncept.pheidippides;

import java.io.IOException;
import java.io.InputStream;

public interface ArtifactResource {

	/**
	 * @return the owning resolved artifacts artifact descriptor
	 */
	public ArtifactDescriptor getDescriptor();
	
	/**
	 * typically, the type of the artifact desired is .jar (and possibly .zip)<br/>
	 * .pom will also feature, but will only be a stream of the artifact pom file.
	 * 
	 * @return the type of the artifact resource
	 */
	public String getType();
	
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
