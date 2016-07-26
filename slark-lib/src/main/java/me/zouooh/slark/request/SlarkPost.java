package me.zouooh.slark.request;

/**
 * Created by zouooh on 2016/7/26.
 */
public abstract class SlarkPost extends Request{
    public SlarkPost(String url) {
        super(url);
        setMethod(Method.POST);
    }
}
