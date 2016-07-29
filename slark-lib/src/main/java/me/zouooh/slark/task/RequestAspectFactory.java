package me.zouooh.slark.task;

import me.zouooh.slark.http.Network;
import me.zouooh.slark.request.Request;
import me.zouooh.slark.request.RequestAspect;

/**
 * Created by zouooh on 2016/7/29.
 */
public interface RequestAspectFactory {
    RequestAspect buildRequestAspect(Request request);
}
