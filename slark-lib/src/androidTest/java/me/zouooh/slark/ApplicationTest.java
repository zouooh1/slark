package me.zouooh.slark;

import android.app.Application;
import android.test.ApplicationTestCase;

import me.zouooh.slark.response.LogProgress;
import me.zouooh.slark.response.TextResponse;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Logs.d("test","test");
        Slark.DEBUG_DATAS = true;
        Slark.DEBUG = true;
        Slark.init(getApplication());
        Slark.with(getApplication()).get("http://www.seqi360.com/app/gift").progress(LogProgress.obtain()).response(new TextResponse()).request();
    }
}