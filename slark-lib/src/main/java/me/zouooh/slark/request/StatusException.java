package me.zouooh.slark.request;

import me.zouooh.slark.SlarkException;

/**
 * Created by zouooh on 2016/7/26.
 */
public class StatusException extends SlarkException{
    private int status;

    public StatusException(int status){
        this.status = status;
    }
    public int getStatus(){
        return  this.status;
    }
}
