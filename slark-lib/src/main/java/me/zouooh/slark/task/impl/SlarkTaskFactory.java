package me.zouooh.slark.task.impl;

import me.zouooh.slark.request.Request;
import me.zouooh.slark.task.Task;
import me.zouooh.slark.task.TaskFactory;

/**
 * Created by zouooh on 2016/7/26.
 */
public class SlarkTaskFactory implements TaskFactory{
    @Override
    public Task buildTask(Request request) {
        Task.TaskLisnter taskLisnter = request.getQueue();
        return new SlarkTask(request,taskLisnter);
    }
}
