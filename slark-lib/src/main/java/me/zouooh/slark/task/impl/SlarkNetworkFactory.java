package me.zouooh.slark.task.impl;

import javax.net.ssl.SSLSocketFactory;

import me.zouooh.slark.http.Network;
import me.zouooh.slark.http.UrlNetwork;
import me.zouooh.slark.request.Request;
import me.zouooh.slark.task.NetworkFactory;

/**
 * Created by zouooh on 2016/7/26.
 */
public class SlarkNetworkFactory implements NetworkFactory{

    private SSLSocketFactory sslSocketFactory;

    public  SlarkNetworkFactory(){
    }
    public  SlarkNetworkFactory(SSLSocketFactory sslSocketFactory){
        this.sslSocketFactory = sslSocketFactory;
    }
    @Override
    public Network buildNetwork(Request request) {
        UrlNetwork network = new UrlNetwork(request);
        network.setSslSocketFactory(sslSocketFactory);
        return network;
    }
}
