package me.zouooh.slark.request;

/**
 * Created by zouooh on 2016/7/29.
 */
public interface RequestAspect {
    void  beforeOnBack(Request request);
    void  aftereOnBack(Request request);
}
