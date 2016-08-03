package me.zouooh.slark.task;

import me.zouooh.slark.request.Request;
import me.zouooh.slark.request.RequestConfig;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface Queue extends Task.TaskLisnter{
    ContextHolder contextHolder();
    RequestConfig g(String path);
    RequestConfig g(String hostname,String path);
    RequestConfig p(String path);
    RequestConfig p(String hostname, String path);
    RequestConfig get(String url);
    RequestConfig post(String url);
    Request fillRequest(Request request);
    QueueAspect queueAspect();
    void destory(Request request);
    void submitRequest(Request request);
    void executeRequest(Request request);
    void resume();
    void pause();
    void destroy();
}
