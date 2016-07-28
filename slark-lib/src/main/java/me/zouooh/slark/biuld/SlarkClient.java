package me.zouooh.slark.biuld;

import java.io.File;
import java.util.concurrent.ExecutorService;

import me.zouooh.slark.task.Aspect;
import me.zouooh.slark.task.CacheworkFactory;
import me.zouooh.slark.task.NetworkFactory;
import me.zouooh.slark.task.TaskFactory;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface SlarkClient {
    File base();
    NetworkFactory networkFactory();
    CacheworkFactory cacheworkFactory();
    TaskFactory taskFactory();
    ExecutorService executorService();
    Aspect aspect();
}
