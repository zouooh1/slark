
package me.zouooh.slark.http;

import org.nutz.lang.Strings;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import me.zouooh.slark.request.Request;


/**
 * 
 * @author zouooh
 *
 */
public class UrlNetwork implements Network {
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private SSLSocketFactory sslSocketFactory;
	private Request request;

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	public UrlNetwork(Request request){
		this.request = request;
	}

	public HttpURLConnection _exe() throws IOException {
		if (request == null) {
			return null;
		}

		String url = request;
		URL parsedUrl = new URL(url);
		if (BurroDebug.dataEable()) {
			BurroDebug.dataf("[Network]:%s is connecting!", url);
		}
		HttpURLConnection connection = openConnection(parsedUrl, request);
		if (request.getHeaders() != null) {
			for (String headerName : request.getHeaders().keySet()) {
				connection.addRequestProperty(headerName, request.getHeaders()
						.get(headerName));
			}
		}
		connection.disconnect();
		setConnectionParametersForRequest(connection, request);
		return connection;
	}

	@Override
	public void performRequest(Request request) throws AntsException {
		if (request == null) {
			return;
		}
		while (true) {
			NetworkResponse networkResponse = null;
			OnNetworkResponse onNetworkResponse = request;
			Map<String, String> responseHeaders = new LinkedHashMap<String, String>();
			int statusCode = 0;
			HttpURLConnection httpsURLConnection = null;
			InputStream inputStream = null;
			try {
				httpsURLConnection = _exe(request);

				statusCode = httpsURLConnection.getResponseCode();
				if (statusCode == -1) {
					throw new IOException(
							"Could not retrieve response code from HttpUrlConnection.");
				}
				for (Entry<String, List<String>> entry : httpsURLConnection
						.getHeaderFields().entrySet()) {
					String header = entry.getKey();
					if (Strings.isBlank(header)) {
						continue;
					}
					if (header.equals("Set-Cookie")) {
						CookieStore cookieStore = request.getAntsQueue()
								.getCookieStore();
						for (String cookie : entry.getValue()) {
							if (cookieStore != null) {
								cookieStore.save(httpsURLConnection.getURL()
										.getHost(), cookie);
							}
						}
					} else {
						responseHeaders.put(header, entry.getValue().get(0));
					}
				}

				/*
				 * if (statusCode < 200 || statusCode > 299) { throw new
				 * IOException(); }
				 */

				try {
					inputStream = httpsURLConnection.getInputStream();
				} catch (Exception e) {

				}

				networkResponse = new NetworkResponse(statusCode, inputStream,
						responseHeaders);

				networkResponse.contentLengh = httpsURLConnection
						.getContentLength();
				onNetworkResponse.onNetResponse(networkResponse);
				break;
			} catch (SocketTimeoutException e) {
				attemptRetryOnException(request, new TimeoutError());
			} catch (MalformedURLException e) {
				throw new AntsException("Bad URL " + request.getUrl(), e);
			} catch (IOException e) {
				VolleyLog.e("Unexpected response code %d for %s", statusCode,
						request.getUrl());
				throw new NetworkError();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}

				if (httpsURLConnection != null) {
					httpsURLConnection.disconnect();
				}
			}
		}
	}

	protected HttpURLConnection createConnection(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}

	private HttpURLConnection openConnection(URL url, Request request)
			throws IOException {
		HttpURLConnection connection = createConnection(url);

		int timeoutMs = request.getTimeoutMs();
		connection.setConnectTimeout(timeoutMs);
		connection.setReadTimeout(timeoutMs);
		connection.setUseCaches(false);
		connection.setDoInput(true);

		// use caller-provided custom SslSocketFactory, if any, for HTTPS
		if ("https".equals(url.getProtocol()) && sslSocketFactory != null) {
			((HttpsURLConnection) connection)
					.setSSLSocketFactory(sslSocketFactory);
		}
		return connection;
	}

	private void setConnectionParametersForRequest(
			HttpURLConnection connection, Request request) throws IOException,
			AuthFailureError {
		CookieStore cookieStore = request.getAntsQueue().getCookieStore();
		if (cookieStore != null) {
			String sid = cookieStore.cookieOf(connection.getURL().getHost());
			if (sid != null) {
				connection.setRequestProperty("Cookie", sid);
			}
		}
		String token = UserService.service(request.getAntsQueue().getContext()).getToken();
		connection.setRequestProperty("Authorization", "Bearer "+token);
		switch (request.getMethod()) {
		case Method.GET:
			connection.setRequestMethod("GET");
			break;
		case Method.DELETE:
			connection.setRequestMethod("DELETE");
			break;
		case Method.POST:
			connection.setRequestMethod("POST");
			addBodyIfExists(connection, request);
			break;
		case Method.PUT:
			connection.setRequestMethod("PUT");
			addBodyIfExists(connection, request);
			break;
		case Method.HEAD:
			connection.setRequestMethod("HEAD");
			break;
		case Method.OPTIONS:
			connection.setRequestMethod("OPTIONS");
			break;
		case Method.TRACE:
			connection.setRequestMethod("TRACE");
			break;
		case Method.PATCH:
			connection.setRequestMethod("PATCH");
			addBodyIfExists(connection, request);
			break;
		default:
			throw new IllegalStateException("Unknown method type.");
		}
	}

	private void addBodyIfExists(HttpURLConnection connection, Request request)
			throws IOException, AuthFailureError {
		if (request instanceof AntsFilePost) {
			AntsFilePost antsPost = (AntsFilePost) request;
			//connection.setChunkedStreamingMode(8192);
			connection.setRequestProperty("Charsert", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + AntsFilePost.BOUNDARY);
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			antsPost.sendData(out);
			out.close();
			return;
		}
		byte[] body = request.getBody();
		if (body != null) {
			connection.setDoOutput(true);
			connection.addRequestProperty(HEADER_CONTENT_TYPE,
					request.getBodyContentType());
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.write(body);
			out.close();
		}
	}

	private void attemptRetryOnException(Request request,
			AntsException exception) throws AntsException {
		RetryPolicy retryPolicy = request.getRetryPolicy();
		try {
			retryPolicy.retry(exception);
		} catch (AntsException e) {
			throw e;
		}
	}

}
