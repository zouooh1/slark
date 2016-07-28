package me.zouooh.slark.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import me.zouooh.slark.DataSource;
import me.zouooh.slark.Logs;
import me.zouooh.slark.DataResponse;
import me.zouooh.slark.Slark;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.http.HttpHeaderParser;
import me.zouooh.slark.request.Request;

public  class TextResponse implements Response<String> {


	@Override
	public String adpter(Request request, DataResponse networkResponse) throws SlarkException, IOException {
		InputStream inputStream = networkResponse.data;
		String parsed;
		byte[] data = null;
		ByteArrayOutputStream os = null;
		try {
			// 创建字节输出流对象
			os = new ByteArrayOutputStream();
			// 定义读取的长度
			int len = 0;
			// 定义缓冲区
			byte buffer[] = new byte[1024];
			// 按照缓冲区的大小，循环读取
			while ((len = inputStream.read(buffer)) != -1) {
				// 根据读取的长度写入到os对象中
				os.write(buffer, 0, len);
			}
			data = os.toByteArray();
			parsed = new String(data,HttpHeaderParser.parseCharset(networkResponse.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(data);
		} finally {
			if (os!=null){
				try {
					os.close();
				}catch (IOException e){

				}
			}
		}
		return parsed;
	}

	@Override
	public void onRequestSuccess(Request request, String s) {
		if (Slark.DEBUG_DATAS){
			Logs.dd(s);
		}
	}

	@Override
	public void onRequestFailure(Request request, Throwable throwable) {
		if (Slark.DEBUG_DATAS){
			Logs.dd(throwable+"");
		}
	}
}
