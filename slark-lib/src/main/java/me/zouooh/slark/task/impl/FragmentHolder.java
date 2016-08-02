package me.zouooh.slark.task.impl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import me.zouooh.slark.task.ContextHolder;

/**
 * Created by zouooh on 2016/7/27.
 */
public class FragmentHolder implements ContextHolder{

    private Fragment fragment;

    public FragmentHolder(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public Object holder() {
        return fragment;
    }

    @Override
    public Context context() {
        return fragment.getActivity();
    }

    @Override
    public boolean canDispatch() {
        if (fragment == null){
            return false;
        }
        if (fragment.isDetached()||fragment.isRemoving()){
            return false;
        }
        return true;
    }

    @Override
    public void release() {
        fragment = null;
    }
}
