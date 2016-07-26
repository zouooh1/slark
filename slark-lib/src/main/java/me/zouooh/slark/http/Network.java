package me.zouooh.slark.http;

import me.zouooh.slark.NetworkResponse;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.request.Request;

public interface Network {
	NetworkResponse open() throws SlarkException;
	void close();
}