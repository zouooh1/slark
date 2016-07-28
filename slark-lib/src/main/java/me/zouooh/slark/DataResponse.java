package me.zouooh.slark;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import me.zouooh.slark.http.HttpStatus;

public class DataResponse {

	public DataResponse(int statusCode, InputStream data,
						Map<String, String> headers, int contentLength,DataSource dataSource) {
		this.statusCode = statusCode;
		this.data = data;
		this.headers = headers;
		this.contentLength = contentLength;
		this.dataSource = dataSource;
	}

	public DataResponse(InputStream data,DataSource dataSource) {
		this(HttpStatus.SC_OK, data, Collections.<String, String>emptyMap(),0,dataSource);
	}

	public DataResponse(InputStream data, Map<String, String> headers,DataSource dataSource) {
		this(HttpStatus.SC_OK, data, headers,0,dataSource);
	}

	public final int statusCode;

	public final InputStream data;

	public final Map<String, String> headers;

	public final int contentLength;

	public final DataSource dataSource;
}
