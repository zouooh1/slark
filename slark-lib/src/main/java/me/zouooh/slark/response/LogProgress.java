package me.zouooh.slark.response;

import me.zouooh.slark.Logs;
import me.zouooh.slark.Slark;
import me.zouooh.slark.request.Request;

/**
 * Created by zouooh on 2016/7/28.
 */
public class LogProgress implements Progress{

    public static  LogProgress obtain(){
        return  new LogProgress();
    }

    @Override
    public boolean loading() {
        return false;
    }

    @Override
    public void onRequestStart(Request request) {
        if (Slark.DEBUG){
            Logs.d("onRequestStart");
        }
    }

    @Override
    public void onRequestEnd(Request request) {
        if (Slark.DEBUG){
            Logs.d("onRequestEnd");
        }
    }

    @Override
    public void onRequestSucess(Request request) {
        if (Slark.DEBUG){
            Logs.d("onRequestSucess");
        }
    }

    @Override
    public void onRequestFailure(Request request, Throwable throwable) {
        if (Slark.DEBUG){
            Logs.d("onRequestFailure");
            throwable.printStackTrace();
        }
    }

    @Override
    public void onLoading(int index, int current, int total) {

    }

    @Override
    public void tip(String msg) {
        if (Slark.DEBUG){
            Logs.d(msg);
        }
    }
}
