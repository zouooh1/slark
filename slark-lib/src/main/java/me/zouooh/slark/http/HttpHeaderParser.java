/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zouooh.slark.http;

import java.util.Map;

/**
 * Utility methods for parsing HTTP headers.
 */
public class HttpHeaderParser {
	
	
	 public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONTENT_LEN = "Content-Length";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONTENT_TYPE = "Content-Type";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONTENT_ENCODING = "Content-Encoding";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String EXPECT_DIRECTIVE = "Expect";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONN_DIRECTIVE = "Connection";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String TARGET_HOST = "Host";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String USER_AGENT = "User-Agent";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String DATE_HEADER = "Date";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String SERVER_HEADER = "Server";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String EXPECT_CONTINUE = "100-continue";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONN_CLOSE = "Close";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CONN_KEEP_ALIVE = "Keep-Alive";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CHUNK_CODING = "chunked";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String IDENTITY_CODING = "identity";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String UTF_8 = "UTF-8";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String UTF_16 = "UTF-16";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String US_ASCII = "US-ASCII";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String ASCII = "ASCII";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String ISO_8859_1 = "ISO-8859-1";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String DEFAULT_PROTOCOL_CHARSET = "US-ASCII";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String OCTET_STREAM_TYPE = "application/octet-stream";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String PLAIN_TEXT_TYPE = "text/plain";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String CHARSET_PARAM = "; charset=";
	  
	  // Field descriptor #18 Ljava/lang/String;
	  public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	


    /**
     * Parse date in RFC1123 format, and return its value as epoch
     */
    public static long parseDateAsEpoch(String dateStr) {
       
            // Parse date in RFC1123 format if this header contains one
            //return DateUtils.parseDate(dateStr).getTime();
        return 0;
    }

    /**
     * Retrieve a charset from headers
     *
     * @param headers An {@link Map} of headers
     * @param defaultCharset Charset to return if none can be found
     * @return Returns the charset specified in the Content-Type of this header,
     * or the defaultCharset if none can be found.
     */
    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get(CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(Map<String, String> headers) {
        return parseCharset(headers, DEFAULT_CONTENT_CHARSET);
    }
}
