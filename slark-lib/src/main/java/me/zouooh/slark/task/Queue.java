package me.zouooh.slark.task;

import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface Queue extends Task.TaskLisnter{
    ContextHolder contextHolder();
    Request g(String path);
    Request g(String hostname,String path);
    Request p(String path);
    Request p(String hostname,String path);
    Request get(String url);
    Request post(String url);
    Request fillRequest(Request request);
    void destory(Request request);
    void submitRequest(Request request);
    void executeRequest(Request request);
    void resume();
    void pause();
    void destroy();
}
