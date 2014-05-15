package koncept.pheidippides.http.lister;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactLister;
import koncept.pheidippides.http.HttpRepository;
import koncept.pheidippides.listing.ContentListing;
import koncept.pheidippides.listing.LocationListing;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SimpleHtmlLister implements ArtifactLister {

	private final HttpRepository repo;
	
	public SimpleHtmlLister(HttpRepository repo) {
		this.repo = repo;
	}
	public Document get(String uri) {
		return repo.get(uri);
	}
	
	public URI getRepositoryUri() {
		return repo.getRepositoryUri();
	}
	
	public ContentListing getContent(String uri) {
		return repo.getContent(uri);
	}
	
	public LocationListing getRootSearchLocation() {
		return new SimpleHtmlListing(getRepositoryUri());
	}

	public LocationListing getSearchLocation(String repositoryPath) {
		if (!repositoryPath.startsWith("/"))
			throw new RuntimeException("repository paths must be absolute");
		String location = getRepositoryUri().toString();
		if (location.endsWith("/"))
			location = location.substring(0, location.length() - 1);
		try {
			return new SimpleHtmlListing(new URI(location + repositoryPath));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

 class SimpleHtmlListing implements LocationListing {
		private final URI location;
		private final List<String> relativePathsFound;
		private final List<String> contents;
		public SimpleHtmlListing(URI location) {
			this.location = location;
			relativePathsFound = new ArrayList<String>();
			contents = new ArrayList<String>();
			
			Document doc = get(location.toString());
			Elements aTags = doc.select("a");
			for(Element a: aTags) {
				String href = a.attr("href");
				if (href != null && !href.equals("../")) {
					if (href.endsWith("/"))
						relativePathsFound.add(href);
					else
						contents.add(href);
				}		
			}
		}
		
		public List<LocationListing> search() {
			List<LocationListing> listing = new ArrayList<LocationListing>();
			String location = getLocation().toString();
			for(String path: relativePathsFound) {
				try {
					listing.add(new SimpleHtmlListing(new URI(location +  path)));
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
			return listing;
		}
		
		public List<ArtifactDescriptor> getArtifactDescriptors() {
			throw new UnsupportedOperationException();
		}
		
		public URI getLocation() {
			return location;
		}
		
		public String getListingPath() {
			URI repositoryUri = getRepositoryUri();
			if (location.equals(repositoryUri))
				return "/";
			
			String repoLocation = repositoryUri.toString();
			String thisLocation = location.toString();
			
			return "/" + thisLocation.substring(repoLocation.length());
		}
		
		public List<String> listContents() {
			return contents;
		}
		
		public ContentListing getContents(String name) {
			String contentLocation = getLocation().toString();
			return getContent(contentLocation + name);
		}
		
	}
	
}
