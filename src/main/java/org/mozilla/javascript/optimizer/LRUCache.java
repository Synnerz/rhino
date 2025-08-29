package org.mozilla.javascript.optimizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<T> {
    private int capacity;
    private LinkedHashMap<Integer, T> map;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<Integer, T>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, T> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public synchronized T get(int key) {
        return map.get(key);
    }

    public synchronized void put(int key, T value) {
        map.put(key, value);
    }

    public synchronized void delete(int key) {
        map.remove(key);
    }
}
