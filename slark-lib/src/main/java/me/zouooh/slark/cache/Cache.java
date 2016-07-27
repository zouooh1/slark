package me.zouooh.slark.cache;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public interface Cache {
    Entry get(String key);
    void put(String key, Entry entry);
    void initialize();
    void invalidate(String key, boolean fullExpire);
    void remove(String key);
    void clear();
    class Entry {
        public File data;
        public String etag;
        public long serverDate;
        public long lastModified;
        public long ttl;
        public long softTtl;
        public Map<String, String> responseHeaders = Collections.emptyMap();
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }
        public boolean refreshNeeded() {
            return this.softTtl < System.currentTimeMillis();
        }
    }
}
