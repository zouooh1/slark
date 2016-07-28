package me.zouooh.slark.cache;

import me.zouooh.slark.DataResponse;
import me.zouooh.slark.SlarkException;

/**
 * Created by zouooh on 2016/7/26.
 */
public interface Cachework {
    DataResponse open();
    DataResponse open(SlarkException slarkException);
    DataResponse process(DataResponse networkResponse);
    void release();
}
