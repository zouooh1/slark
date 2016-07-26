package me.zouooh.slark.task.impl;

import android.os.AsyncTask;


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import me.zouooh.slark.request.Request;
import me.zouooh.slark.response.Progress;
import me.zouooh.slark.response.Response;
import me.zouooh.slark.task.ContextHolder;
import me.zouooh.slark.task.Queue;
import me.zouooh.slark.task.Task;

/**
 * 异步任务
 *
 * @author zouooh
 */
public class SlarkTask extends AsyncTask<String, Object, Object> implements Task {

    private Request request;
    private Task.TaskLisnter taskLisnter;

    public SlarkTask(Request antsRequest, TaskLisnter taskLisnter) {
        this.request = antsRequest;
        this.taskLisnter = taskLisnter;
    }

    @Override
    public void stop() {
        request = null;
        cancel(true);
    }

    @Override
    protected Object doInBackground(String... arg0) {
        if (request == null) {
            return null;
        }
        if (taskLisnter != null) {
            taskLisnter.onStart(request);
        }
        request.perform();
        if (taskLisnter != null) {
            taskLisnter.onEnd(request);
        }
        taskLisnter = null;
        return null;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if (request == null) {
            return;
        }
        Response response = request.getResponse();
        Progress progress = request.getProgress();
        int update = Integer.valueOf(String.valueOf(values[0]));
        switch (update) {
            case STATE_SUCCESS:
                if (response != null) {
                    try {
                        response.onRequestSuccess(request, values[1]);
                    } catch (Exception e) {
                        response.onRequestFailure(request, e);
                        if (progress != null) {
                            progress.onRequestFailure(request, e);
                        }
                    }
                }
                if (progress != null) {
                    progress.onRequestSucess(request);
                }
                break;
            case STATE_FAILURE:
                if (response != null) {
                    response.onRequestFailure(request, (Throwable) values[1]);
                }
                if (progress != null) {
                    progress.onRequestFailure(request, (Throwable) values[1]);
                }
                break;
            case STATE_END:
                if (progress != null) {
                    progress.onRequestEnd(request);
                }
                break;
            case STATE_LOADING:
                if (progress != null) {
                    int[] a = (int[]) values[1];
                    progress.onLoading(a[0], a[1], a[2]);
                }
                break;
            default:
                break;
        }
        super.onProgressUpdate(values);
    }

    @Override
    public void dispatch(int state, Object object) {
        publishProgress(state, object);
    }

    @Override
    public void executeOnExecutor(Executor executor) {
        if (executor == null) {
            execute();
            return;
        }
        super.executeOnExecutor(executor);
    }
}
