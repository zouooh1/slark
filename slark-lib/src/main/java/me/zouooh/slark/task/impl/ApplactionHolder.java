package me.zouooh.slark.task.impl;

import android.app.Application;
import android.content.Context;

import me.zouooh.slark.task.ContextHolder;

/**
 * Created by zouooh on 2016/7/26.
 */
public class ApplactionHolder implements ContextHolder {

    private Application application;

    public ApplactionHolder(Application application) {
        this.application = application;
    }

    @Override
    public Object holder() {
        return application;
    }

    @Override
    public Context context() {
        return this.application;
    }

    @Override
    public boolean canDispatch() {
        return true;
    }

    @Override
    public void release() {
        this.application = null;
    }
}
