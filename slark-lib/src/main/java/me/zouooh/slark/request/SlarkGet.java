package me.zouooh.slark.request;

import java.net.MalformedURLException;
import java.net.URL;

import me.zouooh.slark.DataSource;
import me.zouooh.slark.NetworkResponse;
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
        try {
            return new URL(getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NetworkResponse adpter(NetworkResponse networkResponse, DataSource dataSource) throws SlarkException {
        if (networkResponse.statusCode == HttpStatus.SC_OK)
            return  networkResponse;
        return null;
    }
}
