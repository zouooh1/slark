package me.zouooh.slark.response;

import org.nutz.json.Json;
import org.nutz.lang.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import me.zouooh.common.Record;
import me.zouooh.slark.DataResponse;
import me.zouooh.slark.Logs;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.http.HttpHeaderParser;
import me.zouooh.slark.request.Request;

public abstract class JsonResponse implements Response<Record>{

	@Override
	public Record adpter(Request request, DataResponse networkResponse) throws SlarkException, IOException {
		InputStream inputStream = networkResponse.data;
		String parsed ;
		byte[] data = null; 
		try {
			// 创建字节输出流对象  
            ByteArrayOutputStream os = new ByteArrayOutputStream();  
            // 定义读取的长度  
            int len = 0;  
            // 定义缓冲区  
            byte buffer[] = new byte[1024];  
            // 按照缓冲区的大小，循环读取  
            while ((len = inputStream.read(buffer)) != -1) {  
                // 根据读取的长度写入到os对象中  
                os.write(buffer, 0, len);  
            }  
            // 释放资源    
            os.close(); 
            data = os.toByteArray();
			parsed = new String(data,
					HttpHeaderParser.parseCharset(networkResponse.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(data);
		}
		Record record = null;
		if (Strings.isBlank(parsed)) {
			throw new ParseError();
		}
		Logs.dd(parsed);
		Map<String, Object> object = Json.fromJsonAsMap(Object.class,parsed);
		if (object != null) {
			record = new Record(object);
		}
		return record;
	}
}
