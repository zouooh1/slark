package me.zouooh.slark.task.impl;

import android.app.Activity;
import android.content.Context;

import me.zouooh.slark.task.ContextHolder;

/**
 * Created by zouooh on 2016/7/26.
 */
public class ActivityHolder implements ContextHolder {

    private Activity activity;

    public ActivityHolder(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Object holder() {
        return activity;
    }

    @Override
    public Context context() {
        return this.activity;
    }

    @Override
    public boolean canDispatch() {
        if (this.activity == null)
            return false;
        if (this.activity.isFinishing()) {
            return false;
        }
        return true;
    }

}
