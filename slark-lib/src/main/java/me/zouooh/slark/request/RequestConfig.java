package me.zouooh.slark.request;

import me.zouooh.slark.cache.Cachework;
import me.zouooh.slark.http.Network;
import me.zouooh.slark.response.Progress;
import me.zouooh.slark.response.Response;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface RequestConfig {
    RequestConfig param(String name,String value);
    RequestConfig header(String name,String value);
    RequestConfig network(Network network);
    RequestConfig cachework(Cachework cachework);
    RequestConfig retryPolicy(RetryPolicy retryPolicy);
    RequestConfig progress(Progress progress);
    RequestConfig response(Response response);
    void  request();
}
