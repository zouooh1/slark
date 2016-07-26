package me.zouooh.slark.request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import me.zouooh.slark.DataSource;
import me.zouooh.slark.Logs;
import me.zouooh.slark.NetworkResponse;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.cache.Cachework;
import me.zouooh.slark.http.Network;
import me.zouooh.slark.response.Progress;
import me.zouooh.slark.response.Response;
import me.zouooh.slark.task.Queue;
import me.zouooh.slark.task.Task;

public abstract class Request implements RequestConfig {

    public String getUrl() {
        return url;
    }

    public int getMethod() {
        return method;
    }

    protected void setMethod(int method) {
        this.method = method;
    }

    public Network getNetwork() {
        return network;
    }

    public RequestConfig network(Network network) {
        this.network = network;
        return this;
    }


    public Cachework getCachework() {
        return cachework;
    }

    public RequestConfig cachework(Cachework cachework) {
        this.cachework = cachework;
        return this;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public Progress getProgress() {
        return progress;
    }

    public RequestConfig progress(Progress progress) {
        this.progress = progress;
        return this;
    }

    public Response getResponse() {
        return response;
    }

    public RequestConfig response(Response response) {
        this.response = response;
        return this;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public RequestConfig retryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public RequestConfig param(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public RequestConfig hearder(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
        return this;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public final int getTimeoutMs() {
        if (getRetryPolicy() != null){
            return getRetryPolicy().getCurrentTimeout();
        }
        return DEFAULT_TIMES_OUT;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public HashMap<String,String> getHeaders() {
        return headers;
    }

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
    public static final int DEFAULT_TIMES_OUT = 3000;

    private String url;
    protected URL requestURL;
    private int method;
    private Network network;
    private Cachework cachework;
    private Task task;
    private Queue queue;
    private Progress progress;
    private Response response;
    private RetryPolicy retryPolicy;

    protected HashMap<String, String> params;
    protected HashMap<String, String> headers;
    protected HashMap<String, FormFileItem> files;

    private boolean lock = false;
    private boolean process = false;
    private boolean pause = false;
    private boolean canceled = false;
    private boolean disptachEnd = false;

    public Request(String url) {
        this.url = url;
    }

    public abstract URL makeURL();

    public abstract NetworkResponse adpter(NetworkResponse networkResponse,DataSource dataSource) throws StatusException;

    public URL requestURL() {
        if (requestURL == null) {
            requestURL = makeURL();
        }
        return requestURL;
    }

    public void request() {
        if (url == null) {
            Logs.d("[Pre]url is null.");
        }
        if (getQueue() == null) {
            Logs.d("[Pre]request need run on a quene.");
            return;
        }
        if (process) {
            Logs.d("[Pre]request is processing.");
            return;
        }
        process = true;

        Progress progress = getProgress();

        if (progress != null) {
            progress.onRequestStart(this);
        }

        queue.executeRequest(this);
    }

    public void perform() {
        if (isPause()) {
            return;
        }
        try {
            Object object = loadData();
            disptachState(Task.STATE_SUCCESS, object);
        } catch (Exception e) {
            disptachState(Task.STATE_FAILURE, e);
        }
        disptachState(Task.STATE_END, null);
        process = false;
    }

    public Object loadData() throws IOException, SlarkException {
        Cachework cachework = getCachework();
        DataSource dataSource = DataSource.DISK;
        NetworkResponse networkResponse = null;
        Response response = getResponse();
        if (cachework != null) {
            networkResponse = cachework.open();
        }
        if (networkResponse == null) {
            Network network = getNetwork();
            networkResponse = network.open();
            dataSource = DataSource.NETWORK;
            if (cachework != null) {
                networkResponse = cachework.process(networkResponse);
            }
        }
        if (networkResponse!=null){
            networkResponse = adpter(networkResponse,dataSource);
        }
        Object object = null;
        if (networkResponse != null && response != null) {
            object = response.adpter(this, networkResponse, dataSource);
        }
        return object;
    }

    protected void disptachState(int state, Object object) {
        if (state == Task.STATE_END) {
            if (disptachEnd) {
                return;
            }
            disptachEnd = true;
        }
        if (getTask() != null) {
            getTask().dispatch(state, object);
        }
    }

    public void pause() {
        pause = true;
    }

    public void destroy() {
        if (!isPause()) {
            pause();
        }
        canceled = true;
        progress = null;
        response = null;
        queue = null;
        if (params != null) {
            params.clear();
            params = null;
        }
        if (headers != null) {
            headers.clear();
            headers = null;
        }
        if (cachework != null) {
            cachework.release();
            cachework = null;
        }
        if (network != null) {
            network.close();
            network = null;
        }
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    public byte[] getBody()  {

        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

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

    public boolean hasFile(){
        return files == null&&files.size() > 0;
    }

    public void sendData(DataOutputStream out)
            throws UnsupportedEncodingException, IOException{
    }
}
