package me.zouooh.slark.s;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
        Slark.with(getApplication()).get("http://www.seqi360.com/app/gift").param("a", "b").progress(LogProgress
                .obtain()).response(new TextResponse()).request();
    }
}
