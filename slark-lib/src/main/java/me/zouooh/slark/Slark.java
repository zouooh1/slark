package me.zouooh.slark;

import android.app.Activity;
import android.content.Context;
import android.webkit.URLUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.zouooh.slark.biuld.SlarkClient;
import me.zouooh.slark.biuld.SlarkClientImpl;
import me.zouooh.slark.task.CacheworkFactory;
import me.zouooh.slark.task.ContextHolder;
import me.zouooh.slark.task.NetworkFactory;
import me.zouooh.slark.task.Queue;
import me.zouooh.slark.task.TaskFactory;
import me.zouooh.slark.task.impl.ActivityHolder;
import me.zouooh.slark.task.impl.SlarkQueue;

/**
 * Created by zouooh on 2016/7/26.
 */
public final class Slark {

    public static boolean DEBUG = false;
    public static boolean DEBUG_DATAS = false;
    public static String DATAS_HOST = "datas";

    private static HashMap<String, String> hosts;
    private static HashMap<Object, Queue> queues;
    private static SlarkClient slarkClient;
    private static CacheworkFactory cacheworkFactory;
    private static NetworkFactory networkFactory;
    private static TaskFactory taskFactory;
    private static ExecutorService executorService;

    private Slark() {
    }

    ;

    private File cacheBase;

    public static void init(Context context) {
        init(context, new SlarkClientImpl(context));
    }

    public static void init(Context context, SlarkClient slarkClient) {
        hosts = new HashMap<>();
        queues = new HashMap<>();
        Slark.slarkClient = slarkClient;
        Slark.cacheworkFactory = slarkClient.cacheworkFactory();
        Slark.networkFactory = slarkClient.networkFactory();
        Slark.taskFactory = slarkClient.taskFactory();
        Slark.executorService = slarkClient.executorService();
    }

    public static Queue with(Activity activity) {
        Queue queue = queues.get(activity);
        if (queue == null) {
            ContextHolder contextHolder = new ActivityHolder(activity);
            queue = new SlarkQueue(contextHolder, Slark.executorService, Slark.taskFactory, Slark.networkFactory, Slark.cacheworkFactory);
            queues.put(activity, queue);
        }
        return queue;
    }

    public static void destroy(Object object) {
        if (object != null) {
            Queue queue = queues.remove(object);
            if (queue!=null){
                queue.destroy();
            }
        }
    }

    public static URL urlWithHost(String path) {
        return urlWithHost(DATAS_HOST, path);
    }

    public static URL urlWithHost(String hostName, String path) {
        String host = hostOf(hostName);
        try {
            return new URL(host + path);
        } catch (MalformedURLException e) {
        }
        return null;
    }

    public static String hostOf(String name) {
        if (hosts == null)
            return "";
        return hosts.get(name);
    }
}
