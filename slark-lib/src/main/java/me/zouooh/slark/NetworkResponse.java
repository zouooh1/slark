package me.zouooh.slark;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import me.zouooh.slark.http.HttpStatus;

public class NetworkResponse {

	public NetworkResponse(int statusCode, InputStream data,
			Map<String, String> headers,int contentLength) {
		this.statusCode = statusCode;
		this.data = data;
		this.headers = headers;
		this.contentLength = contentLength;
	}

	public NetworkResponse(InputStream data) {
		this(HttpStatus.SC_OK, data, Collections.<String, String>emptyMap(),0);
	}

	public NetworkResponse(InputStream data, Map<String, String> headers) {
		this(HttpStatus.SC_OK, data, headers,0);
	}

	public final int statusCode;

	public final InputStream data;

	public final Map<String, String> headers;

	public final int contentLength;
}
