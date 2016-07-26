package me.zouooh.slark.task;

import me.zouooh.slark.cache.Cachework;
import me.zouooh.slark.http.Network;
import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface Queue {
    Request get(Sting path);
    Request g(Sting path);
    Request post(Sting path);
    Request p(Sting path);

    Network buildNetwork(Request request);
    Cachework buildCachework(Request request);

    void resume();
    void pause();
    void destroy();
}
