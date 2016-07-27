package me.zouooh.slark.task;

import android.content.Context;

import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface ContextHolder {
    Object holder();
    Context context();
    boolean canDispatch();

    Request get(String path);
    Request post(String path);

    void resume();
    void pause();
    void destroy();
}
