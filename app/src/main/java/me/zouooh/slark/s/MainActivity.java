package me.zouooh.slark.s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.zouooh.slark.Slark;
import me.zouooh.slark.response.LogProgress;
import me.zouooh.slark.response.TextResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Slark.DEBUG_DATAS = true;
        Slark.DEBUG = true;
        Slark.init(getApplication());
        Slark.with(getApplication()).get("http://www.seqi360.com/app/gift").progress(LogProgress.obtain()).response(new TextResponse()).request();
    }
}
