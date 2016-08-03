package me.zouooh.slark;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import me.zouooh.slark.biuld.SlarkClient;
import me.zouooh.slark.biuld.SlarkClientImpl;
import me.zouooh.slark.task.QueueAspect;
import me.zouooh.slark.task.CacheworkFactory;
import me.zouooh.slark.task.ContextHolder;
import me.zouooh.slark.task.NetworkFactory;
import me.zouooh.slark.task.Queue;
import me.zouooh.slark.task.RequestAspectFactory;
import me.zouooh.slark.task.TaskFactory;
import me.zouooh.slark.task.impl.ActivityHolder;
import me.zouooh.slark.task.impl.ApplactionHolder;
import me.zouooh.slark.task.impl.FragmentHolder;
import me.zouooh.slark.task.impl.SlarkQueue;

/**
 * Created by zouooh on 2016/7/26.
 */
public final class Slark {

    public static boolean DEBUG = false;
    public static boolean DEBUG_DATAS = false;
    public static final String DATAS_HOST = "datas";
    public static final String FILE_HOST = "files";

    private static HashMap<String, String> hosts;
    private static HashMap<Object, Queue> queues;
    private static SlarkClient slarkClient;
    private static CacheworkFactory cacheworkFactory;
    private static NetworkFactory networkFactory;
    private static TaskFactory taskFactory;
    private static ExecutorService executorService;
    private static QueueAspect aspect;
    private static RequestAspectFactory requestAspectFactory;

    private Slark() {
    }

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
        Slark.aspect = slarkClient.aspect();
        Slark.requestAspectFactory = slarkClient.requestAspectFactory();
    }

    public static Queue with(Application application) {
        Queue queue = queues.get(application);
        if (queue == null) {
            ContextHolder contextHolder = new ApplactionHolder(application);
            queue = create(contextHolder);
            queues.put(application, queue);
        }
        return queue;
    }

    public static Queue with(Activity activity) {
        Queue queue = queues.get(activity);
        if (queue == null) {
            ContextHolder contextHolder = new ActivityHolder(activity);
            queue = create(contextHolder);
            queues.put(activity, queue);
        }
        return queue;
    }

    public static Queue with(Fragment fragment) {
        Queue queue = queues.get(fragment);
        if (queue == null) {
            ContextHolder contextHolder = new FragmentHolder(fragment);
            queue = create(contextHolder);
            queues.put(fragment, queue);
        }
        return queue;
    }

    private static Queue create(ContextHolder contextHolder) {
        return new SlarkQueue(contextHolder, Slark.executorService, Slark.taskFactory, Slark.networkFactory, Slark
                .cacheworkFactory, Slark.aspect, Slark.requestAspectFactory);
    }

    public static void destroy(Object object) {
        if (object != null) {
            Queue queue = queues.remove(object);
            if (queue != null) {
                queue.destroy();
            }
        }
    }

    public static String urlWithHost(String path) {
        return urlWithHost(DATAS_HOST, path);
    }

    public static String urlWithHost(String hostName, String path) {
        String host = hostOf(hostName);
        return host + path;
    }

    public static String hostOf(String name) {
        if (hosts == null)
            return "";
        return hosts.get(name);
    }

    public static void host(String name,String host){
        hosts.put(name,host);
    }
}
