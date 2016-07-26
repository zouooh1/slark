package me.zouooh.slark.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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
	void stop();
	void dispatch(int state, Object object);
	void executeOnExecutor(Executor executor);
}
