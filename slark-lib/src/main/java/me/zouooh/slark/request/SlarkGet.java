package me.zouooh.slark.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import me.zouooh.slark.DataSource;
import me.zouooh.slark.DataResponse;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.http.HttpStatus;

/**
 * Created by zouooh on 2016/7/26.
 */
public class SlarkGet extends Request{
    public SlarkGet(String url) {
        super(url);
        setMethod(Method.GET);
    }

    @Override
    public URL makeURL() {
        String urlInner = getUrl();
        String ps = "";
        if (params!=null&&params.size()>0){
            for (String key : params.keySet()) {
                try {
                    ps += "&"
                            + key
                            + "="
                            + URLEncoder.encode(params.get(key),
                            getParamsEncoding());
                } catch (UnsupportedEncodingException e) {
                    ps += "&" + key + "=" + params.get(key);
                }
            }
            if (urlInner.contains("?")) {
                urlInner = urlInner + ps;
            } else {
                urlInner = urlInner + "?" + ps.replaceFirst("&", "");
            }
        }
        try {
            return new URL(urlInner);
        } catch (MalformedURLException e) {
            Log.d("bad url %s",urlInner);
        }
        return null;
    }

    @Override
    public DataResponse adpter(DataResponse networkResponse) throws SlarkException {
        if (networkResponse.statusCode == HttpStatus.SC_OK)
            return  networkResponse;
        return null;
    }
}
