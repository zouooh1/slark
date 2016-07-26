package me.zouooh.slark;

import android.content.Context;
import android.webkit.URLUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import me.zouooh.slark.biuld.SlarkClient;
import me.zouooh.slark.biuld.SlarkClientImpl;

/**
 * Created by zouooh on 2016/7/26.
 */
public final class Slark {

    public  static  boolean DEBUG = false;
    public  static  boolean DEBUG_DATAS = false;
    public  static  String DATAS_HOST = "datas";

    private static HashMap<String,String> hosts;
    private static SlarkClient slarkClient;

    private  Slark(){};

    private File cacheBase;

    public  static void init(Context context){
        init(context,new SlarkClientImpl());
    }

    public  static void init(Context context,SlarkClient slarkClient){
        Slark.slarkClient = slarkClient;
    }

    public  static  URL urlWithHost(String path){
        return  urlWithHost(DATAS_HOST,path);
    }

    public  static  URL urlWithHost(String hostName, String path){
        String host = hostOf(hostName);
        try {
            return  new URL(host+path);
        } catch (MalformedURLException e) {
        }
        return null;
    }

    public  static  String hostOf(String name){
        if (hosts == null)
            return "";
        return  hosts.get(name);
    }
}
