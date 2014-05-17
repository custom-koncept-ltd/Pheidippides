package koncept.pheidippides.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import koncept.pheidippides.ArtifactDescriptor;
import koncept.pheidippides.ArtifactLister;
import koncept.pheidippides.ArtifactResolutionPoint;
import koncept.pheidippides.Repository;
import koncept.pheidippides.artifact.DefaultArtifactResolutionPoint;
import koncept.pheidippides.artifact.MavenPomDescriptor;
import koncept.pheidippides.http.lister.SimpleHtmlLister;
import koncept.pheidippides.listing.ContentListing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * this class is a representation of a http repository.<br/>
 * By default, it can only do the basics, but it may be able to detect a way to
 * do more than just that, for some server products.
 * 
 * artifactory - building nexus - coming
 * 
 * @author koncept
 * 
 */
public class HttpRepository implements Repository {

	public static HttpRepository mavenCentralRepository()
			throws URISyntaxException {
		HttpRepository repository = new HttpRepository(new URI(
				"http://repo1.maven.org/maven/"));
		return repository;
	}

	public static HttpRepository konceptRepository() throws URISyntaxException {
		HttpRepository repository = new HttpRepository(new URI(
				"http://repository.custom-koncept.co.uk/artifactory/public/"));
		return repository;
	}

	private final URI repositoryUri;
	private HttpClient client;
	private ArtifactLister artifactLister;

	// details on maven index here:
	// http://www.jroller.com/eu/entry/maven_indexes

	public HttpRepository(URI repositoryUri) {
		this.repositoryUri = repositoryUri;
		autodetectParserType();
	}

	public URI getRepositoryUri() {
		return repositoryUri;
	}

	public ArtifactResolutionPoint resolve(ArtifactDescriptor descriptor) {
		try {
			String uri = repositoryUri.toString();
			String path = descriptor.toPath();
			String fileName = descriptor.getArtifactId() + "-" + descriptor.getVersion() + ".pom";
			if (uri.endsWith("/")) path = path.substring(1); //trim the leading slash if required
			
			ContentListing content = getContent(uri + path + fileName, fileName);
			MavenPomDescriptor pomDescriptor = new MavenPomDescriptor(content.location(), content.openStream());
			
			if (!descriptor.equals(pomDescriptor.getDescriptor()))
				throw new RuntimeException("unexpected artifact descriptor: got " + pomDescriptor.getDescriptor() + " but expected " + descriptor);
			return new DefaultArtifactResolutionPoint(artifactLister, new URI(uri + path), pomDescriptor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isListableRepository() {
		return artifactLister != null;
	}

	public ArtifactLister getArtifactLister() {
		return artifactLister;
	}

	/**
	 * Performs a http get on the root URI of the repository in order to figure
	 * out what sort of repository this is. <br/>
	 */
	/*
	 * TODO: convert method use a provider list ad match method
	 */
	protected void autodetectParserType() {
		Document doc = get(repositoryUri.toString());

		Elements e = doc.select("address");
		if (e.size() == 1) {
			if (e.get(0).text().startsWith("Artifactory/")) {
				artifactLister = new SimpleHtmlLister(this);
			}
		}

	}

	public Document get(String uri) {
		try {
			return Jsoup.connect(uri).get();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ContentListing getContent(final String uri, final String name) {
		HttpRequestBase request = new HttpGet(uri);
		try {
			HttpClient client = getClient();
			
			HttpResponse response = client.execute(request);

			// response.getStatusLine().getStatusCode()

			HttpEntity responseEntity = response.getEntity();
			final byte[] data = EntityUtils.toByteArray(responseEntity);
			
			return new ContentListing() {
				public URI location() {
					try {
						return new URI(uri);
					} catch (URISyntaxException e) {
						throw new RuntimeException(e);
					}
				}
				public String name() {
					return name;
				}
				public InputStream openStream() throws IOException {
					return new ByteArrayInputStream(data);
				}
			};


		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private HttpClient getClient() {
		if (client == null) client = HttpClients.custom()
				.setUserAgent("koncept.pheidippides/1.0-SNAPSHOT")
				.build();
		return client;
	}

}
