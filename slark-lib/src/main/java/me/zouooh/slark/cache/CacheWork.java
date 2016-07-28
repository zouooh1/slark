package me.zouooh.slark.cache;

import me.zouooh.slark.NetworkResponse;
import me.zouooh.slark.SlarkException;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface Cachework {
    NetworkResponse open();
    NetworkResponse open(SlarkException slarkException);
    NetworkResponse process(NetworkResponse networkResponse);
    void release();
}
