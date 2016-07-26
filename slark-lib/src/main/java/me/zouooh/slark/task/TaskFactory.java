package me.zouooh.slark.task;

import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface TaskFactory {
    Task buildTask(Request request);
}
