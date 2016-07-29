package me.zouooh.slark.task.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import me.zouooh.slark.Logs;
import me.zouooh.slark.Slark;
import me.zouooh.slark.request.Request;
import me.zouooh.slark.request.RequestAspect;
import me.zouooh.slark.request.SlarkGet;
import me.zouooh.slark.request.SlarkPost;
import me.zouooh.slark.task.QueueAspect;
import me.zouooh.slark.task.CacheworkFactory;
import me.zouooh.slark.task.ContextHolder;
import me.zouooh.slark.task.NetworkFactory;
import me.zouooh.slark.task.Queue;
import me.zouooh.slark.task.RequestAspectFactory;
import me.zouooh.slark.task.Task;
import me.zouooh.slark.task.TaskFactory;

/**
 * 请求队列
 *
 * @author zouooh
 */
public class SlarkQueue implements Queue {
    protected ContextHolder contextHolder;
    protected ExecutorService executorService;
    protected TaskFactory taskFactory;
    protected NetworkFactory networkFactory;
    protected CacheworkFactory cacheworkFactory;
    protected QueueAspect aspect;
    protected RequestAspectFactory requestAspectFactory;
    private List<Request> requests = new LinkedList<>();
    private boolean resume = false;

    public SlarkQueue(ContextHolder contextHolder, ExecutorService executorService, TaskFactory taskFactory,
                      NetworkFactory networkFactory, CacheworkFactory cacheworkFactory, QueueAspect aspect,
                      RequestAspectFactory requestAspectFactory) {
        this.contextHolder = contextHolder;
        this.executorService = executorService;
        this.taskFactory = taskFactory;
        this.networkFactory = networkFactory;
        this.cacheworkFactory = cacheworkFactory;
        this.aspect = aspect;
        this.requestAspectFactory = requestAspectFactory;
    }

    @Override
    public ContextHolder contextHolder() {
        return contextHolder;
    }

    @Override
    public Request g(String path) {
        return get(Slark.urlWithHost(path));
    }

    @Override
    public Request g(String hostname, String path) {
        return get(Slark.urlWithHost(hostname, path));
    }

    @Override
    public Request p(String path) {
        return post(Slark.urlWithHost(path));
    }

    @Override
    public Request p(String hostname, String path) {
        return post(Slark.urlWithHost(hostname, path));
    }

    @Override
    public Request get(String url) {
        SlarkGet slarkGet = new SlarkGet(url);
        return fillRequest(slarkGet);
    }

    @Override
    public Request post(String url) {
        SlarkPost slarkGet = new SlarkPost(url);
        return fillRequest(slarkGet);
    }

    @Override
    public Request fillRequest(Request request) {
        if (request == null) {
            return null;
        }
        request.setQueue(this);
        if (cacheworkFactory != null) {
            request.cachework(cacheworkFactory.buildCachework(request));
        }
        if (networkFactory != null) {
            request.network(networkFactory.buildNetwork(request));
        }
        if (requestAspectFactory != null){
            request.aspect(requestAspectFactory.buildRequestAspect(request));
        }
        if (aspect != null) {
            request = aspect.aspect(request);
        }
        return request;
    }

    public void executeRequest(Request request) {
        if (this.executorService == null || request == null) {
            return;
        }
        submitTask(request, false);
        executeTaskBack(request);
    }


    private void executeTaskBack(Request request) {
        if (this.executorService == null || request == null || taskFactory == null) {
            return;
        }
        Task task = taskFactory.buildTask(request);
        request.setTask(task);
        task.executeOnExecutor(executorService);
    }

    @Override
    public void submitRequest(Request request) {
        submitTask(request, true);
    }

    private void submitTask(Request request, boolean b) {
        synchronized (this) {
            if (requests == null) {
                return;
            }
            if (!requests.contains(request)) {
                requests.add(request);
            }
            if (b) {
                if (resume) {
                    request.request();
                }
            }
        }
    }

    @Override
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

    @Override
    public void resume() {
        synchronized (this) {
            resume = true;
            for (Request request : requests) {
                request.resume();
                request.request();
            }
        }
    }

    @Override
    public void pause() {
        synchronized (this) {
            resume = false;
            for (Request antsRequest : requests) {
                antsRequest.pause();
            }
        }
    }

    @Override
    public void destroy() {
        synchronized (this) {
            if (contextHolder != null) {
                contextHolder.release();
                contextHolder = null;
            }
            executorService = null;
            if (requests == null) {
                return;
            }
            for (Request request : requests) {
                request.destroy();
            }
            requests.clear();
            requests = null;
        }
    }

    @Override
    public void onStart(Request request) {
    }

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
                if (request.isCanceled() || request.isFinish()) {
                    requests.remove(request);
                    Logs.d("%s removed", request.requestURL());
                } else {
                    Logs.d("%s stay in.", request.requestURL());
                }
            }
        }
    }
}
