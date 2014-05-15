package koncept.pheidippides.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import koncept.pheidippides.listing.ContentListing;

public class FileContentListing implements ContentListing {
	
	private final File file;
	
	public FileContentListing(File file) {
		this.file = file;
	}

	public URI location() {
		return file.toURI();
	}

	public String name() {
		return file.getName();
	}

	public InputStream openStream() throws IOException {
		return new FileInputStream(file);
	}

}
