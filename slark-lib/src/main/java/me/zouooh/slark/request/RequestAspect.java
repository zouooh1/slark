package me.zouooh.slark.request;

import me.zouooh.slark.DataResponse;

/**
 * Created by zouooh on 2016/7/29.
 */
public interface RequestAspect {
    void  beforeOnBack(Request request);
    void  afterOnBack(Request request);
    void aspect(Request request,DataResponse networkResponse);
}
