package me.zouooh.slark.task;

import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/28.
 */
public interface QueueAspect {
    Request aspect(Request request);
}
