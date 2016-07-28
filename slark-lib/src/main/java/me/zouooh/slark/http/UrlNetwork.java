
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

import me.zouooh.slark.Logs;
import me.zouooh.slark.NetworkResponse;
import me.zouooh.slark.Slark;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.request.Request;
import me.zouooh.slark.request.RetryPolicy;


/**
 * @author zouooh
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

    public UrlNetwork(Request request) {
        this.request = request;
    }

    public HttpURLConnection _exe() throws IOException {
        if (request == null) {
            return null;
        }
        URL parsedUrl = request.requestURL();
        Logs.d("%s is connecting!", parsedUrl);
        HttpURLConnection connection = openConnection(parsedUrl, request);
        if (request.getHeaders() != null) {
            for (String headerName : request.getHeaders().keySet()) {
                connection.addRequestProperty(headerName, request.getHeaders()
                        .get(headerName));
            }
        }
        setConnectionParametersForRequest(connection, request);
        return connection;
    }

    private  HttpURLConnection httpsURLConnection ;

    @Override
    public void close(){
        if (httpsURLConnection!=null){
            httpsURLConnection.disconnect();
        }
    }

    @Override
    public NetworkResponse open() throws SlarkException {
        if (request == null) {
            return null;
        }
        while (true) {
            int statusCode = 0;
            try {
                Map<String, String> responseHeaders = new LinkedHashMap<>();
                InputStream inputStream = null;
                httpsURLConnection = _exe();
                statusCode = httpsURLConnection.getResponseCode();
                if (statusCode == -1) {
                    throw new IOException(
                            "Could not retrieve response code from HttpUrlConnection.");
                }
                int contentLength = httpsURLConnection.getContentLength();
                for (Entry<String, List<String>> entry : httpsURLConnection
                        .getHeaderFields().entrySet()) {
                    String header = entry.getKey();
                    if (Strings.isBlank(header)) {
                        continue;
                    }
                    responseHeaders.put(header, entry.getValue().get(0));
                }

                try {
                    inputStream = httpsURLConnection.getInputStream();
                } catch (IOException e) {
                }

                NetworkResponse networkResponse = new NetworkResponse(statusCode, inputStream,
                        responseHeaders,contentLength);
                return networkResponse;
            } catch (SocketTimeoutException e) {
                attemptRetryOnException(request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new SlarkException("Bad URL " + request.requestURL(), e);
            } catch (IOException e) {
                Logs.d("Unexpected response code %d for %s", statusCode,
                        request.getUrl());
                throw new NetworkError();
            }
        }
    }

    private HttpURLConnection openConnection(URL url, Request request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        if ("https".equals(url.getProtocol()) && sslSocketFactory != null) {
            ((HttpsURLConnection) connection)
                    .setSSLSocketFactory(sslSocketFactory);
        }
        return connection;
    }

    private void setConnectionParametersForRequest(HttpURLConnection connection, Request request) throws IOException {
        switch (request.getMethod()) {
            case Request.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private void addBodyIfExists(HttpURLConnection connection, Request request)
            throws IOException {
        boolean noFile = !request.hasFile();
        if (noFile) {
            byte[] body = request.getBody();
            if (body != null) {
                connection.setDoOutput(true);
                connection.addRequestProperty(HEADER_CONTENT_TYPE,
                        request.getBodyContentType());
                DataOutputStream out = new DataOutputStream(
                        connection.getOutputStream());
                try {
                    out.write(body);
                } finally {
                    out.close();
                }
            }
        } else {
            connection.setChunkedStreamingMode(8192);
            connection.setRequestProperty("Charsert", request.getParamsEncoding());
            connection.setRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            try {
                request.sendData(out);
            } finally {
                out.close();
            }
        }
    }

    private void attemptRetryOnException(Request request, SlarkException exception) throws SlarkException {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        try {
            retryPolicy.retry(exception);
        } catch (SlarkException e) {
            throw e;
        }
    }

}
