package me.zouooh.slark.response;

import me.zouooh.slark.request.Request;

/**
 * 请求进度指示接口
 * 
 * @author zouooh
 * 
 */
public interface Progress {

	/**
	 * 请求开始
	 */
	void onRequestStart(Request antsRequest);

	/**
	 * 请求结束
	 */
	void onRequestEnd(Request antsRequest);
	
	/**
	 * 请求成功
	 */
	void onRequestSucess(Request antsRequest);
	
	/**
	 * 请求失败
	 */
	void onRequestFailure(Request antsRequest, Throwable throwable);

	/**
	 * 请求进度
	 * 
	 * @param index
	 *            如果是上传多个文件为顺序号，其他为0
	 * @param current
	 *            当前处理字节数
	 * @param total
	 *            字节总数
	 */
	void onLoading(int index, int current, int total);

	/**
	 * 提示文字
	 * 
	 * @param msg
	 */
	void tip(String msg);
}
