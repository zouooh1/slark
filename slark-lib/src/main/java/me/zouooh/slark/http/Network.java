package me.zouooh.slark.http;

import me.zouooh.slark.DataResponse;
import me.zouooh.slark.SlarkException;

public interface Network {
	DataResponse open() throws SlarkException;
	void close();
}