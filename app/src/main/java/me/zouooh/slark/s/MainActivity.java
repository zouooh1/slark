package me.zouooh.slark.s;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.zouooh.slark.Slark;
import me.zouooh.slark.request.Request;
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
        Slark.with(getApplication()).get("http://www.seqi360.com/app/tb/topic/bar?topic_bar_id=15").param("a", "b")
                .progress(LogProgress
                .obtain()).response(new TextResponse() {
            @Override
            public void onRequestSuccess(Request request, String s) {
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
            }
        }).request();
        Slark.with(getApplication()).post("http://192.168.22.99/app/login").progress(LogProgress.obtain()).response
                (new TextResponse()).request();

    }
}
