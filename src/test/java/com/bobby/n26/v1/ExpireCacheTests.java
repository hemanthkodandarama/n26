package com.bobby.n26.v1;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class ExpireCacheTests {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ConcurrentMap<Integer, String> concurrentMap;
    private Cache<Integer, String> cache;
    private int ttl = 1000;

    @Before
    public void setUp() {
        cache = CacheBuilder
            .newBuilder().expireAfterWrite(ttl, TimeUnit.MILLISECONDS).build();
        concurrentMap = cache.asMap();
    }

    @Test
    public void shouldRemoveElementWhenDueTime() throws InterruptedException {
        // when
        concurrentMap.put(10, "Bob");
        Thread.sleep(ttl + 500);  // wait for the element to be removed

        // then
        assertThat(concurrentMap.containsKey(10), is(false));
        assertThat(concurrentMap.containsValue("Bob"), is(false));
    }

    @Test
    public void shouldKeepElementWhenItsNotTheTimeYet() throws InterruptedException {
        // when
        concurrentMap.put(10, "Bob");

        // then
        assertThat(concurrentMap.containsKey(10), is(true));
        assertThat(concurrentMap.containsValue("Bob"), is(true));
    }

    @Test
    public void shouldBeIterable() throws InterruptedException {
        // when
        concurrentMap.put(1, "Java");
        concurrentMap.put(20, ".NET");
        concurrentMap.put(30, "Node.js");
        concurrentMap.put(40, "PHP");

        for (Map.Entry<Integer,String> entry : concurrentMap.entrySet()){
            log.debug("Key: {} , Value: {}",entry.getKey(), entry.getValue());
        }

    }

    @Test
    public void cacheChangesAffectMap() throws InterruptedException {
        // when
        concurrentMap.put(1, "Java");
        concurrentMap.put(20, ".NET");
        concurrentMap.put(30, "Node.js");
        concurrentMap.put(40, "PHP");

        cache.put(50,"Python");

        for (Map.Entry<Integer,String> entry : concurrentMap.entrySet()){
            log.debug("Key: {} , Value: {}",entry.getKey(), entry.getValue());
        }

    }

    @Test
    public void mapChangesAffectCache() throws InterruptedException {
        // when
        concurrentMap.put(1, "Java");

        // then
        assertThat(cache.getIfPresent(1), is("Java"));

    }

}
