package me.zouooh.slark.request;

import org.nutz.lang.Strings;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.zouooh.slark.DataResponse;
import me.zouooh.slark.Logs;
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

    public RequestAspect getRequestAspect(){
        return  requestAspect;
    }
    public  RequestConfig aspect(RequestAspect requestAspect){
        this.requestAspect = requestAspect;
        return  this;
    }

    public RequestConfig param(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public RequestConfig file(String name, String filePath) {
        return file(name, filePath, IMAGE);
    }

    public RequestConfig file(String name, String filePath, String type) {
        if (Strings.isBlank(filePath) || Strings.isBlank(filePath)) {
            return this;
        }
        if (fileItems == null) {
            fileItems = new LinkedList<>();
        }
        fileItems.add(new FormFileItem(filePath, filePath, type));
        return this;
    }

    public RequestConfig header(String key, String value) {
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

    public boolean isFinish() {
        return finish;
    }

    public final int getTimeoutMs() {
        if (getRetryPolicy() != null) {
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

    public HashMap<String, String> getHeaders() {
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
    public static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO65jyHcd";
    public static final String IMAGE = "image";

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
    private RequestAspect requestAspect;

    protected HashMap<String, String> params;
    protected HashMap<String, String> headers;
    protected List<FormFileItem> fileItems;

    private boolean finish = false;
    private boolean process = false;
    private boolean pause = false;
    private boolean canceled = false;
    private boolean disptachEnd = false;

    public Request(String url) {
        this.url = url;
    }

    public abstract URL makeURL();

    public abstract DataResponse adpter(DataResponse networkResponse) throws SlarkException;

    public URL requestURL() {
        if (requestURL == null) {
            requestURL = makeURL();
        }
        return requestURL;
    }

    public void request() {
        if (url == null) {
            Logs.d("url is null.");
        }
        if (getQueue() == null) {
            Logs.d("request need run on a quene.");
            return;
        }
        if (process) {
            Logs.d("request is processing.");
            return;
        }
        if (finish) {
            Logs.d("request is finished.");
            return;
        }
        process = true;
        finish = false;

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

        RequestAspect requestAspect = getRequestAspect();

        if (requestAspect!=null){
            requestAspect.beforeOnBack(this);
        }

        try {
            Object object = loadData();
            disptachState(Task.STATE_SUCCESS, object);
        } catch (Exception e) {
            disptachState(Task.STATE_FAILURE, e);
        }

        if (requestAspect!=null){
            requestAspect.afterOnBack(this);
        }

        Network network = getNetwork();
        if (network != null) {
            network.close();
        }
        disptachState(Task.STATE_END, null);
        process = false;
        finish = true;
    }

    public Object loadData() throws SlarkException, IOException {
        Cachework cachework = getCachework();
        DataResponse networkResponse = null;
        Response response = getResponse();

        if (cachework != null) {
            networkResponse = cachework.open();
        }
        if (networkResponse == null) {
            Network network = getNetwork();
            try {
                networkResponse = network.open();
            } catch (SlarkException e) {
                if (cachework != null) {
                    networkResponse = cachework.open(e);
                }
            }
            if (networkResponse != null) {
                RequestAspect requestAspect = getRequestAspect();
                if (requestAspect!=null){
                    requestAspect.aspect(this,networkResponse);
                }
                networkResponse = adpter(networkResponse);
            }
            if (networkResponse != null && cachework != null) {
                networkResponse = cachework.process(networkResponse);
                if (network != null) {
                    network.close();
                }
            }
        }
        Object object = null;
        if (networkResponse != null && response != null) {
            object = response.adpter(this, networkResponse);
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

    public void resume() {
        pause = false;
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

    public byte[] getBody() {

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

    public boolean hasFile() {
        return fileItems == null && fileItems.size() > 0;
    }

    public void sendData(DataOutputStream out) throws IOException {
    }
}
