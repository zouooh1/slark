package me.zouooh.slark.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

import com.inttus.BurroDebug;
import com.inttus.ants.Network.OnNetworkResponse;
import com.inttus.ants.impl.DefaultRetryPolicy;
import com.inttus.ants.tool.Record;

import me.zouooh.slark.http.Network;
import me.zouooh.slark.response.Progress;
import me.zouooh.slark.response.Response;
import me.zouooh.slark.task.impl.SlarkQueue;
import me.zouooh.slark.task.Task;

public abstract class Request implements OnNetworkResponse{
	
	public interface Method {
		int GET = 0;
		int POST = 1;
		int PUT = 2;
		int DELETE = 3;
		int HEAD = 4;
		int OPTIONS = 5;
		int TRACE = 6;
		int PATCH = 7;
	}
	
	public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
	
	private URL url;
	private int method;
	private Network network;
	private Progress progress;
	private Response<?> response;
	private Task task;
	private SlarkQueue slarkQueue;

	protected HashMap<String, String> params;
	protected HashMap<String, String> headers;

	private boolean ok = false;
	private boolean process = false;
	private boolean pause = false;
	private boolean canceled = false;
	
	private RetryPolicy retryPolicy = new DefaultRetryPolicy();
	
	protected void _disptach(int state, Object object) {
		if (getTask() != null) {
			getTask().dispatch(state, object);
		}
	}

	public void _exe(){
		if (!can()) {
			return;
		}
		_disptach(Task.STATE_START, null);
		try {
			getData();
		} catch (Exception e) {
			_disptach(Task.STATE_FAILURE, e);
		}
		_disptach(Task.STATE_END, null);
		process = false;
	}
	
	public boolean can(){
		return !isPause()&&!isCanceled();
	}
	
	
	public void param(String key,String value){
		getParams().put(key, value);
		ok = false;
	}
	
	public void param(Record record){
		Map<String, Object> ps = record.getMap();
		for (String key : ps.keySet()) {
			getParams().put(key, Castors.me().castToString(ps.get(key)));
		}
		ok = false;
	}
	
	public void removeParam(String key){
		getParams().remove(key);
		ok = false;
	}
	
	public void hearder(String key,String value){
		getHeaders().put(key, value);
		ok = false;
	}
	
	/**
	 * 销毁请求
	 */
	public void destroy() {
		canceled = true;
		progress = null;
		response = null;
		if (getTask() != null) {
			getTask().stop();
		}
	}
	
	/**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
	
	/**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }
	/**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }
    
	protected void getData() throws AntsException,IOException{
		if (network != null && can()) {
			network.performRequest(this);
		}
	}
	public HashMap<String, String> getHeaders() {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		return headers;
	}
	public int getMethod() {
		return method;
	}
	public Network getNetwork() {
		return network;
	}
	public HashMap<String, String> getParams() {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		return params;
	}
	protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }
	public Progress getProgress() {
		return progress;
	}
	public Response<?> getResponse() {
		return response;
	}
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}
	public final int getTimeoutMs() {
        return getRetryPolicy().getCurrentTimeout();
    }
	public String getUrl() {
		if (Strings.isBlank(url)) {
			return null;
		}
		if (!url.startsWith("http")) {
			url = getAntsQueue().getHost()+url;
		}
		return url;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public boolean isOk() {
		return ok;
	}
	public boolean isPause() {
		return pause;
	}
	public boolean isProcess() {
		return process;
	}
	/**
	 * 停止请求
	 */
	public void pause() {
		process = false;
		pause = true;
	}
	
	/**
	 * 异步请求，立即在该队列运行
	 */
	public void request() {
		requestOnAntsQueue(getAntsQueue());
	}
	
	/**
	 * 异步请求，立即在该队列运行
	 */
	public void requestOnAntsQueue(AntsQueue antsQueue) {
		if (antsQueue == null) {
			return;
		}
		if (process) {
			if (BurroDebug.dataEable()) {
				BurroDebug.dataf("%s is exeing cancel", this);
			}
			return;
		}
		process = true;
		_reset();
		if (this.network == null) {
			this.network = antsQueue.getNetwork();
		}
		antsQueue.executeTask(this);
	}
	
	protected void _reset() {
		pause = false;
		canceled = false;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}


	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public void setMethod(int method) {
		this.method = method;
	}
	
	
	 public void setNetwork(Network network) {
		this.network = network;
	}

    public void setOk(boolean ok) {
		this.ok = ok;
	}

    public void setParams(HashMap<String, String> params) {
		this.params = params;
	}


	public void setPause(boolean pause) {
		this.pause = pause;
	}


	public void setProcess(boolean process) {
		this.process = process;
	}
	
	
	public void setProgress(Progress progress) {
		this.progress = progress;
	}


	public void setResponse(Response<?> response) {
		this.response = response;
	}


	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}
	
	
	public void setUrl(String url) {
		ok = false;
		this.url = url;
	}

	/**
	 * 提交一个请求,运行在该队列<br>
	 * 不会立即执行，队列决定运行时机。
	 */
	public void submitOnAntsQueue(AntsQueue antsQueue) {
		antsQueue.submitTask(this);
	}
	
	
	protected  boolean hasResponseBody(int responseCode) {
        return method != Method.HEAD
            && !(HttpStatus.SC_CONTINUE <= responseCode && responseCode < HttpStatus.SC_OK)
            && responseCode != HttpStatus.SC_NO_CONTENT
            && responseCode != HttpStatus.SC_NOT_MODIFIED;
    }

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public AntsQueue getAntsQueue() {
		return antsQueue;
	}
	
	public void setAntsQueue(AntsQueue antsQueue) {
		this.antsQueue = antsQueue;
	}
}
