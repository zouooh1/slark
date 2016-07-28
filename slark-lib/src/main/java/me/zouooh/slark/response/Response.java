package me.zouooh.slark.response;

import java.io.IOException;

import me.zouooh.slark.DataSource;
import me.zouooh.slark.NetworkResponse;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.request.Request;

/**
 * 异步数据处理接口
 * @author zouooh
 * @param <T>
 */
public interface Response<T> {
	
	
	T adpter(Request request, NetworkResponse networkResponse, DataSource dataSource) throws SlarkException,IOException;
	
	/**
	 * 请求成功
	 * @param request
	 * @param t
	 */
	void onRequestSuccess(Request request, T t);
	/**
	 * 请求失败
	 * @param request
	 * @param throwable
	 */
	void onRequestFailure(Request request, Throwable throwable);
	
}
