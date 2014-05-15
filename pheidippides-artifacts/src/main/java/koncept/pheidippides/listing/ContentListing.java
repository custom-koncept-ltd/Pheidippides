package koncept.pheidippides.listing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface ContentListing {
	public URI location();
	public String name();
	public InputStream openStream() throws IOException;
}