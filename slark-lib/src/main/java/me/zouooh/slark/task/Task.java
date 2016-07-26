package me.zouooh.slark.task;

import me.zouooh.slark.request.Request;

/**
 * 后台任务
 * @author zouooh
 *
 */
public interface Task{
	public static interface TaskLisnter{
		void onStart(Request request);
		void onEnd(Request request);
	}
	int STATE_START = 1;
	int STATE_END = 2;
	int STATE_SUCCESS = 3;
	int STATE_FAILURE = 4;
	int STATE_LOADING = 5;
	/**
	 * 任务中止
	 */
	void stop();
	/**
	 * 通知UI线程
	 * @param state
	 * @param object
	 */
	void dispatch(int state, Object object);
}
