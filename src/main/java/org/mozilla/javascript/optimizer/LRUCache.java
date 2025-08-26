package org.mozilla.javascript.optimizer;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class LRUCache {
    private int capacity;
    private LinkedHashMap<Integer, OptRuntime.LookupCache> map;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true);
    }

    public OptRuntime.LookupCache get(int key) {
        return this.map.get(key);
    }

    public void put(int key, OptRuntime.LookupCache value) {
        if (!this.map.containsKey(key) && this.map.size() == this.capacity) {
            Iterator<Integer> it = this.map.keySet().iterator();
            it.next();
            it.remove();
        }
        this.map.put(key, value);
    }
}
