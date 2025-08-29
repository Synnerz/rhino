package org.mozilla.javascript.optimizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<T> {
    private int capacity;
    private LinkedHashMap<Integer, LRUCacheEntry<T>> map;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<Integer, LRUCacheEntry<T>>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, LRUCacheEntry<T>> eldest) {
                if (size() <= LRUCache.this.capacity) return false;

                int k = -1;
                int leastHits = 99999999;

                // Technically this impl is flawed in the sense that it will not reset the hottest cache
                // which means that it will never remove those over time even if they're no longer used
                // it _should_ still be fine to use this impl as a test for such caching solution
                for (Map.Entry<Integer, LRUCacheEntry<T>> e : map.entrySet()) {
                    LRUCacheEntry<T> val = e.getValue();
                    if (val.hits < leastHits) {
                        k = e.getKey();
                        leastHits = val.hits;
                    }
                }

                if (k != -1) {
                    LRUCache.this.map.remove(k);
                }

                // Always returns false because we're the ones that should handle this above
                return false;
            }
        };
    }

    public synchronized T get(int key) {
        LRUCacheEntry<T> cache = map.get(key);

        if (cache != null) {
            cache.hits++;
            return cache.value;
        }

        return null;
    }

    public synchronized void put(int key, T value) {
        map.put(key, new LRUCacheEntry<>(value));
    }

    public synchronized void delete(int key) {
        map.remove(key);
    }

    private static class LRUCacheEntry<T> {
        final T value;
        int hits = 1;

        LRUCacheEntry(T value) {
            this.value = value;
        }
    }
}
