package me.zouooh.slark.task.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.os.Build;
import me.zouooh.slark.http.Network;
import me.zouooh.slark.request.Request;
import me.zouooh.slark.task.Task;

/**
 * 请求队列
 * 
 * @author zouooh
 * 
 */
public class SlarkQueue
		implements Task.TaskLisnter {
	private ExecutorService executor = null;
	private Context context;
	private File baseFile;
	private Network network = null;
	private String host;
	private String fileHost;
	private List<Request> requests = new LinkedList<Request>();
	private boolean resume = false;

	public SlarkQueue(Context context) {
		this.context = context;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * 设置线程组<br>
	 * 如果为空，会使用默认线程组。
	 * 
	 * @param executor
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * 执行一个请求
	 * 
	 * @param antsRequest
	 * @param isDelegate
	 * @return
	 */
	public void executeTask(Request request) {
		if (this.executor == null || request == null) {
			return;
		}
		submitTask(request, false);
		executeTaskBack(request);
	}

	/**
	 * 执行一个请求
	 * 
	 * @param antsRequest
	 * @param isDelegate
	 * @return
	 */
	public void executeTaskBack(Request request) {
		if (this.executor == null || request == null) {
			return;
		}
		SlarkTask antsTask = new SlarkTask(request, this);
		request.setTask(antsTask);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			antsTask.executeOnExecutor(executor);
		} else {
			antsTask.execute();
		}
	}

	/**
	 * 添加一个请求，不会立即执行。
	 * 
	 * @param b
	 * 
	 * @param antsRequest
	 */
	public void submitTask(Request request) {
		submitTask(request, true);
	}

	/**
	 * 添加一个请求，不会立即执行。
	 * 
	 * @param b
	 * 
	 * @param antsRequest
	 */
	public void submitTask(Request request, boolean b) {
		synchronized (this) {
			if (requests == null) {
				return;
			}
			if (!requests.contains(request)) {
				requests.add(request);
			}
			if (b) {
				if (resume) {
					request.requestOnAntsQueue(this);
				}
			}
		}
	}

	public void destory(Request request) {
		synchronized (this) {
			request.pause();
			request.destroy();
			if (requests == null) {
				return;
			}
			requests.remove(request);
		}
	}

	/**
	 * 生命期，重新开始<br>
	 * 队列里的请求如果需要，会重新执行一次。<br>
	 * 配合onResume();
	 */
	public void resume() {
		synchronized (this) {
			resume = true;
			for (Request request : requests) {
				request.requestOnAntsQueue(this);
			}
		}
	}

	/**
	 * 生命期，暂停<br>
	 * 队列里未完成的请求全部暂停。<br>
	 * 配合onPause();
	 */
	public void pause() {
		synchronized (this) {
			resume = false;
			for (Request antsRequest : requests) {
				antsRequest.pause();
			}
		}
	}

	/**
	 * 生命期，释放<br>
	 * 队列里所有的请求清空。该队列不可再重新使用<br>
	 * 配合onPause();
	 */
	public void destory() {
		synchronized (this) {
			for (Request request : requests) {
				request.destroy();
			}
			requests.clear();
			requests = null;
			executor = null;
		}
	}

	/**
	 * 不可手动调用
	 */
	@Override
	public void onStart(Request request) {
	}

	/**
	 * 不可手动调用
	 */
	@Override
	public void onEnd(Request request) {
		synchronized (this) {
			if (request == null) {
				return;
			}
			if (requests == null) {
				return;
			}
			if (requests.contains(request)) {
				requests.remove(request);
			}
		}
	}

	public Network getNetwork() {
		if (network == null) {
			network = new UrlNetwork();
		}
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public File baseDir() {
		if (baseFile == null) {
			baseFile = AppUtils.getExternalCacheDir(getContext());
			if (baseFile != null && !baseFile.exists()) {
				if (!baseFile.mkdirs()) {
					baseFile = getContext().getCacheDir();
				}
			}
		}
		return baseFile;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Context getContext() {
		return context;
	}

	public String getFileHost() {
		return fileHost;
	}

	public void setFileHost(String fileHost) {
		this.fileHost = fileHost;
	}

	public CookieStore getCookieStore() {
		if (cookieStore == null) {
			cookieStore = new CookieStore(getContext());
		}
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

}
