package server.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class ServerMemoryCache {

	private static final ServerMemoryCache instance = createCache(10000, 10, 1000000);
	private Cache<Long, Object> cache;

    public Object get(long keyHash) {
        return cache.getIfPresent(keyHash);
    }

    public void put(long keyHash, Object obj) {
        cache.put(keyHash, obj);
    }

    public static ServerMemoryCache getInstance() {
    	return instance;
    }

	private static synchronized ServerMemoryCache createCache(int concurrencyLevel, int expiration, int size) {
		return new ServerMemoryCache(concurrencyLevel, expiration, size);
	}

	private ServerMemoryCache(int concurrencyLevel, int expiration, int size) {
		cache = CacheBuilder.newBuilder().concurrencyLevel(concurrencyLevel).maximumSize(size).softValues()
	            .expireAfterWrite(expiration, TimeUnit.MINUTES).build();
	}

}
