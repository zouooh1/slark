package me.zouooh.slark.biuld;

import java.io.File;
import java.util.concurrent.ExecutorService;

import me.zouooh.slark.task.QueueAspect;
import me.zouooh.slark.task.CacheworkFactory;
import me.zouooh.slark.task.NetworkFactory;
import me.zouooh.slark.task.RequestAspectFactory;
import me.zouooh.slark.task.TaskFactory;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface SlarkClient {
    NetworkFactory networkFactory();
    CacheworkFactory cacheworkFactory();
    TaskFactory taskFactory();
    ExecutorService executorService();
    QueueAspect aspect();
    RequestAspectFactory requestAspectFactory();
}
