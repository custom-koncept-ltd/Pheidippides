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
import koncept.pheidippides.http.lister.SimpleHtmlLister;
import koncept.pheidippides.listing.ContentListing;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestExecutor;
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

	private HttpRequestExecutor requestExecutor;
	private HttpCoreContext context;
	private HttpClientConnection conn;

	private ArtifactLister artifactLister;

	// details on maven index here:
	// http://www.jroller.com/eu/entry/maven_indexes

	public HttpRepository(URI repositoryUri) {
		this.repositoryUri = repositoryUri;

		conn = new DefaultManagedHttpClientConnection(getClass().getName(),
				8 * 1024);
		context = HttpCoreContext.create();
		requestExecutor = new HttpRequestExecutor();
		
		autodetectParserType();
	}

	public URI getRepositoryUri() {
		return repositoryUri;
	}

	public ArtifactResolutionPoint resolve(ArtifactDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArtifactResolutionPoint resolveChildModule(
			ArtifactDescriptor descriptor, String modulePath) {
		// TODO Auto-generated method stub
		return null;
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

	public ContentListing getContent(final String uri) {
		HttpRequestBase request = new HttpGet(uri);

		try {
			HttpResponse response = requestExecutor.execute(request, conn,
					context);

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
					String name = "";
					int lastIndex = uri.lastIndexOf("/");
					if (lastIndex > 0 && lastIndex < uri.length())
						name = uri.substring(lastIndex + 1);
					return name;
				}
				public InputStream openStream() throws IOException {
					return new ByteArrayInputStream(data);
				}
			};


		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (HttpException e) {
			throw new RuntimeException(e);
		}
	}

	protected Document httpCommonsGet(String uri) {
		HttpRequestBase request = new HttpGet(uri);

		try {
			HttpResponse response = requestExecutor.execute(request, conn,
					context);

			// response.getStatusLine().getStatusCode()

			HttpEntity responseEntity = response.getEntity();
			String html = EntityUtils.toString(responseEntity);

			return Jsoup.parse(html);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (HttpException e) {
			throw new RuntimeException(e);
		}
	}

}
